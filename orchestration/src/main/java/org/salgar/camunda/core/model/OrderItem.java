package org.salgar.camunda.core.model;

import lombok.Data;

@Data
public class OrderItem {
    private long quantity;
    private Product product;
}
