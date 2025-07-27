package model;

public class Location {
    private int id;
    private String name;
    private Integer parentLocationId;
    private String type;

    public Location() {}

    public Location(int id, String name, Integer parentLocationId, String type) {
        this.id = id;
        this.name = name;
        this.parentLocationId = parentLocationId;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getParentLocationId() { return parentLocationId; }
    public void setParentLocationId(Integer parentLocationId) { this.parentLocationId = parentLocationId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
} 