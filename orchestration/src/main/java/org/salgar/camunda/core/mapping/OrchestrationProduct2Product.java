package org.salgar.camunda.core.mapping;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;

@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
public interface OrchestrationProduct2Product {
    default String mapString(String in) {
        if ((null == in) || in.isEmpty()) {
            return null;
        }
        return in;
    }

    default Double mapDouble(Double in) {
        return in;
    }

    OrderItem map(org.salgar.camunda.product.options.model.protobuf.OrderItem orderItem);
    org.salgar.camunda.product.options.model.protobuf.OrderItem map(OrderItem orderItem);

    Product map(org.salgar.camunda.product.options.model.protobuf.Product product);
    org.salgar.camunda.product.options.model.protobuf.Product map(Product product);
}
