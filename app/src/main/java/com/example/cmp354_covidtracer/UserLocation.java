package com.example.cmp354_covidtracer;

public class UserLocation {
    private double lng;
    private double lat;
    //Timestamp
    private long ts;

    public UserLocation(double lng, double lat, long ts) {
        this.lng = lng;
        this.lat = lat;
        this.ts = ts;
    }

    public UserLocation() {
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public static boolean isExposure(UserLocation posLoc, UserLocation currLoc){
        return ((Math.abs(posLoc.getLat() - currLoc.getLat()) < 2)
                && (Math.abs(posLoc.getLng() - currLoc.getLng()) < 2)
                && (Math.abs(posLoc.getTs() - currLoc.getTs()) < 1000*60*60*24));

    }

    @Override
    public String toString() {
        return "Location{" +
                "lng=" + lng +
                ", lat=" + lat +
                ", ts=" + ts +
                '}';
    }
}
