package com.dispatch.model;

import java.time.LocalDateTime;

public class EmergencyRequest implements Comparable<EmergencyRequest> {
    private final String requestId;
    private final String patientId;
    private final String emergencyType;
    private final EmergencyPriority priority;
    private final double pickupX;
    private final double pickupY;
    private final String destinationHospital;
    private final LocalDateTime timestamp;
    
    private String assignedAmbulanceId;
    private String status; // "PENDING", "DISPATCHED", "COMPLETED"
    private double estimatedArrivalTimeMinutes;

    public EmergencyRequest(String requestId, String patientId, String emergencyType, EmergencyPriority priority, 
                            double pickupX, double pickupY, String destinationHospital) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.priority = priority;
        this.pickupX = pickupX;
        this.pickupY = pickupY;
        this.destinationHospital = destinationHospital;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    // High priority ranks (lower integer values) come first
    @Override
    public int compareTo(EmergencyRequest o) {
        int priorityCompare = Integer.compare(this.priority.getRank(), o.priority.getRank());
        if (priorityCompare != 0) return priorityCompare;
        return this.timestamp.compareTo(o.timestamp); // FIFO if priorities are equal
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public String getPatientId() { return patientId; }
    public EmergencyPriority getPriority() { return priority; }
    public double getPickupX() { return pickupX; }
    public double getPickupY() { return pickupY; }
    public String getDestinationHospital() { return destinationHospital; }
    public synchronized String getAssignedAmbulanceId() { return assignedAmbulanceId; }
    public synchronized void setAssignedAmbulanceId(String assignedAmbulanceId) { this.assignedAmbulanceId = assignedAmbulanceId; }
    public synchronized String getStatus() { return status; }
    public synchronized void setStatus(String status) { this.status = status; }
    public synchronized double getEstimatedArrivalTimeMinutes() { return estimatedArrivalTimeMinutes; }
    public synchronized void setEstimatedArrivalTimeMinutes(double eta) { this.estimatedArrivalTimeMinutes = eta; }
}
