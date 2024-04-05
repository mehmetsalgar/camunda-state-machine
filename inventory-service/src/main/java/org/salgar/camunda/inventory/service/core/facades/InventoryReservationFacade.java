package org.salgar.camunda.inventory.service.core.facades;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import org.salgar.camunda.inventory.command.SourceProcess;
import org.salgar.camunda.inventory.model.protobuf.OrderItem;
import org.salgar.camunda.inventory.model.protobuf.OrderItems;
import org.salgar.camunda.inventory.response.InventoryResponse;
import org.salgar.camunda.inventory.response.ProductReservationRevertSuccessful;
import org.salgar.camunda.inventory.service.adapter.InventoryOutboundAdapter;
import org.salgar.camunda.inventory.service.core.model.OrderReservation;
import org.salgar.camunda.inventory.service.core.model.Product;
import org.salgar.camunda.inventory.service.core.model.ReservedProduct;
import org.salgar.camunda.inventory.service.core.persistence.InventoryMemory;
import org.salgar.camunda.inventory.service.port.InventoryOutboundPort;
import org.salgar.camunda.inventory.util.PayloadVariableConstants;
import org.salgar.camunda.inventory.util.SourceProcessConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.salgar.camunda.inventory.util.ResponseConstants.PRODUCT_RESERVATION_CANCELED;

@Component
@RequiredArgsConstructor
public class InventoryReservationFacade {
    private final InventoryMemory inventoryMemory;
    private final InventoryOutboundPort inventoryOutboundPort;

    public void reserveProduct(String orderId, OrderItems orderItems) {
        OrderReservation orderReservation = new OrderReservation();
        orderReservation.setReservationId(UUID.randomUUID().toString());
        List<ReservedProduct> reservedProducts = new ArrayList<>();
        for (OrderItem orderItem : orderItems.getOrderItemList()) {
            ReservedProduct reservedProduct = new ReservedProduct();
            Product product = new Product();
            product.setProductId(orderItem.getProduct().getProductId());
            product.setDescription(orderItem.getProduct().getDescription());
            reservedProduct.setProduct(product);
            reservedProduct.setQuantity(orderItem.getQuantity());
            reservedProducts.add(reservedProduct);
        }

        orderReservation.setReservedProducts(reservedProducts);

        inventoryMemory.reserveProduct(orderId, orderReservation);
    }

    public void revertProductReservation(String orderId, String sourceProcess) {
        inventoryMemory.revertReservation(orderId);

        InventoryResponse.Builder builder = InventoryResponse.newBuilder();
        builder.setResponse(PRODUCT_RESERVATION_CANCELED);
        builder.putPayload(
                    PayloadVariableConstants.PRODUCT_RESERVATION_REVERT_SUCCESSFUL,
                    Any.pack(
                        ProductReservationRevertSuccessful
                                .newBuilder()
                                .setProductReservationRevertSuccessful(true)
                                .build()
                    )
                )
                .putPayload(
                        SourceProcessConstants.SOURCE_PROCESS,
                        Any.pack(SourceProcess.newBuilder().setSourceProcess(sourceProcess).build())
                );

        inventoryOutboundPort.deliverInventoryResponse(orderId, builder.build());
    }
}
