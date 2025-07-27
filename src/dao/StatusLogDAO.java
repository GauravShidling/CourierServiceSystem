package dao;

import java.sql.*;
import java.util.*;
import model.StatusLog;
import util.DBConnection;

public class StatusLogDAO {
    public List<StatusLog> getStatusLogsByShipmentId(int shipmentId) {
        List<StatusLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM STATUS_LOG WHERE shipment_id = ? ORDER BY timestamp ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StatusLog log = new StatusLog(
                        rs.getInt("id"),
                        rs.getInt("shipment_id"),
                        rs.getInt("location_id"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("status"),
                        rs.getObject("agent_id") != null ? rs.getInt("agent_id") : null
                    );
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    // Create a new status log
    public boolean createStatusLog(StatusLog log) {
        String sql = "INSERT INTO STATUS_LOG (shipment_id, location_id, timestamp, status, agent_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, log.getShipmentId());
            stmt.setInt(2, log.getLocationId());
            stmt.setTimestamp(3, log.getTimestamp());
            stmt.setString(4, log.getStatus());
            if (log.getAgentId() != null) {
                stmt.setInt(5, log.getAgentId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get current status of a shipment
    public String getCurrentStatus(int shipmentId) {
        String sql = "SELECT status FROM STATUS_LOG WHERE shipment_id = ? ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    // Get current location of a shipment
    public int getCurrentLocation(int shipmentId) {
        String sql = "SELECT location_id FROM STATUS_LOG WHERE shipment_id = ? ORDER BY timestamp DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("location_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no location found
    }
} 