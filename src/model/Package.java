package model;

import java.sql.Timestamp;

public class Package {
    private int id;
    private int shipmentId;
    private int senderId;
    private int recipientId;
    private double weight;
    private String contentDescription;
    private String status;
    private Timestamp estimatedDelivery;
    private Timestamp actualDelivery;
    private boolean isDelayed;

    public Package() {}

    public Package(int id, int shipmentId, int senderId, int recipientId, double weight, String contentDescription, String status, Timestamp estimatedDelivery, Timestamp actualDelivery, boolean isDelayed) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.weight = weight;
        this.contentDescription = contentDescription;
        this.status = status;
        this.estimatedDelivery = estimatedDelivery;
        this.actualDelivery = actualDelivery;
        this.isDelayed = isDelayed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getShipmentId() { return shipmentId; }
    public void setShipmentId(int shipmentId) { this.shipmentId = shipmentId; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getContentDescription() { return contentDescription; }
    public void setContentDescription(String contentDescription) { this.contentDescription = contentDescription; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(Timestamp estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public Timestamp getActualDelivery() { return actualDelivery; }
    public void setActualDelivery(Timestamp actualDelivery) { this.actualDelivery = actualDelivery; }
    public boolean isDelayed() { return isDelayed; }
    public void setDelayed(boolean delayed) { isDelayed = delayed; }
} 