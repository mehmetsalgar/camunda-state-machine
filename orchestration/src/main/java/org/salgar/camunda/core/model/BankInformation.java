package org.salgar.camunda.core.model;

import lombok.Data;

@Data
public class BankInformation {
    private String iban;
    private String blz;
}
