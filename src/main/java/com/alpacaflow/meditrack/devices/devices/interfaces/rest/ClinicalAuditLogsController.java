package com.alpacaflow.meditrack.devices.devices.interfaces.rest;

import com.alpacaflow.meditrack.devices.devices.domain.model.queries.GetAllClinicalAuditLogsQuery;
import com.alpacaflow.meditrack.devices.devices.domain.model.queries.GetClinicalAuditLogsByDeviceIdQuery;
import com.alpacaflow.meditrack.devices.devices.domain.services.ClinicalAuditLogQueryService;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.resources.ClinicalAuditLogResource;
import com.alpacaflow.meditrack.devices.devices.interfaces.rest.transform.ClinicalAuditLogResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/audit-logs", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Clinical Audit Logs", description = "Immutable clinical audit trail (TS10)")
public class ClinicalAuditLogsController {

    private final ClinicalAuditLogQueryService clinicalAuditLogQueryService;

    public ClinicalAuditLogsController(ClinicalAuditLogQueryService clinicalAuditLogQueryService) {
        this.clinicalAuditLogQueryService = clinicalAuditLogQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all clinical audit logs", description = "Returns append-only audit entries ordered by occurrence time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit logs found")
    })
    public ResponseEntity<List<ClinicalAuditLogResource>> getAllAuditLogs() {
        var logs = clinicalAuditLogQueryService.handle(new GetAllClinicalAuditLogsQuery());
        var resources = logs.stream()
                .map(ClinicalAuditLogResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "Get audit logs by device ID", description = "Returns immutable audit entries for a device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit logs found")
    })
    public ResponseEntity<List<ClinicalAuditLogResource>> getAuditLogsByDeviceId(@PathVariable Long deviceId) {
        var logs = clinicalAuditLogQueryService.handle(new GetClinicalAuditLogsByDeviceIdQuery(deviceId));
        var resources = logs.stream()
                .map(ClinicalAuditLogResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
