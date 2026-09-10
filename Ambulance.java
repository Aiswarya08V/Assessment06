package com.dispatch.model;

public class Ambulance {
    private final String ambulanceId;
    private final AmbulanceType type;
    private AmbulanceState state;
    private final String driverName;
    private final String driverContact;
    private double currentX; // Simple 2D coordinate for distance mapping
    private double currentY;

    public Ambulance(String ambulanceId, AmbulanceType type, String driverName, String driverContact, double currentX, double currentY) {
        this.ambulanceId = ambulanceId;
        this.type = type;
        this.state = AmbulanceState.AVAILABLE;
        this.driverName = driverName;
        this.driverContact = driverContact;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    // Getters and Setters
    public String getAmbulanceId() { return ambulanceId; }
    public AmbulanceType getType() { return type; }
    public synchronized AmbulanceState getState() { return state; }
    public synchronized void setState(AmbulanceState state) { this.state = state; }
    public String getDriverName() { return driverName; }
    public double getCurrentX() { return currentX; }
    public double getCurrentY() { return currentY; }
    
    public synchronized void updateLocation(double x, double y) {
        this.currentX = x;
        this.currentY = y;
    }

    public double calculateDistance(double targetX, double targetY) {
        return Math.sqrt(Math.pow(this.currentX - targetX, 2) + Math.pow(this.currentY - targetY, 2));
    }
}
