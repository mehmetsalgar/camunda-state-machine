package org.salgar.camunda.adapter;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.core.adapter.AbstractInboundAdapter;
import org.salgar.camunda.core.util.ZeebeMessageConstants;
import org.salgar.camunda.inventory.response.InventoryResponse;
import org.salgar.camunda.inventory.response.ProductReservationRevertSuccessful;
import org.salgar.camunda.inventory.response.ProductReserved;
import org.salgar.camunda.inventory.util.PayloadVariableConstants;
import org.salgar.camunda.inventory.util.ResponseConstants;
import org.salgar.camunda.port.InventoryInboundPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InventoryInboundAdapter extends AbstractInboundAdapter implements InventoryInboundPort {
    public InventoryInboundAdapter(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    @Override
    @SneakyThrows
    public void processInventoryResponse(Message<InventoryResponse> message) {
        if(ResponseConstants.PRODUCT_RESERVED.equals(message.getValue().getResponse())) {
            ProductReserved productReserved = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.PRODUCT_RESERVED).unpack(ProductReserved.class);

            Map<String, Object> variables = new HashMap<>();
            variables.put(
                    PayloadVariableConstants.PRODUCT_RESERVED,
                    productReserved.getProductReserved()
            );
            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.PRODUCT_RESERVED_MESSAGE,
                    variables
            );
        } else if(ResponseConstants.PRODUCT_RESERVATION_CANCELED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            ProductReservationRevertSuccessful productReservationRevertSuccessful = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.PRODUCT_RESERVATION_REVERT_SUCCESSFUL)
                    .unpack(ProductReservationRevertSuccessful.class);
            variables.put(
                    PayloadVariableConstants.PRODUCT_RESERVATION_REVERT_SUCCESSFUL,
                    productReservationRevertSuccessful.getProductReservationRevertSuccessful());
            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.PRODUCT_RESERVATION_REVERTED_MESSAGE,
                    variables
            );
        } else {
            log.info("Unknown command: [{}]", message.getValue().getResponse());
        }
    }
}
