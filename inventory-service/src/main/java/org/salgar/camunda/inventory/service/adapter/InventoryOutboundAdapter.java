package org.salgar.camunda.inventory.service.adapter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.inventory.response.InventoryResponse;
import org.salgar.camunda.inventory.service.port.InventoryOutboundPort;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryOutboundAdapter implements InventoryOutboundPort {
    private final PulsarTemplate<InventoryResponse> pulsarTemplate;
    private final PulsarProperties.Producer inventoryOutboundProperties;

    @Override
    @SneakyThrows
    public void deliverInventoryResponse(String correlationId, InventoryResponse inventoryResponse) {
        log.info("Sending InventoryResponse: [{}]", inventoryResponse);
        pulsarTemplate
                .newMessage(inventoryResponse)
                .withMessageCustomizer(mc -> {
                    mc.key(correlationId);
                })
                .withTopic(inventoryOutboundProperties.getTopicName())
                .send();
    }
}
