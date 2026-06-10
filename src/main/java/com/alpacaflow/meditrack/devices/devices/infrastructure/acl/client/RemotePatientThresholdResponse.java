package com.alpacaflow.meditrack.devices.devices.infrastructure.acl.client;

/**
 * DTO for patient threshold responses from meditrack-clinical.
 */
public record RemotePatientThresholdResponse(
        Long id,
        Long seniorCitizenId,
        int minBpm,
        int maxBpm,
        int minSpo2,
        double minCelsius,
        double maxCelsius
) {
}
