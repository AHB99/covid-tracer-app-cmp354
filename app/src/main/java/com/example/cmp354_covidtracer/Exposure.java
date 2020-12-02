package com.example.cmp354_covidtracer;

public class Exposure {


    private String positiveId;
    private String userId;
    private UserLocation location;

    public Exposure() {
    }


    public Exposure(String positiveId, String userId, UserLocation location) {
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

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
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
