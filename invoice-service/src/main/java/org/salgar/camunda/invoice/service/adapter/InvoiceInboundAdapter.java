package org.salgar.camunda.invoice.service.adapter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.invoice.command.InvoiceCommand;
import org.salgar.camunda.invoice.command.SourceProcess;
import org.salgar.camunda.invoice.service.port.InvoiceInboundPort;
import org.salgar.camunda.invoice.utils.InvoiceCommandConstants;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import static org.salgar.camunda.invoice.utils.SourceProcessConstants.SOURCE_PROCESS;

@Component
@Slf4j
public class InvoiceInboundAdapter implements InvoiceInboundPort {
    @Override
    @PulsarListener(
            subscriptionName = "invoice-inbound-subscription",
            topics = "${spring.pulsar.consumer.invoice.topics}"
    )
    @SneakyThrows
    public void processInventoryCommand(Message<InvoiceCommand> command) {
        log.info(
                "We received the command: [{}] with payload: [{}]",
                command.getValue().getCommand(),
                command.getValue().getPayloadMap());

        if(InvoiceCommandConstants.CREATE_INVOICE.equals(command.getValue().getCommand())) {
            log.info("Creating Invoice");
        } else if(InvoiceCommandConstants.CANCEL_INVOICE.equals(command.getValue().getCommand())) {
            SourceProcess sourceProcess = command
                    .getValue()
                    .getPayloadMap()
                    .get(SOURCE_PROCESS)
                    .unpack(SourceProcess.class);

            log.info("Canceling Invoice for sourceProcess:[{}]", sourceProcess);
        } else {
            log.info("Unknown command: [{}]", command.getValue().getCommand());
        }
    }
}
