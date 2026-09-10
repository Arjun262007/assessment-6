package com.emergency.dispatch;

import com.emergency.dispatch.exception.InvalidRequestException;
import com.emergency.dispatch.model.*;
import com.emergency.dispatch.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DispatchServiceTest {
    private DispatchService dispatchService;

    @BeforeEach
    public void setUp() {
        dispatchService = new DispatchService();
    }

    @Test
    public void testPriorityQueueAllocation() {
        Ambulance basicAmb = new Ambulance("AMB-01", AmbulanceType.BASIC, "Driver A");
        dispatchService.registerAmbulance(basicAmb);

        EmergencyRequest normalReq = new EmergencyRequest("P1", "Fracture", "Loc A", "Hosp X", EmergencyPriority.NORMAL, 10.0);
        EmergencyRequest criticalReq = new EmergencyRequest("P2", "Cardiac Arrest", "Loc B", "Hosp Y", EmergencyPriority.CRITICAL, 15.0);

        dispatchService.submitEmergencyRequest(normalReq); 
        dispatchService.submitEmergencyRequest(criticalReq);

        assertEquals("QUEUED", criticalReq.getStatus());
        assertEquals(criticalReq, dispatchService.getWaitingQueue().peek());
    }

    @Test
    public void testStateTransitionAndReallocation() {
        Ambulance basicAmb = new Ambulance("AMB-01", AmbulanceType.BASIC, "Driver A");
        dispatchService.registerAmbulance(basicAmb);

        EmergencyRequest request1 = new EmergencyRequest("P1", "Incident 1", "Loc A", "Hosp X", EmergencyPriority.HIGH, 5.0);
        EmergencyRequest request2 = new EmergencyRequest("P2", "Incident 2", "Loc B", "Hosp Y", EmergencyPriority.CRITICAL, 12.0);

        dispatchService.submitEmergencyRequest(request1);
        dispatchService.submitEmergencyRequest(request2);

        assertEquals("DISPATCHED", request1.getStatus());
        assertEquals("QUEUED", request2.getStatus());

        dispatchService.updateAmbulanceState("AMB-01", AmbulanceState.AVAILABLE);

        assertEquals("DISPATCHED", request2.getStatus());
        assertTrue(dispatchService.getWaitingQueue().isEmpty());
    }

    @Test
    public void testInvalidRequestValidation() {
        EmergencyRequest invalidReq = new EmergencyRequest("", "Trauma", "", "Hosp Z", EmergencyPriority.MODERATE, -5.0);
        assertThrows(InvalidRequestException.class, () -> dispatchService.submitEmergencyRequest(invalidReq));
    }
}
