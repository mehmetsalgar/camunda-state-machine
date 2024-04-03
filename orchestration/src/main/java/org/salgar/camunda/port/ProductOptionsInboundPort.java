package org.salgar.camunda.port;

import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.product.options.response.ProductOptionsResponse;

public interface ProductOptionsInboundPort {
    void processProductOptionsResponse(Message<ProductOptionsResponse> message);
}
