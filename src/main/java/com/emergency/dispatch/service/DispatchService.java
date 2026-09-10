package com.emergency.dispatch.service;

import com.emergency.dispatch.exception.InvalidRequestException;
import com.emergency.dispatch.model.*;

import java.util.*;

public class DispatchService {
    private final Map<String, Ambulance> ambulances = new HashMap<>();
    private final PriorityQueue<EmergencyRequest> waitingQueue = new PriorityQueue<>();
    private final List<EmergencyRequest> historyLog = new ArrayList<>();

    public void registerAmbulance(Ambulance ambulance) {
        ambulances.put(ambulance.getAmbulanceId(), ambulance);
    }

    public synchronized void submitEmergencyRequest(EmergencyRequest request) {
        validateRequest(request);
        historyLog.add(request);

        Ambulance matchedAmbulance = findBestAvailableAmbulance(request);
        if (matchedAmbulance != null) {
            allocateAmbulance(request, matchedAmbulance);
        } else {
            request.setStatus("QUEUED");
            waitingQueue.add(request);
        }
    }

    private void validateRequest(EmergencyRequest request) {
        if (request.getPatientId() == null || request.getPatientId().isBlank() ||
            request.getPickupLocation() == null || request.getPickupLocation().isBlank() ||
            request.getEstimatedDistance() <= 0) {
            throw new InvalidRequestException("Invalid emergency request data provided.");
        }
    }

    private Ambulance findBestAvailableAmbulance(EmergencyRequest request) {
        AmbulanceType preferredType = mapPriorityToAmbulanceType(request.getPriority());

        for (Ambulance amb : ambulances.values()) {
            if (amb.getState() == AmbulanceState.AVAILABLE && amb.getType() == preferredType) {
                return amb;
            }
        }

        for (Ambulance amb : ambulances.values()) {
            if (amb.getState() == AmbulanceState.AVAILABLE) {
                return amb;
            }
        }
        return null;
    }

    private AmbulanceType mapPriorityToAmbulanceType(EmergencyPriority priority) {
        return switch (priority) {
            case CRITICAL -> AmbulanceType.ICU;
            case HIGH -> AmbulanceType.ADVANCED_LIFE_SUPPORT;
            default -> AmbulanceType.BASIC;
        };
    }

    private void allocateAmbulance(EmergencyRequest request, Ambulance ambulance) {
        ambulance.setState(AmbulanceState.DISPATCHED);
        request.setAssignedAmbulance(ambulance);
        request.setStatus("DISPATCHED");
    }

    public synchronized void updateAmbulanceState(String ambulanceId, AmbulanceState newState) {
        Ambulance ambulance = ambulances.get(ambulanceId);
        if (ambulance == null) {
            throw new IllegalArgumentException("Ambulance ID not found.");
        }

        ambulance.setState(newState);

        if (newState == AmbulanceState.AVAILABLE) {
            processWaitingQueue();
        }
    }

    private void processWaitingQueue() {
        if (!waitingQueue.isEmpty()) {
            EmergencyRequest nextRequest = waitingQueue.peek();
            Ambulance availableAmbulance = findBestAvailableAmbulance(nextRequest);
            if (availableAmbulance != null) {
                waitingQueue.poll();
                allocateAmbulance(nextRequest, availableAmbulance);
            }
        }
    }

    public PriorityQueue<EmergencyRequest> getWaitingQueue() { return waitingQueue; }
    public List<EmergencyRequest> getHistoryLog() { return historyLog; }
    public Map<String, Ambulance> getAmbulances() { return ambulances; }
}
