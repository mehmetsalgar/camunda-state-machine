package org.salgar.camunda.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.core.adapter.AbstractInboundAdapter;
import org.salgar.camunda.core.mapping.OrchestrationCustomer2Customer;
import org.salgar.camunda.core.util.ZeebeMessageConstants;
import org.salgar.camunda.customer.model.protobuf.Customer;
import org.salgar.camunda.customer.response.CustomerCreated;
import org.salgar.camunda.customer.response.CustomerResponse;

import org.salgar.camunda.customer.response.CustomerRevertSuccessful;
import org.salgar.camunda.customer.util.PayloadVariableConstants;
import org.salgar.camunda.customer.util.ResponseConstants;
import org.salgar.camunda.port.CustomerInboundPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomerInboundAdapter extends AbstractInboundAdapter implements CustomerInboundPort {
    private final OrchestrationCustomer2Customer orchestrationCustomer2Customer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomerInboundAdapter(
            ZeebeClient zeebeClient,
            OrchestrationCustomer2Customer orchestrationCustomer2Customer) {
        super(zeebeClient);
        this.orchestrationCustomer2Customer = orchestrationCustomer2Customer;
    }

    @Override
    @SneakyThrows
    public void processCustomerResponse(Message<CustomerResponse> message) {
        if(ResponseConstants.CUSTOMER_CREATED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            CustomerCreated customerCreated =  message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.CUSTOMER_CREATED).unpack(CustomerCreated.class);
            variables.put(
                    PayloadVariableConstants.CUSTOMER_CREATED,
                    customerCreated.getCustomerCreated());

            Customer customer = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.CREATE_CUSTOMER_VARIABLE)
                    .unpack(Customer.class);
            org.salgar.camunda.core.model.Customer customerInternal =
                    orchestrationCustomer2Customer.map(customer);

            String json = objectMapper.writeValueAsString(customerInternal);
            variables.put(
                    PayloadVariableConstants.CREATED_CUSTOMER_VARIABLE,
                    json);

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.CUSTOMER_CREATED_MESSAGE,
                    variables);
        } else if(ResponseConstants.CUSTOMER_FAILED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            CustomerCreated customerCreated =  message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.CUSTOMER_CREATED).unpack(CustomerCreated.class);
            variables.put(
                    PayloadVariableConstants.CUSTOMER_CREATED,
                    customerCreated.getCustomerCreated());
            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.CUSTOMER_CREATED_MESSAGE,
                    variables);
        } else if(ResponseConstants.CUSTOMER_REVERTED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();

            CustomerRevertSuccessful customerRevertSuccessful = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.CUSTOMER_REVERTED_SUCCESSFUL)
                    .unpack(CustomerRevertSuccessful.class);
            variables.put(
                    PayloadVariableConstants.CUSTOMER_REVERTED_SUCCESSFUL,
                    customerRevertSuccessful.getCustomerRevertSucessful()
            );

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.CUSTOMER_REVERT_RESULT_MESSAGE,
                    variables);
        }
        else {
            log.info("Unknown command: [{}]", message.getValue().getResponse());
        }
    }
}
