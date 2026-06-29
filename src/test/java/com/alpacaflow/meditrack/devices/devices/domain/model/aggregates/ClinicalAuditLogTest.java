package com.alpacaflow.meditrack.devices.devices.domain.model.aggregates;

import com.alpacaflow.meditrack.devices.devices.domain.exceptions.ImmutableAuditLogException;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EClinicalAuditActionType;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EThresholdViolation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClinicalAuditLogTest {

    @Test
    void shouldBuildAuditLogFromAlertCommand() {
        var command = new CreateAlertCommand(3L, 118.0, "HEARTRATE", EThresholdViolation.HIGH);

        var auditLog = ClinicalAuditLog.fromAlertCommand(command, 99L);

        assertEquals(3L, auditLog.getDeviceId());
        assertNull(auditLog.getUserId());
        assertEquals(118.0, auditLog.getMetricValue());
        assertEquals(EClinicalAuditActionType.ALERT_GENERATED, auditLog.getActionType());
        assertNotNull(auditLog.getOccurredAt());
        assertTrue(auditLog.getDetails().contains("alertId=99"));
        assertTrue(auditLog.getDetails().contains("measurementType=HEARTRATE"));
    }

    @Test
    void shouldRejectUpdateOrDeleteAttempts() {
        var auditLog = new ClinicalAuditLog(
                1L,
                null,
                95.0,
                EClinicalAuditActionType.ALERT_GENERATED,
                "test"
        );

        assertThrows(ImmutableAuditLogException.class, auditLog::rejectMutation);
    }
}
