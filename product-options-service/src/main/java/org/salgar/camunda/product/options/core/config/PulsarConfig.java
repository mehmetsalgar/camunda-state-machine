package org.salgar.camunda.product.options.core.config;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarConfig {
    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.product-options")
    public PulsarProperties.Consumer productOptionsInboundProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.product-options")
    public PulsarProperties.Producer productOptionsOutboundProperties() {
        return new PulsarProperties.Producer();
    }
}
