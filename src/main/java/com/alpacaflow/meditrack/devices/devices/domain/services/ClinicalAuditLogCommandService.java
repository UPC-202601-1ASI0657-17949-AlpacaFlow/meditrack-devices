package com.alpacaflow.meditrack.devices.devices.domain.services;

import com.alpacaflow.meditrack.devices.devices.domain.model.commands.RecordClinicalAuditLogCommand;

/**
 * Command service for append-only clinical audit logs (TS10).
 */
public interface ClinicalAuditLogCommandService {

    /**
     * Persists an immutable audit log entry.
     *
     * @param command audit payload derived from a generated alert
     * @return persisted log id
     */
    Long handle(RecordClinicalAuditLogCommand command);
}
