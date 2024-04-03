package org.salgar.camunda.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.core.util.ZeebeMessageConstants;
import org.salgar.camunda.port.ProductOptionsInboundPort;
import org.salgar.camunda.product.options.model.protobuf.ProductOptions;
import org.salgar.camunda.product.options.model.protobuf.ProductOptionsCalculated;
import org.salgar.camunda.product.options.response.ProductOptionsResponse;
import org.salgar.camunda.product.options.util.PayloadVariableConstants;
import org.salgar.camunda.product.options.util.ProductOptionsResponseConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOptionsInboundAdapter implements ProductOptionsInboundPort {
    private final ZeebeClient zeebeClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public void processProductOptionsResponse(Message<ProductOptionsResponse> message) {
        if(ProductOptionsResponseConstants.AVAILABLE_PRODUCT_OPTIONS.equals(message.getValue().getResponse())) {
            ProductOptions productOptions = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.AVAILABLE_PRODUCT_OPTIONS).unpack(ProductOptions.class);

            ProductOptionsCalculated productOptionsCalculated = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.PRODUCT_OPTIONS_CALCULATED).unpack(ProductOptionsCalculated.class);

            Map<String, Object> variables = new HashMap<>();
            List<String> productList = new ArrayList<>();
            for (int x = 0, n = productOptions.getOptionCount(); x < n; x++) {
                productList.add(productOptions.getOption(x));
            }

            String json = objectMapper.writeValueAsString(productList);

            variables.put(
                    PayloadVariableConstants.AVAILABLE_PRODUCT_OPTIONS,
                    json);
            variables.put(
                    PayloadVariableConstants.PRODUCT_OPTIONS_CALCULATED,
                    productOptionsCalculated.getProductOptionsCalculated());

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.AVAILABLE_PRODUCT_OPTIONS_MESSAGE,
                    variables
            );

        } else {
            log.warn("Unknown Response: [{}]", message.getValue());
        }
    }

    private void processZeebeMessage(
            String correlationId,
            String zeebeMessage,
            Map<String, Object> variables) {
        zeebeClient
                .newPublishMessageCommand()
                .messageName(zeebeMessage)
                .correlationKey(correlationId)
                .variables(variables)
                .send()
                .join();
    }
}
