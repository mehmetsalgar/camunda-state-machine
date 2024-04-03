package org.salgar.camunda.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.order.response.OrderResponse;

public interface OrderInboundPort {
    void processOrderResponse(Message<OrderResponse> message);
}
