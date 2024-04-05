package org.salgar.camunda.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.salgar.camunda.core.mapping.OrchestrationInventory2Inventory;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.customer.util.SourceProcessConstants;
import org.salgar.camunda.inventory.command.SourceProcess;
import org.salgar.camunda.inventory.model.protobuf.OrderItems;
import org.salgar.camunda.inventory.util.InventoryCommandConstants;
import org.salgar.camunda.inventory.util.PayloadVariableConstants;
import org.salgar.camunda.port.InventoryOutboundPort;
import org.salgar.camunda.inventory.command.InventoryCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryOutboundAdapter implements InventoryOutboundPort {
    private final Producer<InventoryCommand> pulsarInventoryProducer;
    private final OrchestrationInventory2Inventory orchestrationInventory2Inventory;

    @Override
    @SneakyThrows
    public void reserveProductInInventory(String correlationId, List<OrderItem> orderItems) {
        log.info("Reserving Product in Inventory: [{}]", orderItems);
        log.info("Key: [{}]", correlationId);

        log.info("Mapping");
        OrderItems.Builder orderItemsBuilder = OrderItems.newBuilder();
        for (OrderItem orderItem : orderItems) {
            orderItemsBuilder.addOrderItem(orchestrationInventory2Inventory.map(orderItem));
        }
        OrderItems orderItemsExternal = orderItemsBuilder.build();

        log.info("Creating Command");
        InventoryCommand.Builder builder = InventoryCommand.newBuilder();
        builder.setCommand(InventoryCommandConstants.RESERVE_PRODUCT);
        builder.putPayload(
                PayloadVariableConstants.ORDER_ITEMS,
                Any.pack(orderItemsExternal)
        );

        pulsarInventoryProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(builder.build())
                .send();

        log.info("Reserve Product Command sent: [{}]", orderItemsExternal);
    }

    @Override
    @SneakyThrows
    public void revertProductReservation(String correlationId, List<OrderItem> orderItems, String sourceProcess) {
        log.info("Reverting Product Reservation in Inventory: [{}]", orderItems);
        log.info("Key: [{}]", correlationId);

        log.info("Mapping");
        OrderItems.Builder orderItemsBuilder = OrderItems.newBuilder();
        for (OrderItem orderItem : orderItems) {
            orderItemsBuilder.addOrderItem(orchestrationInventory2Inventory.map(orderItem));
        }
        OrderItems orderItemsExternal = orderItemsBuilder.build();

        log.info("Creating Command");
        InventoryCommand.Builder builder = InventoryCommand.newBuilder();
        builder.setCommand(InventoryCommandConstants.REVERT_PRODUCT_RESERVATION);
        builder.putPayload(
                        PayloadVariableConstants.ORDER_ITEMS,
                        Any.pack(orderItemsExternal))
                .putPayload(
                        SourceProcessConstants.SOURCE_PROCESS,
                        Any.pack(SourceProcess.newBuilder().setSourceProcess(sourceProcess).build()));

        pulsarInventoryProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(builder.build())
                .send();

        log.info("Reverting Product Reservation Command sent: [{}]", orderItemsExternal);
    }

    @Override
    @SneakyThrows
    public void notifyInterestedCustomer(String correlationId) {
        log.info("Notifying Interested Customers for Product");
        log.info("Key: [{}]", correlationId);


        log.info("Creating Command");
        InventoryCommand.Builder builder = InventoryCommand.newBuilder();
        builder.setCommand(InventoryCommandConstants.NOTIFY_CUSTOMER_FOR_INTERESTED_PRODUCTS);
        InventoryCommand inventoryCommand = builder.build();

        pulsarInventoryProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(inventoryCommand)
                .send();

        log.info("Notify Interested Customer about Product Command sent: [{}]", inventoryCommand);
    }
}
