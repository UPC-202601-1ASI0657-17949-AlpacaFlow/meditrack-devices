package com.alpacaflow.meditrack.devices.devices.domain.model.aggregates;

import com.alpacaflow.meditrack.devices.devices.domain.exceptions.ImmutableAuditLogException;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EClinicalAuditActionType;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EThresholdViolation;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Immutable audit trail for critical clinical IoT events (TS10).
 */
@Getter
@Entity
@Table(name = "clinical_audit_logs")
public class ClinicalAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "metric_value", nullable = false)
    private double metricValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private EClinicalAuditActionType actionType;

    @Column(length = 500)
    private String details;

    protected ClinicalAuditLog() {
    }

    public ClinicalAuditLog(
            Long deviceId,
            Long userId,
            double metricValue,
            EClinicalAuditActionType actionType,
            String details) {
        if (deviceId == null) {
            throw new IllegalArgumentException("Device ID cannot be null");
        }
        if (actionType == null) {
            throw new IllegalArgumentException("Action type cannot be null");
        }
        this.occurredAt = LocalDateTime.now();
        this.deviceId = deviceId;
        this.userId = userId;
        this.metricValue = metricValue;
        this.actionType = actionType;
        this.details = details == null ? "" : details;
    }

    public static ClinicalAuditLog fromAlertCommand(CreateAlertCommand command, Long alertId) {
        var violation = command.violation() == null ? "N/A" : command.violation().name();
        var details = "alertId=%s;measurementType=%s;violation=%s"
                .formatted(alertId, command.measurementType(), violation);
        return new ClinicalAuditLog(
                command.deviceId(),
                null,
                command.dataRegistered(),
                EClinicalAuditActionType.ALERT_GENERATED,
                details
        );
    }

    @PreUpdate
    @PreRemove
    void rejectMutation() {
        throw new ImmutableAuditLogException();
    }
}
