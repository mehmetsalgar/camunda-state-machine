package org.salgar.camunda.product.options.port;

public interface RestPort {
    void prepareProductOptionsServiceResponse(String correlationId, String response);
}
