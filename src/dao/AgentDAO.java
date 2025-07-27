package dao;

import model.Agent;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class AgentDAO {
    
    // Validate agent and check if they can update shipments for a specific location
    public Agent validateAgentForLocation(int agentId, int locationId) {
        String sql = "SELECT * FROM AGENT WHERE id = ? AND location_id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agentId);
            stmt.setInt(2, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Agent(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("employee_id"),
                        rs.getInt("location_id"),
                        rs.getString("contact_details"),
                        rs.getBoolean("is_active")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get agent by ID (simple validation)
    public Agent getAgentById(int agentId) {
        String sql = "SELECT * FROM AGENT WHERE id = ? AND is_active = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Agent(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("employee_id"),
                        rs.getInt("location_id"),
                        rs.getString("contact_details"),
                        rs.getBoolean("is_active")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all agents for a specific location
    public List<Agent> getAgentsByLocation(int locationId) {
        List<Agent> agents = new ArrayList<>();
        String sql = "SELECT * FROM AGENT WHERE location_id = ? AND is_active = 1 ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Agent agent = new Agent(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("employee_id"),
                        rs.getInt("location_id"),
                        rs.getString("contact_details"),
                        rs.getBoolean("is_active")
                    );
                    agents.add(agent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agents;
    }
} 