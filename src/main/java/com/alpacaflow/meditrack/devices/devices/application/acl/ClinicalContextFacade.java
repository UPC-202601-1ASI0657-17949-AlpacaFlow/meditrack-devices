package com.alpacaflow.meditrack.devices.devices.application.acl;

import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.PatientThresholdSnapshot;

import java.util.Optional;

/**
 * Anti-corruption layer for patient thresholds stored in the clinical bounded context.
 */
public interface ClinicalContextFacade {

    Optional<PatientThresholdSnapshot> fetchPatientThreshold(Long seniorCitizenId);
}
