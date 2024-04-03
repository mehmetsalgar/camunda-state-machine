package org.salgar.camunda.inventory.service.core.model;

import lombok.Data;

@Data
public class ReservedProduct {
    private Product product;
    private Long quantity;
}
