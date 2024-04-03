package org.salgar.camunda.core.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.order.util.PayloadVariableConstants;
import org.salgar.camunda.port.CustomerOutboundPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCustomerWorker {
    private final CustomerOutboundPort customerOutboundPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @JobWorker(type = "createCustomer")
    @SneakyThrows
    public void createCustomer(final ActivatedJob job) {
        log.info("Creating Customer");

        Map<String, Object> existingVariables = job.getVariablesAsMap();

        String json = (String) existingVariables.get("order");
        Order order = objectMapper.readValue(json, Order.class);

        String orderId = (String) existingVariables.get(PayloadVariableConstants.ORDER_ID_VARIABLE);

        Customer customer = order.getCustomer();

        customerOutboundPort.createCustomer(orderId, customer);
    }
}
