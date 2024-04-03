package org.salgar.camunda.customer.service.port;

import org.salgar.camunda.customer.response.CustomerResponse;

public interface CustomerOutboundPort {
     void deliverCustomerResponse(String correlationId, CustomerResponse customerResponse);
}
