package com.alpacaflow.meditrack.devices.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.ClinicalAuditLog;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.RecordClinicalAuditLogCommand;
import com.alpacaflow.meditrack.devices.devices.domain.services.ClinicalAuditLogCommandService;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.ClinicalAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicalAuditLogCommandServiceImpl implements ClinicalAuditLogCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicalAuditLogCommandServiceImpl.class);

    private final ClinicalAuditLogRepository clinicalAuditLogRepository;

    public ClinicalAuditLogCommandServiceImpl(ClinicalAuditLogRepository clinicalAuditLogRepository) {
        this.clinicalAuditLogRepository = clinicalAuditLogRepository;
    }

    @Override
    @Transactional
    public Long handle(RecordClinicalAuditLogCommand command) {
        var auditLog = ClinicalAuditLog.fromAlertCommand(command.alertCommand(), command.alertId());
        try {
            clinicalAuditLogRepository.save(auditLog);
        } catch (Exception exception) {
            LOGGER.error("Failed to persist clinical audit log for alertId={}: {}",
                    command.alertId(), exception.getMessage());
            throw new IllegalArgumentException("Error saving clinical audit log: %s".formatted(exception.getMessage()));
        }
        return auditLog.getId();
    }
}
