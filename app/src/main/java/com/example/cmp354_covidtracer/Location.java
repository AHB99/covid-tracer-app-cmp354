package com.example.cmp354_covidtracer;

public class Location {
    private int lng;
    private int lat;
    private int timestamp;

    public Location(int lng, int lat, int timestamp) {
        this.lng = lng;
        this.lat = lat;
        this.timestamp = timestamp;
    }

    public Location() {
    }

    public int getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
