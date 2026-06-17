package com.alpacaflow.meditrack.devices.shared.infrastructure.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mqtt")
public record MqttProperties(
        boolean enabled,
        String brokerUrl,
        String topic,
        String clientId,
        String username,
        String password
) {
    public MqttProperties {
        if (brokerUrl == null || brokerUrl.isBlank()) {
            brokerUrl = "tcp://localhost:1883";
        }
        if (topic == null || topic.isBlank()) {
            topic = MqttTopicNames.TELEMETRY;
        }
        if (clientId == null || clientId.isBlank()) {
            clientId = "meditrack-devices";
        }
    }
}
