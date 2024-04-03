package org.salgar.camunda.core.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.port.OrderOutboundPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOrderWorker {
    private final OrderOutboundPort orderPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @JobWorker(type = "createOrder")
    @SneakyThrows
    public Map<String, Object> createOrder(final ActivatedJob job) {
        log.info("Creating Order");
        String correlationId = UUID.randomUUID().toString();

        Map<String, Object> existingVariables = job.getVariablesAsMap();

        String json = (String) existingVariables.get("order");
        Order order = objectMapper.readValue(json, Order.class);

        Map<String, Object> variables = new HashMap<>();
        variables.put("correlationId", correlationId);

        orderPort.createOrder(correlationId, order);

        return variables;
    }
}
