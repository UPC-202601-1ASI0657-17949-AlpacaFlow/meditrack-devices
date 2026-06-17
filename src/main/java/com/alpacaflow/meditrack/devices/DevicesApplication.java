package com.alpacaflow.meditrack.devices;

import com.alpacaflow.meditrack.devices.shared.infrastructure.mqtt.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.integration.config.EnableIntegration;

@EnableJpaAuditing
@EnableIntegration
@EnableConfigurationProperties(MqttProperties.class)
@SpringBootApplication
public class DevicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevicesApplication.class, args);
    }

}
