package com.example.cmp354_covidtracer;

public class Exposure {

    private String covidId;
    private String userId;
    private Location location;

    public Exposure() {
    }

    public Exposure(String covidId, String userId, Location location) {
        this.covidId = covidId;
        this.userId = userId;
        this.location = location;
    }

    public String getCovidId() {
        return covidId;
    }

    public void setCovidId(String covidId) {
        this.covidId = covidId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
