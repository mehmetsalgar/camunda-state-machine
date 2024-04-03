package org.salgar.camunda.customer.service.core.config;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.customer")
    public PulsarProperties.Consumer customerInboundProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.customer")
    public PulsarProperties.Producer customerOutboundProperties() {
        return new PulsarProperties.Producer();
    }
}
