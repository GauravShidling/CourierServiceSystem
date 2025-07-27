package model;

import java.sql.Timestamp;

public class Shipment {
    private int id;
    private int originId;
    private int destinationId;
    private String status;
    private Timestamp createdAt;

    public Shipment() {}

    public Shipment(int id, int originId, int destinationId, String status, Timestamp createdAt) {
        this.id = id;
        this.originId = originId;
        this.destinationId = destinationId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOriginId() { return originId; }
    public void setOriginId(int originId) { this.originId = originId; }
    public int getDestinationId() { return destinationId; }
    public void setDestinationId(int destinationId) { this.destinationId = destinationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
} 