package dao;

import model.Driver;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class DriverDAO {
    
    // Validate any agent (driver, warehouse staff, admin)
    public Driver validateAgent(int agentId) {
        String sql = "SELECT * FROM DRIVER WHERE id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_number"),
                        rs.getString("contact_details")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Check if agent has permission for specific action
    public boolean hasPermission(int agentId, String action) {
        Driver agent = validateAgent(agentId);
        if (agent == null) return false;
        
        // For now, all valid agents can perform all actions
        // You can add role-based logic later if needed
        return true;
    }
    
    public boolean createDriver(Driver driver) {
        // TODO: Implement driver creation
        return false;
    }
    public Driver getDriverById(int id) {
        // TODO: Implement get driver by id
        return null;
    }
    public List<Driver> getAllDrivers() {
        // TODO: Implement get all drivers
        return new ArrayList<>();
    }
    public boolean updateDriver(Driver driver) {
        // TODO: Implement update driver
        return false;
    }
    public boolean deleteDriver(int id) {
        // TODO: Implement delete driver
        return false;
    }
} 