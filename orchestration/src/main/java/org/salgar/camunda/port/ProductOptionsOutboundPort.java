package org.salgar.camunda.port;


import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;

import java.util.List;

public interface ProductOptionsOutboundPort {
    void calculateOptions(String correlationId, List<OrderItem> orderItems);
}
