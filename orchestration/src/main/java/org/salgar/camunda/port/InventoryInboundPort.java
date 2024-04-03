package org.salgar.camunda.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.inventory.response.InventoryResponse;

public interface InventoryInboundPort {
    void processInventoryResponse(Message<InventoryResponse> message);
}
