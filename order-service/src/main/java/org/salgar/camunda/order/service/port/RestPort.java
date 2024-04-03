package org.salgar.camunda.order.service.port;

public interface RestPort {
    void prepareOrderServiceResponse(String correlationId, String response);
}
