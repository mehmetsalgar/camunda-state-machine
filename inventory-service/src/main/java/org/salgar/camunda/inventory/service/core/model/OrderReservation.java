package org.salgar.camunda.inventory.service.core.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderReservation {
    private String reservationId;
    private List<ReservedProduct> reservedProducts;
}
