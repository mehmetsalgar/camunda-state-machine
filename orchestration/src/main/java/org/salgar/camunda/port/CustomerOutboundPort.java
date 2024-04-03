package org.salgar.camunda.port;

import org.salgar.camunda.core.model.Customer;

public interface CustomerOutboundPort {
    void createCustomer(String correlationId, Customer customer);
    void revertCustomerChanges(String correlationId);
}
