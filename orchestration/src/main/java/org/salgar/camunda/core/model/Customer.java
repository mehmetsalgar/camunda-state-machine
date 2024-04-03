package org.salgar.camunda.core.model;

import lombok.Data;

@Data
public class Customer {
    private String customerId;
    private String name;
    private String firstName;
    private Address address;
}
