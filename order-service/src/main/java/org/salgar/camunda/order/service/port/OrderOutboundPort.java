package org.salgar.camunda.order.service.port;

import org.salgar.camunda.order.response.OrderResponse;

public interface OrderOutboundPort {
    void deliverOrderResponse(String correlationId, OrderResponse orderResponse);
}
