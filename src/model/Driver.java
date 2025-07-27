package model;

public class Driver {
    private int id;
    private String name;
    private String licenseNumber;
    private String contactDetails;

    public Driver() {}

    public Driver(int id, String name, String licenseNumber, String contactDetails) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.contactDetails = contactDetails;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }
} 