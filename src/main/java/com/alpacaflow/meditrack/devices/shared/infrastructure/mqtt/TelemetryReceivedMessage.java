package com.alpacaflow.meditrack.devices.shared.infrastructure.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Payload published by meditrack-edge to Mosquitto.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TelemetryReceivedMessage(
        Long deviceId,
        Long seniorCitizenId,
        String localDeviceId,
        String recordedAt,
        Integer heartRate,
        Double temperature,
        Integer oxygen,
        BloodPressure bloodPressure
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BloodPressure(Integer systolic, Integer diastolic) {
    }
}
