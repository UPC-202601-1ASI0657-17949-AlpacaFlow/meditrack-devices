package com.alpacaflow.meditrack.devices.devices.infrastructure.messaging;

import com.alpacaflow.meditrack.devices.devices.domain.model.commands.AddHeartRateMeasurementToDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.AddOxygenMeasurementToDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.domain.model.commands.AddTemperatureMeasurementToDeviceCommand;
import com.alpacaflow.meditrack.devices.devices.domain.services.DeviceCommandService;
import com.alpacaflow.meditrack.devices.shared.infrastructure.mqtt.TelemetryReceivedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Consumes vital-sign telemetry from Mosquitto (FOG) and persists via domain commands.
 */
@Component
@ConditionalOnProperty(name = "app.mqtt.enabled", havingValue = "true")
public class TelemetryMqttMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryMqttMessageHandler.class);

    private final DeviceCommandService deviceCommandService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelemetryMqttMessageHandler(DeviceCommandService deviceCommandService) {
        this.deviceCommandService = deviceCommandService;
    }

    @ServiceActivator(inputChannel = "mqttTelemetryInputChannel")
    public void handle(Message<String> message) {
        var payload = message.getPayload();
        try {
            var telemetry = objectMapper.readValue(payload, TelemetryReceivedMessage.class);
            applyTelemetry(telemetry);
        } catch (Exception exception) {
            LOGGER.warn("Unable to process MQTT telemetry payload: {}", exception.getMessage());
        }
    }

    private void applyTelemetry(TelemetryReceivedMessage telemetry) {
        if (telemetry.deviceId() == null || telemetry.deviceId() <= 0) {
            LOGGER.warn("MQTT telemetry missing deviceId; payload ignored");
            return;
        }

        var deviceId = telemetry.deviceId();

        if (telemetry.heartRate() != null) {
            deviceCommandService.handle(new AddHeartRateMeasurementToDeviceCommand(
                    telemetry.heartRate(), deviceId));
            LOGGER.debug("MQTT heart rate stored for deviceId={} bpm={}", deviceId, telemetry.heartRate());
        }

        if (telemetry.temperature() != null) {
            deviceCommandService.handle(new AddTemperatureMeasurementToDeviceCommand(
                    telemetry.temperature(), deviceId));
            LOGGER.debug("MQTT temperature stored for deviceId={}", deviceId);
        }

        if (telemetry.oxygen() != null) {
            deviceCommandService.handle(new AddOxygenMeasurementToDeviceCommand(
                    telemetry.oxygen(), deviceId));
            LOGGER.debug("MQTT oxygen stored for deviceId={}", deviceId);
        }

        LOGGER.info(
                "MQTT telemetry applied for deviceId={} localDeviceId={}",
                deviceId,
                telemetry.localDeviceId());
    }
}
