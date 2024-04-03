package org.salgar.camunda.adapter;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.core.adapter.AbstractInboundAdapter;
import org.salgar.camunda.core.util.ZeebeMessageConstants;
import org.salgar.camunda.order.model.protobuf.Order;
import org.salgar.camunda.order.response.OrderResponse;
import org.salgar.camunda.order.util.PayloadVariableConstants;
import org.salgar.camunda.order.util.ResponseConstants;
import org.salgar.camunda.port.OrderInboundPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OrderInboundAdapter extends AbstractInboundAdapter implements OrderInboundPort {
    public OrderInboundAdapter(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    @Override
    @SneakyThrows
    public void processOrderResponse(Message<OrderResponse> message) {
        if(ResponseConstants.ORDER_PENDING.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            Order order =  message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.ORDER_ID_VARIABLE).unpack(Order.class);
            variables.put(
                    PayloadVariableConstants.ORDER_ID_VARIABLE,
                    order.getOrderId());

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.ORDER_PENDING_MESSAGE,
                    variables);
        } else if(ResponseConstants.ORDER_FAILED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            Order order =  message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.ORDER_ID_VARIABLE).unpack(Order.class);
            variables.put(
                    PayloadVariableConstants.ORDER_ID_VARIABLE,
                    order.getOrderId());

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.ORDER_CREATION_FAILED_MESSAGE,
                    variables);
        } else if(ResponseConstants.ORDER_COMPLETE.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.ORDER_COMPLETE_MESSAGE,
                    variables);
        } else {
            log.info("Unknown command: [{}]", message.getValue().getResponse());
        }
    }
}
