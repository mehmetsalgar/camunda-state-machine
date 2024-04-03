package org.salgar.camunda.product.options.adapter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.product.options.port.ProductOptionsOutboundPort;
import org.salgar.camunda.product.options.response.ProductOptionsResponse;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOptionsOutboundAdapter implements ProductOptionsOutboundPort {
    private final PulsarTemplate<ProductOptionsResponse> pulsarTemplate;
    private final PulsarProperties.Producer productOptionsOutboundProperties;

    @Override
    @SneakyThrows
    public void deliverProductOptionsResponse(String correlationId, ProductOptionsResponse productOptionsResponse) {
        log.info("Sending ProductOptionsResponse: [{}]", productOptionsResponse);
        pulsarTemplate
                .newMessage(productOptionsResponse)
                .withMessageCustomizer(mc -> {
                    mc.key(correlationId);
                })
                .withTopic(productOptionsOutboundProperties.getTopicName())
                .send();
    }
}
