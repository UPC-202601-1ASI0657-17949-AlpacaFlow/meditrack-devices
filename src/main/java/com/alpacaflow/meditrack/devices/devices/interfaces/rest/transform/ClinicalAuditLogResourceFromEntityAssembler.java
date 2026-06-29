package com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.ClinicalAuditLog;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.ClinicalAuditLogResource;

public class ClinicalAuditLogResourceFromEntityAssembler {

    private ClinicalAuditLogResourceFromEntityAssembler() {
    }

    public static ClinicalAuditLogResource toResourceFromEntity(ClinicalAuditLog entity) {
        return new ClinicalAuditLogResource(
                entity.getId(),
                entity.getOccurredAt().toString(),
                entity.getDeviceId(),
                entity.getUserId(),
                entity.getMetricValue(),
                entity.getActionType().name(),
                entity.getDetails()
        );
    }
}
