package com.alpacaflow.meditrack.devices.shared.infrastructure.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@ConditionalOnProperty(name = "app.mqtt.enabled", havingValue = "true")
public class MqttInboundConfiguration {

    @Bean
    public MqttPahoClientFactory mqttClientFactory(MqttProperties properties) {
        var factory = new DefaultMqttPahoClientFactory();
        var options = new MqttConnectOptions();
        options.setServerURIs(new String[] { properties.brokerUrl() });
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        if (properties.username() != null && !properties.username().isBlank()) {
            options.setUserName(properties.username());
            options.setPassword(properties.password().toCharArray());
        }
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttTelemetryInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttTelemetryInbound(
            MqttPahoClientFactory mqttClientFactory,
            MessageChannel mqttTelemetryInputChannel,
            MqttProperties properties) {
        var adapter = new MqttPahoMessageDrivenChannelAdapter(
                properties.clientId() + "-in",
                mqttClientFactory,
                properties.topic());
        adapter.setCompletionTimeout(5_000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttTelemetryInputChannel);
        return adapter;
    }
}
