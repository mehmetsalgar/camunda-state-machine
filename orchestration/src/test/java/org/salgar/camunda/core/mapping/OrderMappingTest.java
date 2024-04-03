package org.salgar.camunda.core.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.salgar.camunda.core.model.Address;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderMappingTest {
    @Test
    public void mapOrder2External() {
        OrchestrationOrder2Order orchestrationOrder2Order = new OrchestrationOrder2OrderImpl();
        Address address = new Address();
        address.setStreet("heaven street");
        address.setCity("heaven city");
        address.setHouseNo("17a");
        address.setPlz("98765");
        address.setCountry("heaven");

        Customer customer = new Customer();
        customer.setCustomerId(UUID.randomUUID().toString());
        customer.setName("Doe");
        customer.setFirstName("John");
        customer.setAddress(address);

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setDescription("Best product");
        product.setPrice(635.6);
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItems.add(orderItem);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setOrderItems(orderItems);

        org.salgar.camunda.order.model.protobuf.Order orderExternal =
                orchestrationOrder2Order.map(order);

        Assertions.assertNotNull(orderExternal);
    }
}
