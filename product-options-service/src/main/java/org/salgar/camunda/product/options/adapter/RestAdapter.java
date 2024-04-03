package org.salgar.camunda.product.options.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.product.options.model.protobuf.ProductOptions;
import org.salgar.camunda.product.options.model.protobuf.ProductOptionsCalculated;
import org.salgar.camunda.product.options.port.ProductOptionsOutboundPort;
import org.salgar.camunda.product.options.port.RestPort;
import org.salgar.camunda.product.options.response.ProductOptionsResponse;
import org.salgar.camunda.product.options.util.PayloadVariableConstants;
import org.salgar.camunda.product.options.util.ProductOptionsResponseConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter implements RestPort {
    private final ProductOptionsOutboundPort productOptionsOutboundPort;

    @PostMapping("/product-options/response")
    public void prepare(ServerWebExchange exchange) {
        log.info("Preparing response");
        String correlationId = exchange.getRequest().getQueryParams().getFirst("correlationId");
        if (correlationId==null) {correlationId = "";}

        String response = exchange.getRequest().getQueryParams().getFirst("response");
        if (response==null) {correlationId = "";}

        prepareProductOptionsServiceResponse(correlationId, response);
    }

    @Override
    public void prepareProductOptionsServiceResponse(String correlationId, String response) {
        if("calculate_options".equals(response)) {
            prepareAvailableProductOptionsResponse(correlationId, ProductOptionsResponseConstants.AVAILABLE_PRODUCT_OPTIONS);
        } else {
            log.warn("Unknown response: [{}]", response);
        }
    }

    private void prepareAvailableProductOptionsResponse(String correlationId, String response) {
        ProductOptionsResponse.Builder builder = ProductOptionsResponse.newBuilder();
        builder.setResponse(response);
        builder.putPayload(
                PayloadVariableConstants.AVAILABLE_PRODUCT_OPTIONS,
                Any.pack(ProductOptions
                        .newBuilder()
                        .addOption("option1")
                        .addOption("option2")
                        .addOption("option3")
                        .build())
        );
        builder.putPayload(
                PayloadVariableConstants.PRODUCT_OPTIONS_CALCULATED,
                Any.pack(ProductOptionsCalculated.newBuilder().setProductOptionsCalculated(true).build())
        );

        productOptionsOutboundPort.deliverProductOptionsResponse(correlationId, builder.build());
    }
}
