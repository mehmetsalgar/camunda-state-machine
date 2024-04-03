package org.salgar.camunda.port;

import org.salgar.camunda.core.model.Order;

public interface OrderOutboundPort {
    void createOrder(String correlationId, Order order);
    void completeOrder(String correlationId);
}
