package org.salgar.camunda.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.customer.response.CustomerResponse;

public interface CustomerInboundPort {
    void processCustomerResponse(Message<CustomerResponse> message);
}
