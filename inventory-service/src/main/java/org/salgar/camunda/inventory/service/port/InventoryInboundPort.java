package org.salgar.camunda.inventory.service.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.inventory.command.InventoryCommand;

public interface InventoryInboundPort {
    void processInventoryCommand(Message<InventoryCommand> command);
}
