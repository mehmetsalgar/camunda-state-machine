package org.salgar.camunda.inventory.service.port;

public interface RestPort {
    void processInventoryResponse(String correlationId, String response, String sourceProcess);
}
