package com.alpacaflow.meditrack.devices.devices.domain.model.events;

import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EThresholdViolation;

/**
 * Domain event triggered when an alert is created
 * @param deviceId The device ID that generated the alert
 * @param dataRegistered The measurement value that triggered the alert
 * @param measurementType The type of measurement
 * @param violation Direction of the threshold breach, when applicable
 */
public record AlertCreatedEvent(
        Long deviceId,
        double dataRegistered,
        String measurementType,
        EThresholdViolation violation
) {
}
