package org.salgar.camunda.product.options.adapter;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.product.options.command.ProductOptionsCommand;
import org.salgar.camunda.product.options.port.ProductOptionsInboundPort;
import org.salgar.camunda.product.options.util.CommandConstants;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductOptionsInboundAdapter implements ProductOptionsInboundPort {
    @Override
    @PulsarListener(
            subscriptionName = "product-options-inbound-subscription",
            topics = "${spring.pulsar.consumer.product-options.topics}"
    )
    public void processOrderEvent(Message<ProductOptionsCommand> command) {
        log.info(
                "We received the command: [{}] with payload: [{}]",
                command.getValue().getCommand(),
                command.getValue().getPayloadMap());

            if(CommandConstants.CALCULATE_PRODUCT_OPTIONS.equals(command.getValue().getCommand())) {
                log.info("Calculating Options and caching those!");
            } else {
                log.warn("Received unknown command: [{}]", command);
            }
    }
}
