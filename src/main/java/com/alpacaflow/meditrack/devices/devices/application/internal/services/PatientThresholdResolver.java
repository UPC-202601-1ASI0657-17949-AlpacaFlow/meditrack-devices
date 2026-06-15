package com.alpacaflow.meditrack.devices.devices.application.internal.services;

import com.alpacaflow.meditrack.devices.devices.application.acl.ClinicalContextFacade;
import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;
import org.springframework.stereotype.Service;

@Service
public class PatientThresholdResolver {

    private final ClinicalContextFacade clinicalContextFacade;

    public PatientThresholdResolver(ClinicalContextFacade clinicalContextFacade) {
        this.clinicalContextFacade = clinicalContextFacade;
    }

    public PatientThresholdSnapshot resolveForDevice(Device device) {
        if (device == null || device.getHolder() == null) {
            return PatientThresholdSnapshot.defaults();
        }

        return clinicalContextFacade.fetchPatientThreshold(device.getHolder().holderId())
                .orElseGet(PatientThresholdSnapshot::defaults);
    }
}
