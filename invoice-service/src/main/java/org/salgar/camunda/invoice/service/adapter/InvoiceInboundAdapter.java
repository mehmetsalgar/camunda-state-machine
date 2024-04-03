package org.salgar.camunda.invoice.service.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.invoice.command.InvoiceCommand;
import org.salgar.camunda.invoice.service.port.InvoiceInboundPort;
import org.salgar.camunda.invoice.utils.InvoiceCommandConstants;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InvoiceInboundAdapter implements InvoiceInboundPort {
    @Override
    @PulsarListener(
            subscriptionName = "invoice-inbound-subscription",
            topics = "${spring.pulsar.consumer.invoice.topics}"
    )
    public void processInventoryCommand(Message<InvoiceCommand> command) {
        log.info(
                "We received the command: [{}] with payload: [{}]",
                command.getValue().getCommand(),
                command.getValue().getPayloadMap());

        if(InvoiceCommandConstants.CREATE_INVOICE.equals(command.getValue().getCommand())) {
            log.info("Creating Invoice");
        } else if(InvoiceCommandConstants.CANCEL_INVOICE.equals(command.getValue().getCommand())) {
            log.info("Canceling Invoice");
        } else {
            log.info("Unknown command: [{}]", command.getValue().getCommand());
        }
    }
}
