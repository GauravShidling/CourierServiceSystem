import dao.AgentDAO;
import dao.AssignmentDAO;
import dao.DriverDAO;
import dao.ShipmentDAO;
import dao.StatusLogDAO;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.Agent;
import model.DriverAssignment;
import model.Shipment;
import model.StatusLog;
import service.StatusLogService;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShipmentDAO shipmentDAO = new ShipmentDAO();
        AssignmentDAO assignmentDAO = new AssignmentDAO();
        StatusLogDAO statusLogDAO = new StatusLogDAO();
        DriverDAO driverDAO = new DriverDAO();

        while (true) {
            System.out.println("\n=== Courier Service Management System ===");
            System.out.println("1. Create New Shipment");
            System.out.println("2. View Shipment Status & Location Log");
            System.out.println("3. List Pending Shipments for Driver");
            System.out.println("4. Find Average Delivery Time per Route");
            System.out.println("5. Show Delayed Shipments");
            System.out.println("6. Generate Daily Shipment Volume by Location");
            System.out.println("7. Assign Driver to Shipment");
            System.out.println("8. Update Shipment Location");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createShipment(scanner, shipmentDAO, assignmentDAO);
                    break;
                    
                case 2:
                    viewShipmentStatus(scanner, statusLogDAO);
                    break;
                    
                case 3:
                    listPendingShipmentsForDriver(scanner, assignmentDAO);
                    break;
                    
                case 4:
                    findAverageDeliveryTime(shipmentDAO);
                    break;
                    
                case 5:
                    showDelayedShipments(shipmentDAO);
                    break;
                    
                case 6:
                    generateDailyShipmentVolume(shipmentDAO);
                    break;
                    
                case 7:
                    assignDriverToShipment(scanner, assignmentDAO);
                    break;
                    
                case 8:
                    updateShipmentLocation(scanner);
                    break;
                    
                case 9:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                    
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createShipment(Scanner scanner, ShipmentDAO shipmentDAO, AssignmentDAO assignmentDAO) {
        System.out.println("\n=== Create New Shipment ===");
        
        System.out.print("Enter Origin Location ID: ");
        int originId = scanner.nextInt();
        
        System.out.print("Enter Destination Location ID: ");
        int destinationId = scanner.nextInt();
        
        // Menu-based status selection
        String[] validStatuses = {"pending", "in_transit", "arrived", "returned", "completed"};
        System.out.println("Select Status:");
        for (int i = 0; i < validStatuses.length; i++) {
            System.out.println((i + 1) + ". " + validStatuses[i]);
        }
        System.out.print("Enter choice (1-" + validStatuses.length + "): ");
        int statusChoice = scanner.nextInt();
        while (statusChoice < 1 || statusChoice > validStatuses.length) {
            System.out.print("Invalid choice! Enter (1-" + validStatuses.length + "): ");
            statusChoice = scanner.nextInt();
        }
        String status = validStatuses[statusChoice - 1];
        
        Shipment shipment = new Shipment();
        shipment.setOriginId(originId);
        shipment.setDestinationId(destinationId);
        shipment.setStatus(status);
        
        boolean created = shipmentDAO.createShipment(shipment);
        if (created) {
            System.out.println("Shipment created successfully! ID: " + shipment.getId());
            
            // Create initial status log
            StatusLogService logService = new StatusLogService();
            boolean logCreated = logService.createInitialLog(shipment.getId(), originId, null);
            if (logCreated) {
                System.out.println("Initial status log created.");
            }
            
            // Optionally assign a driver
            System.out.print("Do you want to assign a driver? (y/n): ");
            String assignDriver = scanner.next();
            if (assignDriver.equalsIgnoreCase("y")) {
                System.out.print("Enter Driver ID: ");
                int driverId = scanner.nextInt();
                
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Timestamp eta = new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // +24 hours
                
                boolean assigned = assignmentDAO.assignDriverToShipment(driverId, shipment.getId(), now, eta);
                if (assigned) {
                    System.out.println("Driver assigned successfully!");
                    // Create driver assignment log
                    logService.createDriverAssignmentLog(shipment.getId(), originId, driverId);
                } else {
                    System.out.println("Failed to assign driver.");
                }
            }
        } else {
            System.out.println("Failed to create shipment.");
        }
    }

    private static void viewShipmentStatus(Scanner scanner, StatusLogDAO statusLogDAO) {
        System.out.println("\n=== View Shipment Status ===");
        System.out.print("Enter Shipment ID: ");
        int shipmentId = scanner.nextInt();
        
        List<StatusLog> logs = statusLogDAO.getStatusLogsByShipmentId(shipmentId);
        if (logs.isEmpty()) {
            System.out.println("No status logs found for shipment ID: " + shipmentId);
        } else {
            System.out.println("Status Log for Shipment ID: " + shipmentId);
            System.out.println("Timestamp\t\tStatus\t\tLocation ID\tAgent ID");
            System.out.println("------------------------------------------------------------");
            for (StatusLog log : logs) {
                System.out.printf("%s\t%s\t\t%d\t\t%s%n", 
                    log.getTimestamp(), 
                    log.getStatus(), 
                    log.getLocationId(),
                    log.getAgentId() != null ? log.getAgentId().toString() : "N/A");
            }
        }
    }

    private static void listPendingShipmentsForDriver(Scanner scanner, AssignmentDAO assignmentDAO) {
        System.out.println("\n=== Pending Shipments for Driver ===");
        System.out.print("Enter Driver ID: ");
        int driverId = scanner.nextInt();
        
        // Get driver information
        Map<String, Object> driverInfo = assignmentDAO.getDriverInfo(driverId);
        if (driverInfo.isEmpty()) {
            System.out.println("Driver not found with ID: " + driverId);
            return;
        }
        
        System.out.println("Driver: " + driverInfo.get("name"));
        System.out.println("License: " + driverInfo.get("license_number"));
        System.out.println("Contact: " + driverInfo.get("contact_details"));
        System.out.println("Active Shipments: " + driverInfo.get("active_shipments") + "/5");
        
        // Get pending shipments
        List<DriverAssignment> pendingShipments = assignmentDAO.getPendingShipmentsForDriver(driverId);
        if (pendingShipments.isEmpty()) {
            System.out.println("No pending shipments for this driver.");
        } else {
            System.out.println("\nPending Shipments:");
            System.out.println("Shipment ID\tAssigned At\t\t\tEstimated Delivery");
            System.out.println("------------------------------------------------------------");
            for (DriverAssignment assignment : pendingShipments) {
                System.out.printf("%d\t\t%s\t%s%n", 
                    assignment.getShipmentId(),
                    assignment.getAssignedAt(),
                    assignment.getEstimatedDelivery());
            }
        }
    }

    private static void findAverageDeliveryTime(ShipmentDAO shipmentDAO) {
        System.out.println("\n=== Average Delivery Time per Route ===");
        Map<String, Double> avgTimes = shipmentDAO.getAvgDeliveryTimeByRoute();
        if (avgTimes.isEmpty()) {
            System.out.println("No delivery time data available for completed shipments.");
        } else {
            System.out.println("Route\t\t\tAverage Delivery Time\tTotal Shipments");
            System.out.println("--------------------------------------------------------");
            for (Map.Entry<String, Double> entry : avgTimes.entrySet()) {
                String route = entry.getKey();
                double avgMinutes = entry.getValue();
                
                // Format time nicely
                String timeStr;
                if (avgMinutes < 60) {
                    timeStr = String.format("%.0f minutes", avgMinutes);
                } else {
                    int hours = (int) (avgMinutes / 60);
                    int minutes = (int) (avgMinutes % 60);
                    timeStr = hours + "h " + minutes + "m";
                }
                
                System.out.printf("%-15s\t%s%n", route, timeStr);
            }
        }
    }

    private static void showDelayedShipments(ShipmentDAO shipmentDAO) {
        System.out.println("\n=== Delayed Shipments ===");
        List<Map<String, Object>> delayedShipments = shipmentDAO.getDelayedShipmentsWithDetails();
        
        if (delayedShipments.isEmpty()) {
            System.out.println("No delayed shipments found.");
        } else {
            System.out.println("Delayed Shipments (sorted by delay time):");
            System.out.println("Shipment ID\tOrigin\tDest\tDriver\t\tETA\t\t\tActual\t\t\tDelay");
            System.out.println("----------------------------------------------------------------------------------------");
            
            for (Map<String, Object> shipment : delayedShipments) {
                int id = (Integer) shipment.get("id");
                int originId = (Integer) shipment.get("origin_id");
                int destId = (Integer) shipment.get("destination_id");
                String driverName = (String) shipment.get("driver_name");
                Timestamp eta = (Timestamp) shipment.get("estimated_delivery");
                Timestamp actual = (Timestamp) shipment.get("actual_delivery");
                int delayMinutes = (Integer) shipment.get("delay_minutes");
                
                // Format delay time
                String delayStr;
                if (delayMinutes < 60) {
                    delayStr = delayMinutes + " min";
                } else {
                    int hours = delayMinutes / 60;
                    int minutes = delayMinutes % 60;
                    delayStr = hours + "h " + minutes + "m";
                }
                
                System.out.printf("%d\t\t%d\t%d\t%-12s\t%s\t%s\t%s%n", 
                    id, originId, destId, driverName, eta, actual, delayStr);
            }
            
            // Summary statistics
            int totalDelayed = delayedShipments.size();
            int totalDelayMinutes = delayedShipments.stream()
                .mapToInt(s -> (Integer) s.get("delay_minutes"))
                .sum();
            double avgDelayMinutes = totalDelayMinutes / (double) totalDelayed;
            
            System.out.println("\nSummary:");
            System.out.println("Total Delayed Shipments: " + totalDelayed);
            System.out.println("Total Delay Time: " + (totalDelayMinutes / 60) + " hours " + (totalDelayMinutes % 60) + " minutes");
            System.out.printf("Average Delay: %.1f minutes%n", avgDelayMinutes);
        }
    }

    private static void generateDailyShipmentVolume(ShipmentDAO shipmentDAO) {
        System.out.println("\n=== Daily Shipment Volume by Location ===");
        Map<String, Map<java.sql.Date, Integer>> volumeData = shipmentDAO.getShipmentVolumeByLocation();
        if (volumeData.isEmpty()) {
            System.out.println("No shipment volume data available.");
        } else {
            for (Map.Entry<String, Map<java.sql.Date, Integer>> locationEntry : volumeData.entrySet()) {
                String location = locationEntry.getKey();
                Map<java.sql.Date, Integer> dailyData = locationEntry.getValue();
                
                System.out.println("\n" + location + ":");
                System.out.println("Date\t\t\tShipment Count");
                System.out.println("----------------------------------------");
                
                // Sort by date (newest first)
                dailyData.entrySet().stream()
                    .sorted(Map.Entry.<java.sql.Date, Integer>comparingByKey().reversed())
                    .forEach(entry -> {
                        System.out.printf("%s\t%d%n", entry.getKey(), entry.getValue());
                    });
            }
        }
    }

    private static void assignDriverToShipment(Scanner scanner, AssignmentDAO assignmentDAO) {
        System.out.println("\n=== Assign Driver to Shipment ===");
        System.out.print("Enter Driver ID: ");
        int driverId = scanner.nextInt();
        
        // Check driver info and current load
        Map<String, Object> driverInfo = assignmentDAO.getDriverInfo(driverId);
        if (driverInfo.isEmpty()) {
            System.out.println("Driver not found with ID: " + driverId);
            return;
        }
        
        int activeShipments = (Integer) driverInfo.get("active_shipments");
        System.out.println("Driver: " + driverInfo.get("name"));
        System.out.println("Current Active Shipments: " + activeShipments + "/5");
        
        if (activeShipments >= 5) {
            System.out.println("Cannot assign: Driver has reached maximum active shipments (5)");
            return;
        }
        
        System.out.print("Enter Shipment ID: ");
        int shipmentId = scanner.nextInt();
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp eta = new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // +24 hours
        
        boolean assigned = assignmentDAO.assignDriverToShipment(driverId, shipmentId, now, eta);
        if (assigned) {
            System.out.println("Driver assigned successfully!");
            System.out.println("Driver now has " + (activeShipments + 1) + "/5 active shipments");
        } else {
            System.out.println("Failed to assign driver. Check if driver has reached max shipments.");
        }
    }

    private static void updateShipmentLocation(Scanner scanner) {
        System.out.println("\n=== Update Shipment Location ===");
        
        System.out.print("Enter Shipment ID: ");
        int shipmentId = scanner.nextInt();
        
        System.out.print("Enter New Location ID: ");
        int locationId = scanner.nextInt();
        
        // Get current shipment details first
        ShipmentDAO shipmentDAO = new ShipmentDAO();
        Shipment shipment = shipmentDAO.getShipmentById(shipmentId);
        
        if (shipment == null) {
            System.out.println("Shipment not found with ID: " + shipmentId);
            return;
        }
        
        // Get agent for this location (first agent assigned to this location)
        AgentDAO agentDAO = new AgentDAO();
        List<Agent> agents = agentDAO.getAgentsByLocation(locationId);
        
        if (agents.isEmpty()) {
            System.out.println("ERROR: No agents assigned to location " + locationId);
            return;
        }
        
        Agent agent = agents.get(0); // Use first agent for this location
        System.out.println("Agent assigned: " + agent.getName() + " (Location: " + locationId + ")");
        
        StatusLogService logService = new StatusLogService();
        
        // Check if shipment is already completed
        if (logService.isShipmentCompleted(shipmentId)) {
            System.out.println("ERROR: Shipment " + shipmentId + " is already completed and cannot be updated.");
            System.out.println("Shipment has reached its final destination. Status cannot be updated further. Thank you.");
            return;
        }
        
        System.out.println("Shipment: " + shipmentId);
        System.out.println("Origin: " + shipment.getOriginId());
        System.out.println("Destination: " + shipment.getDestinationId());
        System.out.println("New Location: " + locationId);
        System.out.println("Current Status: " + logService.getCurrentStatus(shipmentId));
        
        // Determine status automatically
        String status;
        if (locationId == shipment.getDestinationId()) {
            status = "completed";
            System.out.println("Status will be set to: COMPLETED (reached destination)");
            System.out.println("WARNING: This will lock the shipment for further updates!");
        } else {
            status = "arrived";
            System.out.println("Status will be set to: ARRIVED (at intermediate location)");
        }
        
        // Prompt for actual delivery time if shipment is completed or arrived
        Timestamp actualDeliveryTime = null;
        if (status.equals("completed") || status.equals("arrived")) {
            System.out.println("\nEnter actual delivery time:");
            System.out.print("Year (e.g., 2024): ");
            int year = scanner.nextInt();
            System.out.print("Month (1-12): ");
            int month = scanner.nextInt();
            System.out.print("Day (1-31): ");
            int day = scanner.nextInt();
            System.out.print("Hour (0-23): ");
            int hour = scanner.nextInt();
            System.out.print("Minute (0-59): ");
            int minute = scanner.nextInt();
            
            // Create timestamp (month is 0-based in Calendar)
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month - 1, day, hour, minute, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            actualDeliveryTime = new Timestamp(cal.getTimeInMillis());
            
            System.out.println("Actual delivery time set to: " + actualDeliveryTime);
        }
        
        // Create status log with automatic status determination and actual delivery time
        boolean logCreated = logService.createStatusChangeLog(shipmentId, locationId, agent.getId(), actualDeliveryTime);
        
        if (logCreated) {
            System.out.println("Location updated successfully!");
            System.out.println("New status: " + status);
            if (actualDeliveryTime != null) {
                System.out.println("Actual delivery time recorded: " + actualDeliveryTime);
            }
            if (status.equals("completed")) {
                System.out.println("Shipment is now COMPLETED and locked for further updates.");
            }
        } else {
            System.out.println("Failed to update location.");
        }
    }
} 