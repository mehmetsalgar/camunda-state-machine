package org.salgar.camunda.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> orderItems;
    private BankInformation bankInformation;
}
