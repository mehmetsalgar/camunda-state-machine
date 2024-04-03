package org.salgar.camunda.order.service.core.config;

import org.salgar.camunda.order.response.OrderResponse;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.DefaultPulsarProducerFactory;
import org.springframework.pulsar.core.PulsarProducerFactory;

@Configuration
public class PulsarConfig {
    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.order")
    public PulsarProperties.Consumer orderInboundProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.order")
    public PulsarProperties.Producer orderOutboundProperties() {
        return new PulsarProperties.Producer();
    }
}
