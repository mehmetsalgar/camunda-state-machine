package org.salgar.camunda.port;

import org.salgar.camunda.core.model.OrderItem;

import java.util.List;

public interface InventoryOutboundPort {
    void reserveProductInInventory(String correlationId, List<OrderItem> orderItems);
    void revertProductReservation(String correlationId, List<OrderItem> orderItems);
    void notifyInterestedCustomer(String correlationId);
}
