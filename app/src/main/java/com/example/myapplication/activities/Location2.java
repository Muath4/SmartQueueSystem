package com.example.myapplication.activities;

public class Location2 {
    private double latitude;
    private double longitude;

    public Location2() {
    }

    public Location2(double l, double lo){
        latitude=l;
        longitude=lo;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
