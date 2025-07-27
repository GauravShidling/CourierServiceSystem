package dao;

import model.Shipment;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ShipmentDAO {
    public boolean createShipment(Shipment shipment) {
        String sql = "INSERT INTO SHIPMENT (origin_id, destination_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, shipment.getOriginId());
            stmt.setInt(2, shipment.getDestinationId());
            stmt.setString(3, shipment.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    shipment.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get shipment by ID
    public Shipment getShipmentById(int shipmentId) {
        String sql = "SELECT * FROM SHIPMENT WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Shipment(
                        rs.getInt("id"),
                        rs.getInt("origin_id"),
                        rs.getInt("destination_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update shipment status
    public boolean updateShipmentStatus(int shipmentId, String newStatus) {
        String sql = "UPDATE SHIPMENT SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, shipmentId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Shipment> getDelayedShipments() {
        List<Shipment> delayedShipments = new ArrayList<>();
        String sql = "SELECT s.*, sda.estimated_delivery, sda.actual_delivery " +
                    "FROM SHIPMENT s " +
                    "JOIN SHIPMENT_DRIVER_ASSIGNMENT sda ON s.id = sda.shipment_id " +
                    "WHERE sda.actual_delivery IS NOT NULL " +
                    "AND sda.actual_delivery > sda.estimated_delivery " +
                    "ORDER BY (sda.actual_delivery - sda.estimated_delivery) DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Shipment shipment = new Shipment(
                        rs.getInt("id"),
                        rs.getInt("origin_id"),
                        rs.getInt("destination_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                    );
                    delayedShipments.add(shipment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delayedShipments;
    }
    
    // Get detailed delayed shipment information
    public List<Map<String, Object>> getDelayedShipmentsWithDetails() {
        List<Map<String, Object>> delayedShipments = new ArrayList<>();
        String sql = "SELECT s.id, s.origin_id, s.destination_id, s.status, s.created_at, " +
                    "sda.estimated_delivery, sda.actual_delivery, " +
                    "TIMESTAMPDIFF(MINUTE, sda.estimated_delivery, sda.actual_delivery) as delay_minutes, " +
                    "d.name as driver_name " +
                    "FROM SHIPMENT s " +
                    "JOIN SHIPMENT_DRIVER_ASSIGNMENT sda ON s.id = sda.shipment_id " +
                    "JOIN DRIVER d ON sda.driver_id = d.id " +
                    "WHERE sda.actual_delivery IS NOT NULL " +
                    "AND sda.actual_delivery > sda.estimated_delivery " +
                    "ORDER BY delay_minutes DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> shipment = new HashMap<>();
                    shipment.put("id", rs.getInt("id"));
                    shipment.put("origin_id", rs.getInt("origin_id"));
                    shipment.put("destination_id", rs.getInt("destination_id"));
                    shipment.put("status", rs.getString("status"));
                    shipment.put("created_at", rs.getTimestamp("created_at"));
                    shipment.put("estimated_delivery", rs.getTimestamp("estimated_delivery"));
                    shipment.put("actual_delivery", rs.getTimestamp("actual_delivery"));
                    shipment.put("delay_minutes", rs.getInt("delay_minutes"));
                    shipment.put("driver_name", rs.getString("driver_name"));
                    delayedShipments.add(shipment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delayedShipments;
    }

    public Map<String, Double> getAvgDeliveryTimeByRoute() {
        Map<String, Double> avgTimes = new HashMap<>();
        String sql = "SELECT " +
                    "CONCAT(s.origin_id, ' -> ', s.destination_id) as route, " +
                    "COUNT(*) as total_shipments, " +
                    "AVG(TIMESTAMPDIFF(MINUTE, s.created_at, sda.actual_delivery)) as avg_delivery_minutes " +
                    "FROM SHIPMENT s " +
                    "JOIN SHIPMENT_DRIVER_ASSIGNMENT sda ON s.id = sda.shipment_id " +
                    "WHERE s.status = 'completed' " +
                    "AND sda.actual_delivery IS NOT NULL " +
                    "GROUP BY s.origin_id, s.destination_id " +
                    "ORDER BY avg_delivery_minutes ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String route = rs.getString("route");
                    double avgMinutes = rs.getDouble("avg_delivery_minutes");
                    avgTimes.put(route, avgMinutes);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avgTimes;
    }

    public Map<String, Map<Date, Integer>> getShipmentVolumeByLocation() {
        Map<String, Map<Date, Integer>> volumeData = new HashMap<>();
        String sql = "SELECT " +
                    "s.origin_id, " +
                    "DATE(s.created_at) as shipment_date, " +
                    "COUNT(*) as shipment_count " +
                    "FROM SHIPMENT s " +
                    "GROUP BY s.origin_id, DATE(s.created_at) " +
                    "ORDER BY s.origin_id, shipment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int originId = rs.getInt("origin_id");
                    Date shipmentDate = rs.getDate("shipment_date");
                    int count = rs.getInt("shipment_count");
                    
                    String locationKey = "Location " + originId;
                    volumeData.computeIfAbsent(locationKey, k -> new HashMap<>()).put(shipmentDate, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return volumeData;
    }
} 