package com.alpacaflow.meditrack.devices.devices.domain.model.commands;

/**
 * Command to append an immutable clinical audit log entry.
 */
public record RecordClinicalAuditLogCommand(CreateAlertCommand alertCommand, Long alertId) {

    public RecordClinicalAuditLogCommand {
        if (alertCommand == null) {
            throw new IllegalArgumentException("Alert command cannot be null");
        }
        if (alertId == null || alertId <= 0) {
            throw new IllegalArgumentException("Alert ID must be positive");
        }
    }
}
