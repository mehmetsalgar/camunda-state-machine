package org.salgar.camunda.customer.service.facades;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import org.salgar.camunda.customer.model.protobuf.Customer;
import org.salgar.camunda.customer.response.CustomerResponse;
import org.salgar.camunda.customer.response.CustomerRevertSuccessful;
import org.salgar.camunda.customer.service.core.memory.CustomerMemory;
import org.salgar.camunda.customer.service.port.CustomerOutboundPort;
import org.springframework.stereotype.Component;

import static org.salgar.camunda.customer.util.PayloadVariableConstants.CUSTOMER_REVERTED_SUCCESSFUL;
import static org.salgar.camunda.customer.util.ResponseConstants.CUSTOMER_REVERTED;

@Component
@RequiredArgsConstructor
public class CustomerFacade {
    private final CustomerMemory customerMemory;
    private final CustomerOutboundPort customerOutboundPort;

    public void revertCustomer(String orderId) {
        Customer customer = customerMemory.retrieveCustomerById(orderId);
        customerMemory.removeCustomer(orderId, customer);

        CustomerResponse.Builder builder = CustomerResponse.newBuilder();
        builder.setResponse(CUSTOMER_REVERTED);
        builder.putPayload(
                CUSTOMER_REVERTED_SUCCESSFUL,
                Any.pack(CustomerRevertSuccessful.newBuilder().setCustomerRevertSucessful(true).build())
        );

        customerOutboundPort.deliverCustomerResponse(orderId, builder.build());
    }
}
