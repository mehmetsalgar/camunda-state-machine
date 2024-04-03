package org.salgar.camunda.order.service.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.order.model.protobuf.Order;
import org.salgar.camunda.order.response.OrderResponse;
import org.salgar.camunda.order.service.port.OrderOutboundPort;
import org.salgar.camunda.order.service.port.RestPort;
import org.salgar.camunda.order.util.ResponseConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;

import static org.salgar.camunda.order.util.PayloadVariableConstants.ORDER_ID_VARIABLE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter implements RestPort {
    private final OrderOutboundPort orderOutboundPort;

    @PostMapping("/order/response")
    public void prepare(ServerWebExchange exchange) {
        log.info("Preparing response");
        String correlationId = exchange.getRequest().getQueryParams().getFirst("correlationId");
        if (correlationId==null) {correlationId = "";}

        String response = exchange.getRequest().getQueryParams().getFirst("response");
        if (response==null) {correlationId = "";}

        prepareOrderServiceResponse(correlationId, response);
    }

    @Override
    public void prepareOrderServiceResponse(String correlationId, String response) {
        String orderResponse = null;
        if("pending".equals(response)) {
            orderResponse = ResponseConstants.ORDER_PENDING;
            prepareOrderPending(correlationId, orderResponse);
        } else if("failed".equals(response)) {
            orderResponse = ResponseConstants.ORDER_FAILED;
            prepareOrderFailed(correlationId, orderResponse);
        } else if("complete".equals(response)) {
            orderResponse = ResponseConstants.ORDER_COMPLETE;
            prepareOrderComplete(correlationId, orderResponse);
        } else {
            log.info("Unknown response: [{}]", response);
        }
    }

    private void prepareOrderPending(String correlationId, String orderResponse) {
        String orderId = UUID.randomUUID().toString();
        Order.Builder orderBuilder = Order.newBuilder();
        orderBuilder.setOrderId(orderId);
        OrderResponse.Builder builder = OrderResponse.newBuilder();
        builder
                .setResponse(orderResponse)
                .putPayload(
                        ORDER_ID_VARIABLE,
                        Any.pack(orderBuilder.build())
                );

        orderOutboundPort.deliverOrderResponse(correlationId, builder.build());
    }

    private void prepareOrderFailed(String correlationId, String orderResponse) {
        OrderResponse.Builder builder = OrderResponse.newBuilder();
        builder.setResponse(orderResponse);

        orderOutboundPort.deliverOrderResponse(correlationId, builder.build());
    }

    private void prepareOrderComplete(String correlationId, String orderResponse) {
        OrderResponse.Builder builder = OrderResponse.newBuilder();
        builder.setResponse(orderResponse);

        orderOutboundPort.deliverOrderResponse(correlationId, builder.build());
    }
}
