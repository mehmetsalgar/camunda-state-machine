package org.salgar.camunda.product.options.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.product.options.command.ProductOptionsCommand;

public interface ProductOptionsInboundPort {
    void processOrderEvent(Message<ProductOptionsCommand> command);
}
