package org.salgar.camunda.invoice.service.port;

public interface RestPort {
    void processInvoiceResponse(String correlationId, String response, String sourceProcess);
}
