package model;

import java.sql.Timestamp;

public class DriverAssignment {
    private int id;
    private Integer driverId;
    private Integer shipmentId;
    private Integer packageId;
    private Timestamp assignedAt;
    private Timestamp estimatedDelivery;
    private Timestamp actualDelivery;

    public DriverAssignment() {}

    public DriverAssignment(int id, Integer driverId, Integer shipmentId, Integer packageId, Timestamp assignedAt, Timestamp estimatedDelivery, Timestamp actualDelivery) {
        this.id = id;
        this.driverId = driverId;
        this.shipmentId = shipmentId;
        this.packageId = packageId;
        this.assignedAt = assignedAt;
        this.estimatedDelivery = estimatedDelivery;
        this.actualDelivery = actualDelivery;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getDriverId() { return driverId; }
    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public Integer getShipmentId() { return shipmentId; }
    public void setShipmentId(Integer shipmentId) { this.shipmentId = shipmentId; }
    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }
    public Timestamp getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }
    public Timestamp getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(Timestamp estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public Timestamp getActualDelivery() { return actualDelivery; }
    public void setActualDelivery(Timestamp actualDelivery) { this.actualDelivery = actualDelivery; }
} 