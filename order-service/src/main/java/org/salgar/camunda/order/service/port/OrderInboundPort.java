package org.salgar.camunda.order.service.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.order.command.OrderCommand;

public interface OrderInboundPort {
    void processOrderEvent(Message<OrderCommand> command);
}
