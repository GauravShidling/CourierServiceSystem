package service;

import dao.ShipmentDAO;
import dao.StatusLogDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import model.Shipment;
import model.StatusLog;
import util.DBConnection;

public class StatusLogService {
    private StatusLogDAO statusLogDAO;
    private ShipmentDAO shipmentDAO;
    
    public StatusLogService() {
        this.statusLogDAO = new StatusLogDAO();
        this.shipmentDAO = new ShipmentDAO();
    }
    
    // Create initial log when shipment is created
    public boolean createInitialLog(int shipmentId, int locationId, Integer agentId) {
        StatusLog log = new StatusLog();
        log.setShipmentId(shipmentId);
        log.setLocationId(locationId);
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setStatus("pending");
        log.setAgentId(agentId);
        
        return statusLogDAO.createStatusLog(log);
    }
    
    // Create log when driver is assigned
    public boolean createDriverAssignmentLog(int shipmentId, int locationId, int agentId) {
        StatusLog log = new StatusLog();
        log.setShipmentId(shipmentId);
        log.setLocationId(locationId);
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setStatus("in_transit");
        log.setAgentId(agentId);
        
        return statusLogDAO.createStatusLog(log);
    }
    
    // Check if shipment is completed (locked for further updates)
    public boolean isShipmentCompleted(int shipmentId) {
        String currentStatus = getCurrentStatus(shipmentId);
        return "completed".equals(currentStatus);
    }
    
    // Create log for status change with automatic status determination
    public boolean createStatusChangeLog(int shipmentId, int locationId, Integer agentId, Timestamp actualDeliveryTime) {
        // Check if shipment is already completed
        if (isShipmentCompleted(shipmentId)) {
            System.out.println("ERROR: Shipment is already completed and cannot be updated further.");
            return false;
        }
        
        // Get shipment details to determine destination
        Shipment shipment = shipmentDAO.getShipmentById(shipmentId);
        if (shipment == null) {
            return false;
        }
        
        // Determine status based on location
        String status;
        if (locationId == shipment.getDestinationId()) {
            status = "completed";
        } else {
            status = "arrived";
        }
        
        StatusLog log = new StatusLog();
        log.setShipmentId(shipmentId);
        log.setLocationId(locationId);
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setStatus(status);
        log.setAgentId(agentId);
        
        // Update shipment status in database
        shipmentDAO.updateShipmentStatus(shipmentId, status);
        
        // If shipment is completed or arrived, update actual_delivery in driver assignment
        if (status.equals("completed") || status.equals("arrived")) {
            updateDriverAssignmentDeliveryTime(shipmentId, actualDeliveryTime);
        }
        
        return statusLogDAO.createStatusLog(log);
    }
    
    // Update actual_delivery time in SHIPMENT_DRIVER_ASSIGNMENT table
    private void updateDriverAssignmentDeliveryTime(int shipmentId, Timestamp actualDeliveryTime) {
        String sql = "UPDATE SHIPMENT_DRIVER_ASSIGNMENT SET actual_delivery = ? WHERE shipment_id = ? AND actual_delivery IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, actualDeliveryTime);
            stmt.setInt(2, shipmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Create log for manual status change
    public boolean createManualStatusChangeLog(int shipmentId, int locationId, String newStatus, Integer agentId) {
        StatusLog log = new StatusLog();
        log.setShipmentId(shipmentId);
        log.setLocationId(locationId);
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setStatus(newStatus);
        log.setAgentId(agentId);
        
        // Update shipment status in database
        shipmentDAO.updateShipmentStatus(shipmentId, newStatus);
        
        return statusLogDAO.createStatusLog(log);
    }
    
    // Get current status of shipment
    public String getCurrentStatus(int shipmentId) {
        return statusLogDAO.getCurrentStatus(shipmentId);
    }
    
    // Get shipment's current location
    public int getCurrentLocation(int shipmentId) {
        return statusLogDAO.getCurrentLocation(shipmentId);
    }
} 