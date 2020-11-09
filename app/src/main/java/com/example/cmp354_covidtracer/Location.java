package com.example.cmp354_covidtracer;

public class Location {
    private int lng;
    private int lat;
    //Timestamp
    private int ts;

    public Location(int lng, int lat, int ts) {
        this.lng = lng;
        this.lat = lat;
        this.ts = ts;
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

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public static boolean isExposure(Location posLoc, Location currLoc){
        return ((Math.abs(posLoc.getLat() - currLoc.getLat()) < 2)
                && (Math.abs(posLoc.getLng() - currLoc.getLng()) < 2)
                && (Math.abs(posLoc.getTs() - currLoc.getTs()) < 50));

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
