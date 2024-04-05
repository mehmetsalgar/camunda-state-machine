package org.salgar.camunda.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.core.model.Address;
import org.salgar.camunda.core.model.BankInformation;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;
import org.salgar.camunda.port.RestPort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter implements RestPort {
    private final ZeebeClient zeebeClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/orchestration/trigger")
    public void trigger() {
        triggerWorkflow();
    }

    @Override
    @SneakyThrows
    public void triggerWorkflow() {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Address address = new Address();
        address.setStreet("heaven street");
        address.setCity("heaven city");
        address.setHouseNo("17a");
        address.setPlz("98765");
        address.setCountry("heaven");

        Customer customer = new Customer();
        //customer.setCustomerId(UUID.randomUUID().toString());
        customer.setName("Doe");
        customer.setFirstName("John");
        customer.setAddress(address);

        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setDescription("Best product");
        product.setPrice(635.6);
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setProduct(product);
        orderItems.add(orderItem);

        BankInformation bankInformation = new BankInformation();
        bankInformation.setIban("PT1234987612349876");
        bankInformation.setBlz("987654321");

        Order order = new Order();
        //order.setOrderId(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setOrderItems(orderItems);
        order.setBankInformation(bankInformation);

        String json = objectMapper.writeValueAsString(order);
        variables.put("order", json);

        log.info("Triggering process");
        ProcessInstanceEvent event = zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId("create-order-optimised")
                .latestVersion()
                .variables(variables)
                .send().
                join();

        log.info(
                "Started instance for processDefinitionKey={}, bpmnProcessId={}, version={} with processInstanceKey={}",
                event.getProcessDefinitionKey(),
                event.getBpmnProcessId(),
                event.getVersion(),
                event.getProcessInstanceKey());

    }
}
