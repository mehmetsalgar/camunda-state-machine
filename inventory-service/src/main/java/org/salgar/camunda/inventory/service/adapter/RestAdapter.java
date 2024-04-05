package org.salgar.camunda.inventory.service.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.inventory.response.InventoryResponse;
import org.salgar.camunda.inventory.response.ProductReserved;
import org.salgar.camunda.inventory.service.core.facades.InventoryReservationFacade;
import org.salgar.camunda.inventory.service.port.RestPort;
import org.salgar.camunda.inventory.util.PayloadVariableConstants;
import org.salgar.camunda.inventory.util.ResponseConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter implements RestPort {
    private final InventoryOutboundAdapter inventoryOutboundAdapter;
    private final InventoryReservationFacade inventoryReservationFacade;

    @PostMapping("/inventory/response")
    public void prepare(ServerWebExchange exchange) {
        log.info("Preparing response");
        String correlationId = exchange.getRequest().getQueryParams().getFirst("correlationId");
        if (correlationId==null) {correlationId = "";}

        String response = exchange.getRequest().getQueryParams().getFirst("response");
        if (response==null) {response = "";}

        String sourceProcess = exchange.getRequest().getQueryParams().getFirst("sourceProcess");
        if (sourceProcess==null) {sourceProcess = "";}

        processInventoryResponse(correlationId, response, sourceProcess);
    }

    @Override
    public void processInventoryResponse(String correlationId, String response, String sourceProcess) {
        String customerResponse;
        if("reserve".equals(response)) {
            customerResponse = ResponseConstants.PRODUCT_RESERVED;
            prepareInventoryCreated(correlationId, customerResponse);
        } else if("fail".equals(response)) {
            customerResponse = ResponseConstants.PRODUCT_RESERVED;
            prepareInventoryCreatedFailed(correlationId, customerResponse);
        } else if("revert".equals(response)) {
            prepareInventoryReservationCanceled(correlationId, sourceProcess);
        } else {
            log.info("Unknown response: [{}]", response);
        }
    }

    private void prepareInventoryCreated(String correlationId, String inventoryResponse) {
        InventoryResponse.Builder builder = InventoryResponse.newBuilder();
        builder.setResponse(inventoryResponse);
        builder.putPayload(
                PayloadVariableConstants.PRODUCT_RESERVED,
                Any.pack(ProductReserved.newBuilder().setProductReserved(true).build())
        );

        inventoryOutboundAdapter.deliverInventoryResponse(correlationId, builder.build());
    }

    private void prepareInventoryCreatedFailed(String correlationId, String inventoryResponse) {
        InventoryResponse.Builder builder = InventoryResponse.newBuilder();
        builder.setResponse(inventoryResponse);
        builder.putPayload(
                PayloadVariableConstants.PRODUCT_RESERVED,
                Any.pack(ProductReserved.newBuilder().setProductReserved(false).build())
        );

        inventoryOutboundAdapter.deliverInventoryResponse(correlationId, builder.build());
    }

    private void prepareInventoryReservationCanceled(String correlationId, String sourceProcess) {
        inventoryReservationFacade.revertProductReservation(correlationId, sourceProcess);
    }
}
