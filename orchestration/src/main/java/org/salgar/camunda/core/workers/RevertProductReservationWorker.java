package org.salgar.camunda.core.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.order.util.PayloadVariableConstants;
import org.salgar.camunda.port.InventoryOutboundPort;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.salgar.camunda.core.util.SubProcessConstants.SOURCE_PROCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class RevertProductReservationWorker {
    private final InventoryOutboundPort inventoryOutboundPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @JobWorker(type = "revertProductReservation")
    @SneakyThrows
    public void revertProductReservation(final ActivatedJob job) {
        log.info("Revering Product Reservation");

        Map<String, Object> existingVariables = job.getVariablesAsMap();
        String orderId = (String) existingVariables.get(PayloadVariableConstants.ORDER_ID_VARIABLE);
        String sourceProcess = (String) existingVariables.get(SOURCE_PROCESS);

        String json = (String) existingVariables.get("order");
        Order order = objectMapper.readValue(json, Order.class);

        inventoryOutboundPort.revertProductReservation(orderId, order.getOrderItems(),  sourceProcess);
    }
}
