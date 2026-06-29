package com.alpacaflow.meditrack.devices.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.ClinicalAuditLog;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.RecordClinicalAuditLogCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EThresholdViolation;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.ClinicalAuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicalAuditLogCommandServiceImplTest {

    @Mock
    private ClinicalAuditLogRepository clinicalAuditLogRepository;

    @InjectMocks
    private ClinicalAuditLogCommandServiceImpl clinicalAuditLogCommandService;

    @Test
    void shouldPersistAuditLogForGeneratedAlert() {
        var alertCommand = new CreateAlertCommand(2L, 38.9, "TEMPERATURE", EThresholdViolation.HIGH);
        var recordCommand = new RecordClinicalAuditLogCommand(alertCommand, 10L);

        when(clinicalAuditLogRepository.save(any(ClinicalAuditLog.class)))
                .thenAnswer(invocation -> {
                    ClinicalAuditLog log = invocation.getArgument(0);
                    var idField = ClinicalAuditLog.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(log, 55L);
                    return log;
                });

        var id = clinicalAuditLogCommandService.handle(recordCommand);

        assertEquals(55L, id);
        var captor = ArgumentCaptor.forClass(ClinicalAuditLog.class);
        verify(clinicalAuditLogRepository).save(captor.capture());
        assertEquals(2L, captor.getValue().getDeviceId());
        assertEquals(38.9, captor.getValue().getMetricValue());
    }
}
