package org.salgar.camunda.order.service.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.order.command.OrderCommand;
import org.salgar.camunda.order.service.port.OrderInboundPort;
import org.salgar.camunda.order.util.CommandConstants;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderInboundAdapter implements OrderInboundPort {
    @Override
    @PulsarListener(
            subscriptionName = "order-inbound-subscription",
            topics = "${spring.pulsar.consumer.order.topics}"
    )
    public void processOrderEvent(Message<OrderCommand> command) {
        log.info(
                "We received the command: [{}] with payload: [{}]",
                command.getValue().getCommand(),
                command.getValue().getPayloadMap());

        if(CommandConstants.CREATE_ORDER.equals(command.getValue().getCommand())) {
            log.info("Creating Order");
        } else if(CommandConstants.COMPLETE_ORDER.equals(command.getValue().getCommand())) {
            log.info("Completing Order");
        } else {
            log.info("Unknown command: [{}]", command.getValue().getCommand());
        }
    }
}
