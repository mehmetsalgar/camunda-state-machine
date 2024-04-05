package org.salgar.camunda.customer.service.port;

public interface RestPort {
    void processCustomerResponse(String correlationId, String response, String sourceProcess);
}
