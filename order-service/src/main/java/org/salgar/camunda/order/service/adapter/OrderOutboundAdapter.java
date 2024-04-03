package org.salgar.camunda.order.service.adapter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.order.response.OrderResponse;
import org.salgar.camunda.order.service.port.OrderOutboundPort;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboundAdapter implements OrderOutboundPort {
    private final PulsarTemplate<OrderResponse> pulsarTemplate;
    private final PulsarProperties.Producer orderOutboundProperties;

    @Override
    @SneakyThrows
    public void deliverOrderResponse(
            String correlationId,
            OrderResponse orderResponse) {

        log.info("Sending OrderResponse: [{}]", orderResponse);
        pulsarTemplate
                .newMessage(orderResponse)
                .withMessageCustomizer(mc -> {
                    mc.key(correlationId);
                })
                .withTopic(orderOutboundProperties.getTopicName())
                .send();
    }
}
