package com.alpacaflow.meditrack.devices.devices.domain.services;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.ClinicalAuditLog;
import com.alpacaflow.meditrack.devices.devices.domain.model.queries.GetAllClinicalAuditLogsQuery;
import com.alpacaflow.meditrack.devices.devices.domain.model.queries.GetClinicalAuditLogsByDeviceIdQuery;

import java.util.List;

/**
 * Query service for clinical audit logs (TS10).
 */
public interface ClinicalAuditLogQueryService {

    List<ClinicalAuditLog> handle(GetAllClinicalAuditLogsQuery query);

    List<ClinicalAuditLog> handle(GetClinicalAuditLogsByDeviceIdQuery query);
}
