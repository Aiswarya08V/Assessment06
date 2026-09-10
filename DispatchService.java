package com.dispatch.service;

import com.dispatch.model.*;
import com.dispatch.exception.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class DispatchService {
    private final Map<String, Ambulance> fleet = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<EmergencyRequest> waitingQueue = new PriorityBlockingQueue<>();
    private final List<EmergencyRequest> history = Collections.synchronizedList(new ArrayList<>());

    private static final double AVERAGE_SPEED_KM_PER_MIN = 0.8; // ~50 km/h

    public void registerAmbulance(Ambulance ambulance) {
        if (ambulance == null || ambulance.getAmbulanceId() == null) {
            throw new InvalidRequestException("Invalid ambulance details registration entry.");
        }
        fleet.put(ambulance.getAmbulanceId(), ambulance);
    }

    public synchronized void submitEmergencyRequest(EmergencyRequest request) {
        if (request == null || request.getPatientId() == null || request.getRequestId() == null) {
            throw new InvalidRequestException("Emergency request structure is invalid.");
        }
        history.add(request);
        waitingQueue.add(request);
        processWaitingQueue();
    }

    public synchronized void processWaitingQueue() {
        if (waitingQueue.isEmpty()) return;

        List<EmergencyRequest> unserved = new ArrayList<>();

        while (!waitingQueue.isEmpty()) {
            EmergencyRequest currentReq = waitingQueue.poll();
            Ambulance assigned = findBestAvailableAmbulance(currentReq);

            if (assigned != null) {
                assigned.setState(AmbulanceState.DISPATCHED);
                currentReq.setAssignedAmbulanceId(assigned.getAmbulanceId());
                currentReq.setStatus("DISPATCHED");
                
                double distance = assigned.calculateDistance(currentReq.getPickupX(), currentReq.getPickupY());
                currentReq.setEstimatedArrivalTimeMinutes(distance / AVERAGE_SPEED_KM_PER_MIN);
                
                System.out.println("[DISPATCHED] Emergency ID " + currentReq.getRequestId() + 
                                   " handled by Ambulance ID: " + assigned.getAmbulanceId());
            } else {
                unserved.add(currentReq);
            }
        }
        waitingQueue.addAll(unserved);
    }

    private Ambulance findBestAvailableAmbulance(EmergencyRequest request) {
        Ambulance bestMatch = null;
        double minDistance = Double.MAX_VALUE;

        for (Ambulance amb : fleet.values()) {
            if (amb.getState() == AmbulanceState.AVAILABLE && isTypeMatching(request.getPriority(), amb.getType())) {
                double dist = amb.calculateDistance(request.getPickupX(), request.getPickupY());
                if (dist < minDistance) {
                    minDistance = dist;
                    bestMatch = amb;
                }
            }
        }
        return bestMatch;
    }

    private boolean isTypeMatching(EmergencyPriority priority, AmbulanceType ambType) {
        if (priority == EmergencyPriority.CRITICAL) return ambType == AmbulanceType.ICU;
        if (priority == EmergencyPriority.HIGH) return ambType == AmbulanceType.ADVANCED_LIFE_SUPPORT;
        return ambType == AmbulanceType.BASIC || ambType == AmbulanceType.ADVANCED_LIFE_SUPPORT || ambType == AmbulanceType.ICU;
    }

    public synchronized void updateAmbulanceState(String ambulanceId, AmbulanceState newState) throws ResourceUnavailableException {
        Ambulance amb = fleet.get(ambulanceId);
        if (amb == null) throw new ResourceUnavailableException("Ambulance ID record not found.");

        amb.setState(newState);
        System.out.println("[STATE UPDATE] Ambulance " + ambulanceId + " changed state to " + newState);

        if (newState == AmbulanceState.AVAILABLE) {
            processWaitingQueue();
        }
    }

    public List<EmergencyRequest> getHistory() { return new ArrayList<>(history); }
    public int getWaitingQueueSize() { return waitingQueue.size(); }
    public Map<String, Ambulance> getFleet() { return fleet; }
}
