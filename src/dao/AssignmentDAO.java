package dao;

import model.DriverAssignment;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class AssignmentDAO {
    private static final int MAX_ACTIVE_SHIPMENTS_PER_DRIVER = 5;
    
    // Assign a driver to a shipment with ETA
    public boolean assignDriverToShipment(int driverId, int shipmentId, Timestamp assignedAt, Timestamp estimatedDelivery) {
        // Check max shipments per driver
        if (getActiveShipmentCountForDriver(driverId) >= MAX_ACTIVE_SHIPMENTS_PER_DRIVER) {
            System.out.println("Driver has reached max active shipments (" + MAX_ACTIVE_SHIPMENTS_PER_DRIVER + ").");
            return false;
        }
        
        String sql = "INSERT INTO SHIPMENT_DRIVER_ASSIGNMENT (driver_id, shipment_id, assigned_at, estimated_delivery) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            stmt.setInt(2, shipmentId);
            stmt.setTimestamp(3, assignedAt);
            stmt.setTimestamp(4, estimatedDelivery);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get count of active shipments for a driver
    public int getActiveShipmentCountForDriver(int driverId) {
        String sql = "SELECT COUNT(*) FROM SHIPMENT_DRIVER_ASSIGNMENT WHERE driver_id = ? AND actual_delivery IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get pending shipments for a specific driver
    public List<DriverAssignment> getPendingShipmentsForDriver(int driverId) {
        List<DriverAssignment> pendingShipments = new ArrayList<>();
        String sql = "SELECT sda.*, s.origin_id, s.destination_id, s.status as shipment_status " +
                    "FROM SHIPMENT_DRIVER_ASSIGNMENT sda " +
                    "JOIN SHIPMENT s ON sda.shipment_id = s.id " +
                    "WHERE sda.driver_id = ? AND sda.actual_delivery IS NULL " +
                    "ORDER BY sda.assigned_at ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DriverAssignment assignment = new DriverAssignment(
                        rs.getInt("id"),
                        rs.getInt("driver_id"),
                        rs.getInt("shipment_id"),
                        null, // package_id is null for shipment assignments
                        rs.getTimestamp("assigned_at"),
                        rs.getTimestamp("estimated_delivery"),
                        rs.getTimestamp("actual_delivery")
                    );
                    pendingShipments.add(assignment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingShipments;
    }

    // Get driver details with active shipment count
    public Map<String, Object> getDriverInfo(int driverId) {
        Map<String, Object> driverInfo = new HashMap<>();
        String sql = "SELECT d.name, d.license_number, d.contact_details, " +
                    "(SELECT COUNT(*) FROM SHIPMENT_DRIVER_ASSIGNMENT WHERE driver_id = d.id AND actual_delivery IS NULL) as active_shipments " +
                    "FROM DRIVER d WHERE d.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    driverInfo.put("name", rs.getString("name"));
                    driverInfo.put("license_number", rs.getString("license_number"));
                    driverInfo.put("contact_details", rs.getString("contact_details"));
                    driverInfo.put("active_shipments", rs.getInt("active_shipments"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driverInfo;
    }

    // Stub for assigning driver to package
    public boolean assignDriverToPackage(int driverId, int packageId, Timestamp assignedAt, Timestamp estimatedDelivery) {
        // TODO: Implement package driver assignment
        return false;
    }
} 