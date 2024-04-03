package org.salgar.camunda.customer.service.adapter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.customer.response.CustomerResponse;
import org.salgar.camunda.customer.service.port.CustomerOutboundPort;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerOutboundAdapter implements CustomerOutboundPort {
    private final PulsarTemplate<CustomerResponse> pulsarTemplate;
    private final PulsarProperties.Producer customerOutboundProperties;

    @Override
    @SneakyThrows
    public void deliverCustomerResponse(String correlationId, CustomerResponse customerResponse) {
        log.info("Sending CustomerResponse: [{}]", customerResponse);
        pulsarTemplate
                .newMessage(customerResponse)
                .withMessageCustomizer(mc -> {
                    mc.key(correlationId);
                })
                .withTopic(customerOutboundProperties.getTopicName())
                .send();
    }
}
