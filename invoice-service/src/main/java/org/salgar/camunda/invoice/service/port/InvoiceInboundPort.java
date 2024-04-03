package org.salgar.camunda.invoice.service.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.invoice.command.InvoiceCommand;


public interface InvoiceInboundPort {
    void processInventoryCommand(Message<InvoiceCommand> command);
}
