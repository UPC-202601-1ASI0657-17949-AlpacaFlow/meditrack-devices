package com.alpacaflow.meditrack.devices.devices.domain.model.queries;

/**
 * Query to retrieve clinical audit logs for a specific device.
 */
public record GetClinicalAuditLogsByDeviceIdQuery(Long deviceId) {

    public GetClinicalAuditLogsByDeviceIdQuery {
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("Device ID must be positive");
        }
    }
}
