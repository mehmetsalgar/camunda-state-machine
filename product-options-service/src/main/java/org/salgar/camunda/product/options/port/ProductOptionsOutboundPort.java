package org.salgar.camunda.product.options.port;

import org.salgar.camunda.product.options.response.ProductOptionsResponse;

public interface ProductOptionsOutboundPort {
    void deliverProductOptionsResponse(String correlationId, ProductOptionsResponse productOptionsResponse);
}
