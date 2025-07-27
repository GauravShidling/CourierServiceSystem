package model;

import java.sql.Timestamp;

public class StatusLog {
    private int id;
    private int shipmentId;
    private int locationId;
    private Timestamp timestamp;
    private String status;
    private Integer agentId;

    public StatusLog() {}

    public StatusLog(int id, int shipmentId, int locationId, Timestamp timestamp, String status, Integer agentId) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.locationId = locationId;
        this.timestamp = timestamp;
        this.status = status;
        this.agentId = agentId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getShipmentId() { return shipmentId; }
    public void setShipmentId(int shipmentId) { this.shipmentId = shipmentId; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getAgentId() { return agentId; }
    public void setAgentId(Integer agentId) { this.agentId = agentId; }
} 