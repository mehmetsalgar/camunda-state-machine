package org.salgar.camunda.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.invoice.response.InvoiceResponse;

public interface InvoiceInboundPort {
    void processInvoiceResponse(Message<InvoiceResponse> message);
}
