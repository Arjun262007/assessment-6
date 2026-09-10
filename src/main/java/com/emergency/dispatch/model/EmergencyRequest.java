package com.emergency.dispatch.model;

import java.util.UUID;

public class EmergencyRequest implements Comparable<EmergencyRequest> {
    private final String requestId;
    private final String patientId;
    private final String emergencyType;
    private final String pickupLocation;
    private final String destinationHospital;
    private final EmergencyPriority priority;
    private final double estimatedDistance;
    private String status;
    private Ambulance assignedAmbulance;

    public EmergencyRequest(String patientId, String emergencyType, String pickupLocation, 
                            String destinationHospital, EmergencyPriority priority, double estimatedDistance) {
        this.requestId = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.emergencyType = emergencyType;
        this.pickupLocation = pickupLocation;
        this.destinationHospital = destinationHospital;
        this.priority = priority;
        this.estimatedDistance = estimatedDistance;
        this.status = "PENDING";
    }

    @Override
    public int compareTo(EmergencyRequest o) {
        return Integer.compare(this.priority.getRank(), o.priority.getRank());
    }

    public double calculateETA() {
        return 2 + (estimatedDistance / 50.0) * 60;
    }

    public String getRequestId() { return requestId; }
    public String getPatientId() { return patientId; }
    public String getEmergencyType() { return emergencyType; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestinationHospital() { return destinationHospital; }
    public EmergencyPriority getPriority() { return priority; }
    public double getEstimatedDistance() { return estimatedDistance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Ambulance getAssignedAmbulance() { return assignedAmbulance; }
    public void setAssignedAmbulance(Ambulance assignedAmbulance) { this.assignedAmbulance = assignedAmbulance; }
}
