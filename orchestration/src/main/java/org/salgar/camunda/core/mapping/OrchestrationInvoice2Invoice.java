package org.salgar.camunda.core.mapping;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.salgar.camunda.core.model.BankInformation;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.core.model.Product;

@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
public interface OrchestrationInvoice2Invoice {
    default String mapString(String in) {
        if ((null == in) || in.isEmpty()) {
            return null;
        }
        return in;
    }

    default Double mapDouble(Double in) {
        return in;
    }

    Customer map(org.salgar.camunda.invoice.model.protobuf.Customer customerProto);
    org.salgar.camunda.invoice.model.protobuf.Customer map(Customer customer);

    BankInformation map(org.salgar.camunda.invoice.model.protobuf.BankInformation bankInformationProto);
    org.salgar.camunda.invoice.model.protobuf.BankInformation map(BankInformation bankInformation);

    OrderItem map(org.salgar.camunda.invoice.model.protobuf.OrderItem OrderItemProto);
    org.salgar.camunda.invoice.model.protobuf.OrderItem map(OrderItem OrderItem);

    Product map(org.salgar.camunda.invoice.model.protobuf.Product product);
    org.salgar.camunda.invoice.model.protobuf.Product map(Product product);
}
