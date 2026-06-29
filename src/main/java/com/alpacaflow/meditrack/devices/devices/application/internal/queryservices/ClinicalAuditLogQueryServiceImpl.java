package com.alpacaflow.meditrack.devices.devices.application.internal.queryservices;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.ClinicalAuditLog;
import com.alpacaflow.meditrack.devices.devices.domain.model.queries.GetAllClinicalAuditLogsQuery;
import com.alpacaflow.meditrack.devices.devices.domain.model.queries.GetClinicalAuditLogsByDeviceIdQuery;
import com.alpacaflow.meditrack.devices.devices.domain.services.ClinicalAuditLogQueryService;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.ClinicalAuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicalAuditLogQueryServiceImpl implements ClinicalAuditLogQueryService {

    private final ClinicalAuditLogRepository clinicalAuditLogRepository;

    public ClinicalAuditLogQueryServiceImpl(ClinicalAuditLogRepository clinicalAuditLogRepository) {
        this.clinicalAuditLogRepository = clinicalAuditLogRepository;
    }

    @Override
    public List<ClinicalAuditLog> handle(GetAllClinicalAuditLogsQuery query) {
        return clinicalAuditLogRepository.findAllByOrderByOccurredAtDesc();
    }

    @Override
    public List<ClinicalAuditLog> handle(GetClinicalAuditLogsByDeviceIdQuery query) {
        return clinicalAuditLogRepository.findByDeviceIdOrderByOccurredAtDesc(query.deviceId());
    }
}
