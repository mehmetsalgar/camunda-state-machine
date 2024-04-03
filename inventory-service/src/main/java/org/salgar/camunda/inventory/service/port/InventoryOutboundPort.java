package org.salgar.camunda.inventory.service.port;

import org.salgar.camunda.inventory.response.InventoryResponse;

public interface InventoryOutboundPort {
    void deliverInventoryResponse(String correlationId, InventoryResponse inventoryResponse);
}
