package org.salgar.camunda.inventory.service.core.persistence;

import org.salgar.camunda.inventory.service.core.model.OrderReservation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InventoryMemory {
    private final Map<String, OrderReservation> memory = new HashMap<>();

    public void reserveProduct(String orderId, OrderReservation orderReservation) {
        memory.put(orderId, orderReservation);
    }

    public void revertReservation(String orderId) {
        memory.remove(orderId);
    }
}
