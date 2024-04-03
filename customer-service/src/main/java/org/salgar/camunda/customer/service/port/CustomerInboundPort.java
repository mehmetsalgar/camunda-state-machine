package org.salgar.camunda.customer.service.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.customer.command.CustomerCommand;

public interface CustomerInboundPort {
    void processCustomerCommand(Message<CustomerCommand> command);
}
