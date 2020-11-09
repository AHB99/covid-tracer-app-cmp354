package com.example.cmp354_covidtracer;

public class Exposure {


    private String positiveId;
    private String userId;
    private Location location;

    public Exposure() {
    }


    public Exposure(String positiveId, String userId, Location location) {
        this.positiveId = positiveId;
        this.userId = userId;
        this.location = location;
    }

    public String getPositiveId() {
        return positiveId;
    }

    public void setPositiveId(String positiveId) {
        this.positiveId = positiveId;
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
    @Override
    public String toString() {
        return "Exposure{" +
                "positiveId='" + positiveId + '\'' +
                ", userId='" + userId + '\'' +
                ", location=" + location +
                '}';
    }
}
