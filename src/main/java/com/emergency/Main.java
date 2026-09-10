package com.emergency;

import com.emergency.dispatch.model.*;
import com.emergency.dispatch.service.DispatchService;

public class Main {
    public static void main(String[] args) {
        DispatchService service = new DispatchService();

        service.registerAmbulance(new Ambulance("AMB-ICU", AmbulanceType.ICU, "John Doe"));
        service.registerAmbulance(new Ambulance("AMB-BASIC", AmbulanceType.BASIC, "Jane Smith"));

        System.out.println("Emergency System Initialized successfully.");
    }
}
