package org.salgar.camunda.core.mapping;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.salgar.camunda.core.model.Address;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.Order;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;

@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
        //unmappedSourcePolicy = ReportingPolicy.ERROR,
        //unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrchestrationOrder2Order {
    default String mapString(String in) {
        if ((null == in) || in.isEmpty()) {
            return null;
        }
        return in;
    }

    default Double mapDouble(Double in) {
        return in;
    }

    Order map(org.salgar.camunda.order.model.protobuf.Order orderProto);
    org.salgar.camunda.order.model.protobuf.Order map(Order order);

    Customer map(org.salgar.camunda.order.model.protobuf.Customer customerProto);
    org.salgar.camunda.order.model.protobuf.Customer map(Customer customer);

    Address map(org.salgar.camunda.order.model.protobuf.Address addressProto);
    org.salgar.camunda.order.model.protobuf.Address map(Address address);

    OrderItem map(org.salgar.camunda.order.model.protobuf.OrderItem OrderItem);
    org.salgar.camunda.order.model.protobuf.OrderItem map(OrderItem OrderItem);

    Product map(org.salgar.camunda.order.model.protobuf.Product productProto);
    org.salgar.camunda.order.model.protobuf.Product map(Product product);
}
