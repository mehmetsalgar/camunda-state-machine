package org.salgar.camunda.core.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.order.util.PayloadVariableConstants;
import org.salgar.camunda.port.OrderOutboundPort;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompleteOrderWorker {
    private final OrderOutboundPort orderOutboundPort;

    @JobWorker(type = "completeOrder")
    public void completeOrder(final ActivatedJob job) {
        log.info("Completing Order");

        Map<String, Object> existingVariables = job.getVariablesAsMap();
        String orderId = (String) existingVariables.get(PayloadVariableConstants.ORDER_ID_VARIABLE);

        orderOutboundPort.completeOrder(orderId);
    }
}
