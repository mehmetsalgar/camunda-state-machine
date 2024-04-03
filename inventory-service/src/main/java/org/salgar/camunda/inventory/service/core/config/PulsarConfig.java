package org.salgar.camunda.inventory.service.core.config;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.inventory")
    public PulsarProperties.Consumer inventoryInboundProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.inventory")
    public PulsarProperties.Producer inventoryOutboundProperties() {
        return new PulsarProperties.Producer();
    }
}
