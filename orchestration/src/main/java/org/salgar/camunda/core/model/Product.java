package org.salgar.camunda.core.model;

import lombok.Data;

@Data
public class Product {
    private String productId;
    private String description;
    private Double price;
}
