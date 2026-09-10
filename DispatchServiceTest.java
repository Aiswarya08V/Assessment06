package com.dispatch.service;

import com.dispatch.model.*;
import com.dispatch.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DispatchServiceTest {
    private DispatchService service;

    @BeforeEach
    public void setup() {
        service = new DispatchService();
    }

    @Test
    public void testPriorityQueueDispatching() {
        service.registerAmbulance(new Ambulance("AMB-ICU", AmbulanceType.ICU, "Driver1", "123", 0, 0));

        EmergencyRequest highPriority = new EmergencyRequest("R-01", "P-01", "Trauma", EmergencyPriority.CRITICAL, 1, 1, "HospA");
        service.submitEmergencyRequest(highPriority);

        assertEquals("AMB-ICU", highPriority.getAssignedAmbulanceId());
        assertEquals("DISPATCHED", highPriority.getStatus());
    }

    @Test
    public void testWaitingQueueAndAutoAllocation() throws Exception {
        service.registerAmbulance(new Ambulance("AMB-BASIC", AmbulanceType.BASIC, "Driver2", "456", 0, 0));

        EmergencyRequest r1 = new EmergencyRequest("R-02", "P-02", "Minor", EmergencyPriority.NORMAL, 1, 1, "HospB");
        EmergencyRequest r2 = new EmergencyRequest("R-03", "P-03", "Moderate", EmergencyPriority.MODERATE, 2, 2, "HospB");

        service.submitEmergencyRequest(r1); // Takes the only available ambulance
        service.submitEmergencyRequest(r2); // Must go to queue

        assertEquals(1, service.getWaitingQueueSize());

        // Free up the ambulance
        service.updateAmbulanceState("AMB-BASIC", AmbulanceState.AVAILABLE);

        assertEquals(0, service.getWaitingQueueSize());
        assertEquals("AMB-BASIC", r2.getAssignedAmbulanceId());
    }

    @Test
    public void testInvalidRequestException() {
        assertThrows(InvalidRequestException.class, () -> {
            service.submitEmergencyRequest(null);
        });
    }
}
