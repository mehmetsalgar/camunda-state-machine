package org.salgar.camunda.inventory.service.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.inventory.command.InventoryCommand;
import org.salgar.camunda.inventory.model.protobuf.OrderItems;
import org.salgar.camunda.inventory.service.core.facades.InventoryReservationFacade;
import org.salgar.camunda.inventory.service.port.InventoryInboundPort;
import org.salgar.camunda.inventory.util.InventoryCommandConstants;
import org.salgar.camunda.inventory.util.PayloadVariableConstants;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import static org.salgar.camunda.inventory.util.SourceProcessConstants.SOURCE_PROCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryInboundAdapter implements InventoryInboundPort {
    @Override
    @PulsarListener(
            subscriptionName = "inventory-inbound-subscription",
            topics = "${spring.pulsar.consumer.inventory.topics}"
    )
    public void processInventoryCommand(Message<InventoryCommand> command) {
        try {
            processInventoryCommandInternal(command);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public void processInventoryCommandInternal(Message<InventoryCommand> command) throws InvalidProtocolBufferException {
        log.info(
                "We received the command: [{}] with key: [{}] and payload: [{}]",
                command.getValue().getCommand(),
                command.getKey(),
                command.getValue().getPayloadMap());

        if(InventoryCommandConstants.RESERVE_PRODUCT.equals(command.getValue().getCommand())) {
            log.info("Reserving Product");
            String orderId = command.getKey();

            OrderItems orderItems = command
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.ORDER_ITEMS).unpack(OrderItems.class);

//            inventoryReservationFacade.reserveProduct(orderId, orderItems);
        } else if(InventoryCommandConstants.REVERT_PRODUCT_RESERVATION.equals(command.getValue().getCommand())) {
            String sourceProcess = String.valueOf(command.getValue().getPayloadMap().get(SOURCE_PROCESS));
            log.info("Reverting Product Reservation for sourceProcess:[{}]", sourceProcess);
            String orderId = command.getKey();

            //inventoryReservationFacade.revertProductReservation(orderId);
        } else if(InventoryCommandConstants.NOTIFY_CUSTOMER_FOR_INTERESTED_PRODUCTS.equals(command.getValue().getCommand())) {
            log.info("Notified Interested Customers for Product");
        } else {
            log.info("Unknown command: [{}]", command.getValue().getCommand());
        }
    }
}
