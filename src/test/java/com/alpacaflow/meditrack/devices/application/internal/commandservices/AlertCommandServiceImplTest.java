package com.alpacaflow.meditrack.devices.application.internal.commandservices;

import com.alpacaflow.meditrack.devices.devices.application.internal.commandservices.AlertCommandServiceImpl;
import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Alert;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertCommandServiceImplTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertCommandServiceImpl alertCommandService;

    @Test
    void shouldSaveAlertSuccessfully() {
        var command = new CreateAlertCommand(1L, 39.5, "TEMPERATURE");

        when(alertRepository.save(any(Alert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        alertCommandService.handle(command);

        verify(alertRepository).save(any(Alert.class));
    }
}
