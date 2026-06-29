package com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources;

/**
 * REST resource for an immutable clinical audit log entry (TS10).
 */
public record ClinicalAuditLogResource(
        Long id,
        String occurredAt,
        Long deviceId,
        Long userId,
        double metricValue,
        String actionType,
        String details
) {
}
