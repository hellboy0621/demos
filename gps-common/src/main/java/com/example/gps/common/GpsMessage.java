package com.example.gps.common;

public class GpsMessage {
    private String deviceId;
    private double latitude;
    private double longitude;
    private double speedKmh;
    private long eventTime;

    public GpsMessage() {
    }

    public GpsMessage(String deviceId, double latitude, double longitude, double speedKmh, long eventTime) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmh = speedKmh;
        this.eventTime = eventTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeedKmh() {
        return speedKmh;
    }

    public void setSpeedKmh(double speedKmh) {
        this.speedKmh = speedKmh;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "GpsMessage{" +
                "deviceId='" + deviceId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speedKmh=" + speedKmh +
                ", eventTime=" + eventTime +
                '}';
    }
}
