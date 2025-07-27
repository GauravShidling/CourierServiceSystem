package model;

public class Agent {
    private int id;
    private String name;
    private String employeeId;
    private int locationId; // Links agent to a specific location
    private String contactDetails;
    private boolean isActive;

    public Agent() {}

    public Agent(int id, String name, String employeeId, int locationId, String contactDetails, boolean isActive) {
        this.id = id;
        this.name = name;
        this.employeeId = employeeId;
        this.locationId = locationId;
        this.contactDetails = contactDetails;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
} 