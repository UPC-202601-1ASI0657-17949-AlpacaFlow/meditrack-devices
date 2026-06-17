package com.alpacaflow.meditrack.devices.shared.infrastructure.persistence.jpa.configuration;

import com.alpacaflow.meditrack.devices.shared.infrastructure.persistence.jpa.configuration.strategy.SnakeCaseWithPluralizedTablePhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaNamingConfiguration {

    @Bean
    public PhysicalNamingStrategy physicalNamingStrategy() {
        return new SnakeCaseWithPluralizedTablePhysicalNamingStrategy();
    }
}
