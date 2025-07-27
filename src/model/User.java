package model;

public class User {
    private int id;
    private String name;
    private String contactDetails;
    private String address;

    public User() {}

    public User(int id, String name, String contactDetails, String address) {
        this.id = id;
        this.name = name;
        this.contactDetails = contactDetails;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
} 