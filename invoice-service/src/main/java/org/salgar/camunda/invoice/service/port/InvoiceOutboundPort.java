package org.salgar.camunda.invoice.service.port;

import org.salgar.camunda.invoice.response.InvoiceResponse;

public interface InvoiceOutboundPort {
    void deliverInvoiceResponse(String correlationId, InvoiceResponse inventoryResponse);
}
