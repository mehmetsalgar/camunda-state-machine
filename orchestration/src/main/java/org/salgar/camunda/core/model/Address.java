package org.salgar.camunda.core.model;

import lombok.Data;

@Data
public class Address {
    private String street;
    private String houseNo;
    private String city;
    private String plz;
    private String country;
}
