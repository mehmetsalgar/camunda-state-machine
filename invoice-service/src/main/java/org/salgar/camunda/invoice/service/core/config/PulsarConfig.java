package org.salgar.camunda.invoice.service.core.config;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.invoice")
    public PulsarProperties.Consumer invoiceInboundProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.invoice")
    public PulsarProperties.Producer invoiceOutboundProperties() {
        return new PulsarProperties.Producer();
    }
}
