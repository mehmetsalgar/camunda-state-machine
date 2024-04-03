package org.salgar.camunda.core.mapping;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;

@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
public interface OrchestrationInventory2Inventory {
    default String mapString(String in) {
        if ((null == in) || in.isEmpty()) {
            return null;
        }
        return in;
    }

    default Double mapDouble(Double in) {
        return in;
    }

    OrderItem map(org.salgar.camunda.inventory.model.protobuf.OrderItem orderItem);
    org.salgar.camunda.inventory.model.protobuf.OrderItem map(OrderItem orderItem);

    Product map(org.salgar.camunda.inventory.model.protobuf.Product product);
    org.salgar.camunda.inventory.model.protobuf.Product map(Product product);
}
