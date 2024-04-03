package org.salgar.camunda.customer.service.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.customer.model.protobuf.Customer;
import org.salgar.camunda.customer.response.CustomerCreated;
import org.salgar.camunda.customer.response.CustomerResponse;
import org.salgar.camunda.customer.response.CustomerRevertSuccessful;
import org.salgar.camunda.customer.service.core.memory.CustomerMemory;
import org.salgar.camunda.customer.service.facades.CustomerFacade;
import org.salgar.camunda.customer.service.port.CustomerOutboundPort;
import org.salgar.camunda.customer.service.port.RestPort;
import org.salgar.camunda.customer.util.PayloadVariableConstants;
import org.salgar.camunda.customer.util.ResponseConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import static org.salgar.camunda.customer.util.PayloadVariableConstants.CUSTOMER_REVERTED_SUCCESSFUL;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter implements RestPort {
    private final CustomerOutboundPort customerOutboundPort;
    private final CustomerMemory customerMemory;
    private final CustomerFacade customerFacade;

    @PostMapping("/customer/response")
    public void prepare(ServerWebExchange exchange) {
        log.info("Preparing response");
        String correlationId = exchange.getRequest().getQueryParams().getFirst("correlationId");
        if (correlationId==null) {correlationId = "";}

        String response = exchange.getRequest().getQueryParams().getFirst("response");
        if (response==null) {correlationId = "";}

        processCustomerResponse(correlationId, response);
    }

    @Override
    public void processCustomerResponse(String correlationId, String response) {
        String customerResponse;
        if("creating".equals(response)) {
            customerResponse = ResponseConstants.CUSTOMER_CREATED;
            prepareCustomerCreated(correlationId, customerResponse);
        } else if("fail".equals(response)) {
            customerResponse = ResponseConstants.CUSTOMER_FAILED;
            prepareCustomerFailed(correlationId, customerResponse);
        } else if("revert".equals(response)) {
            prepareCustomerReverted(correlationId);
        } else if("revert_fail".equals(response)) {
            prepareCustomerRevertFailed(correlationId);
        }else {
            log.info("Unknown response: [{}]", response);
        }
    }

    private void prepareCustomerCreated(String correlationId, String customerResponse) {
        Customer customer = customerMemory.retrieveCustomerByOrderId(correlationId);

        CustomerResponse.Builder builder = CustomerResponse.newBuilder();
        builder
                .setResponse(customerResponse)
                .putPayload(
                        PayloadVariableConstants.CUSTOMER_CREATED,
                        Any.pack(CustomerCreated.newBuilder().setCustomerCreated(true).build()))
                .putPayload(
                        PayloadVariableConstants.CREATE_CUSTOMER_VARIABLE,
                        Any.pack(customer));

        customerOutboundPort.deliverCustomerResponse(correlationId, builder.build());
    }

    private void prepareCustomerFailed(String correlationId, String customerResponse) {
        CustomerResponse.Builder builder = CustomerResponse.newBuilder();
        builder
                .setResponse(customerResponse)
                .putPayload(
                        PayloadVariableConstants.CUSTOMER_CREATED,
                        Any.pack(CustomerCreated.newBuilder().setCustomerCreated(false).build()));

        customerOutboundPort.deliverCustomerResponse(correlationId, builder.build());
    }

    private void prepareCustomerReverted(String correlationId) {
        customerFacade.revertCustomer(correlationId);
    }

    private void prepareCustomerRevertFailed(String correlationId) {
        CustomerResponse.Builder builder = CustomerResponse.newBuilder();
        builder
                .setResponse(ResponseConstants.CUSTOMER_REVERTED)
                .putPayload(
                        PayloadVariableConstants.CUSTOMER_REVERTED_SUCCESSFUL,
                        Any.pack(CustomerRevertSuccessful.newBuilder().setCustomerRevertSucessful(false).build()));


        customerOutboundPort.deliverCustomerResponse(correlationId, builder.build());
    }
}
