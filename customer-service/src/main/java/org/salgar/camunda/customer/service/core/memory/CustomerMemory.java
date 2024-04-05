package org.salgar.camunda.customer.service.core.memory;

import org.salgar.camunda.customer.model.protobuf.Customer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerMemory {
    private final Map<String, Customer> memoryByOrderId = new HashMap<>();
    private final Map<String, Customer> memory = new HashMap<>();

    public void persistCustomer(String orderId, Customer customer) {
        memory.put(customer.getCustomerId(), customer);
        memoryByOrderId.put(orderId, customer);
    }

    public Customer retrieveCustomerByOrderId(String orderId) {
        return memoryByOrderId.get(orderId);
    }

    public Customer retrieveCustomerById(String customerId) {
        return memory.get(customerId);
    }

    public void removeCustomer(String orderId) {
        Customer customer =memoryByOrderId.remove(orderId);
        memory.remove(customer.getCustomerId());
    }
}
