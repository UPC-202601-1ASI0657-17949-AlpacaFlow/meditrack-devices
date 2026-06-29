package com.alpacaflow.meditrack.devices.devices.application.internal.commandservices;


import com.alpacaflow.meditrack.devices.devices.domain.model.aggregates.Alert;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrack.devices.devices.domain.services.AlertCommandService;
import com.alpacaflow.meditrack.devices.devices.infrastructure.persistence.jpa.repositories.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of the AlertCommandService interface.
 * <p>This class is responsible for handling the commands related to the Alert aggregate. It requires an AlertRepository.</p>
 * @see AlertCommandService
 * @see AlertRepository
 */
@Service
public class AlertCommandServiceImpl implements AlertCommandService {
    private static final int ALERT_COOLDOWN_MINUTES = 5;

    private final AlertRepository alertRepository;

    public AlertCommandServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public Long handle(CreateAlertCommand command) {
        var dedupePrefix = Alert.dedupePrefix(command.measurementType(), command.violation());
        var cooldownStart = LocalDateTime.now().minusMinutes(ALERT_COOLDOWN_MINUTES);
        if (alertRepository.existsRecentAlertWithPrefix(command.deviceId(), dedupePrefix, cooldownStart)) {
            return null;
        }

        var alert = new Alert(command);
        try {
            alertRepository.save(alert);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving alert: %s".formatted(e.getMessage()));
        }
        return alert.getId();
    }
}
