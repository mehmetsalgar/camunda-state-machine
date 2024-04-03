package org.salgar.camunda.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.salgar.camunda.core.mapping.OrchestrationProduct2Product;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.port.ProductOptionsOutboundPort;
import org.salgar.camunda.product.options.command.ProductOptionsCommand;
import org.salgar.camunda.product.options.model.protobuf.OrderItems;
import org.salgar.camunda.product.options.util.CommandConstants;
import org.salgar.camunda.product.options.util.PayloadVariableConstants;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOptionsOutboundAdapter implements ProductOptionsOutboundPort {
    private final Producer<ProductOptionsCommand> pulsarProductOptionsProducer;
    private final OrchestrationProduct2Product orchestrationProduct2Product;

    @Override
    @SneakyThrows
    public void calculateOptions(String correlationId, List<OrderItem> orderItems) {
        log.info("Calculate Product Options: [{}]", orderItems);
        log.info("Key: [{}]", correlationId);

        log.info("Mapping");
        OrderItems.Builder orderItemsBuilder = OrderItems.newBuilder();
        for (OrderItem orderItem : orderItems) {
            orderItemsBuilder.addProducts(orchestrationProduct2Product.map(orderItem));
        }
        OrderItems orderItemsExternal = orderItemsBuilder.build();

        log.info("Creating Command");
        ProductOptionsCommand.Builder builder = ProductOptionsCommand.newBuilder();
        builder.setCommand(CommandConstants.CALCULATE_PRODUCT_OPTIONS);
        builder.putPayload(
                PayloadVariableConstants.PRODUCTS_TO_CALCULATE,
                Any.pack(orderItemsExternal));


        pulsarProductOptionsProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(builder.build())
                .send();

        log.info("Create Command sent: [{}]", orderItemsExternal);
    }
}
