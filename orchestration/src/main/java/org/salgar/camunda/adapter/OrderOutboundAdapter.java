package org.salgar.camunda.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.order.command.OrderCommand;
import org.salgar.camunda.order.util.CommandConstants;
import org.salgar.camunda.port.OrderOutboundPort;
import org.salgar.camunda.core.mapping.OrchestrationOrder2Order;
import org.salgar.camunda.order.util.PayloadVariableConstants;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboundAdapter implements OrderOutboundPort {
    private final Producer<org.salgar.camunda.order.command.OrderCommand> pulsarProducer;
    private final OrchestrationOrder2Order orchestrationOrder2Order;

    @Override
    @SneakyThrows
    public void createOrder(String correlationId, Order order) {
        log.info("Creating Order: [{}]", order);
        log.info("Key: [{}]", correlationId);

        log.info("Mapping");
        org.salgar.camunda.order.model.protobuf.Order orderExternal =
                orchestrationOrder2Order.map(order);

        log.info("Creating Command");
        OrderCommand.Builder builder = OrderCommand.newBuilder();
        builder.setCommand(CommandConstants.CREATE_ORDER);
        builder.putPayload(
                PayloadVariableConstants.CREATE_ORDER_VARIABLE,
                Any.pack(orderExternal)
        );

        pulsarProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(builder.build())
                .send();

        log.info("Create Order sent: [{}]", orderExternal);
    }

    @Override
    @SneakyThrows
    public void completeOrder(String correlationId) {
        log.info("Completing Order");
        log.info("Key: [{}]", correlationId);

        log.info("Creating Command");
        OrderCommand.Builder builder = OrderCommand.newBuilder();
        builder.setCommand(CommandConstants.COMPLETE_ORDER);

        OrderCommand orderCommand = builder.build();

        pulsarProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(orderCommand)
                .send();

        log.info("Complete Order sent: [{}]", orderCommand);
    }
}
