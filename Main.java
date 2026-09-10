package com.dispatch;

import com.dispatch.model.*;
import com.dispatch.service.DispatchService;

public class Main {
    public static void main(String[] args) throws Exception {
        DispatchService service = new DispatchService();

        // Register Fleet
        service.registerAmbulance(new Ambulance("AMB-01", AmbulanceType.BASIC, "John Doe", "555-0101", 0.0, 0.0));
        service.registerAmbulance(new Ambulance("AMB-02", AmbulanceType.ADVANCED_LIFE_SUPPORT, "Jane Smith", "555-0102", 2.0, 2.0));
        service.registerAmbulance(new Ambulance("AMB-03", AmbulanceType.ICU, "Bob Johnson", "555-0103", 5.0, 5.0));

        System.out.println("--- Submitting Requests ---");
        EmergencyRequest r1 = new EmergencyRequest("REQ-1", "P-101", "Cardiac Arrest", EmergencyPriority.CRITICAL, 4.5, 4.5, "City Gen Hospital");
        EmergencyRequest r2 = new EmergencyRequest("REQ-2", "P-102", "Fracture", EmergencyPriority.NORMAL, 1.0, 1.0, "General Clinic");
        EmergencyRequest r3 = new EmergencyRequest("REQ-3", "P-103", "Severe Burn", EmergencyPriority.CRITICAL, 6.0, 6.0, "Burn Care Center");

        service.submitEmergencyRequest(r1); // Will match ICU
        service.submitEmergencyRequest(r2); // Will match Basic
        service.submitEmergencyRequest(r3); // No ICU available, goes to waiting queue

        System.out.println("Waiting Queue Count: " + service.getWaitingQueueSize());

        System.out.println("\n--- Freeing up ICU Ambulance ---");
        service.updateAmbulanceState("AMB-03", AmbulanceState.AVAILABLE); // Automatically triggers allocation for REQ-3
        
        System.out.println("Waiting Queue Count after return: " + service.getWaitingQueueSize());
    }
}
