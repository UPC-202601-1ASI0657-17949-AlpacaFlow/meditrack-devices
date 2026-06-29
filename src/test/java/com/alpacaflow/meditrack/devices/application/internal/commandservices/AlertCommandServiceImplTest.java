package com.alpacaflow.meditrack.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.application.internal.commandservices.AlertCommandServiceImpl;
import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Alert;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.RecordClinicalAuditLogCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.valueobjects.EThresholdViolation;
import com.alpacaflow.meditrack.devices.devices.domain.services.ClinicalAuditLogCommandService;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertCommandServiceImplTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private ClinicalAuditLogCommandService clinicalAuditLogCommandService;

    @InjectMocks
    private AlertCommandServiceImpl alertCommandService;

    @Test
    void shouldSaveAlertSuccessfully() {
        var command = new CreateAlertCommand(1L, 39.5, "TEMPERATURE", EThresholdViolation.HIGH);

        when(alertRepository.existsRecentAlertWithPrefix(eq(1L), eq("High temperature recorded:"), any(LocalDateTime.class)))
                .thenReturn(false);
        when(alertRepository.save(any(Alert.class)))
                .thenAnswer(invocation -> {
                    Alert saved = invocation.getArgument(0);
                    var idField = Alert.class.getSuperclass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(saved, 42L);
                    return saved;
                });

        alertCommandService.handle(command);

        verify(alertRepository).save(any(Alert.class));
        verify(clinicalAuditLogCommandService).handle(any(RecordClinicalAuditLogCommand.class));
    }

    @Test
    void shouldSkipDuplicateAlertWithinCooldown() {
        var command = new CreateAlertCommand(1L, 35.9, "TEMPERATURE", EThresholdViolation.LOW);

        when(alertRepository.existsRecentAlertWithPrefix(eq(1L), eq("Low temperature recorded:"), any(LocalDateTime.class)))
                .thenReturn(true);

        alertCommandService.handle(command);

        verify(alertRepository, never()).save(any(Alert.class));
        verify(clinicalAuditLogCommandService, never()).handle(any(RecordClinicalAuditLogCommand.class));
    }
}
