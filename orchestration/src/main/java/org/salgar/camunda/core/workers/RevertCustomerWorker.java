package org.salgar.camunda.core.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.order.util.PayloadVariableConstants;
import org.salgar.camunda.port.CustomerOutboundPort;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.salgar.camunda.core.util.SubProcessConstants.SOURCE_PROCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class RevertCustomerWorker {
    private final CustomerOutboundPort customerOutboundPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @JobWorker(type = "revertCustomer")
    @SneakyThrows
    public void revertCustomer(final ActivatedJob job) {
        log.info("Revering Customer changes");
        Map<String, Object> existingVariables = job.getVariablesAsMap();

        String orderId = (String) existingVariables.get(PayloadVariableConstants.ORDER_ID_VARIABLE);
        String sourceProcess = (String) existingVariables.get(SOURCE_PROCESS);
        //String json = (String) existingVariables.get(
        //        org.salgar.camunda.customer.util.PayloadVariableConstants.CREATE_CUSTOMER_VARIABLE);
        //Customer customer = objectMapper.readValue(json, Customer.class);

        customerOutboundPort.revertCustomerChanges(orderId, sourceProcess);
    }
}
