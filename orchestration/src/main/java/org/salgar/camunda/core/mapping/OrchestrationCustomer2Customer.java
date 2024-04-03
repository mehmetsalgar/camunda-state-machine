package org.salgar.camunda.core.mapping;


import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.salgar.camunda.core.model.Address;
import org.salgar.camunda.core.model.Customer;

@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring"
)
public interface OrchestrationCustomer2Customer {
    default String mapString(String in) {
        if ((null == in) || in.isEmpty()) {
            return null;
        }
        return in;
    }

    default Double mapDouble(Double in) {
        return in;
    }

    Customer map(org.salgar.camunda.customer.model.protobuf.Customer customer);
    org.salgar.camunda.customer.model.protobuf.Customer map(Customer customer);

    Address map(org.salgar.camunda.customer.model.protobuf.Address address);
    org.salgar.camunda.customer.model.protobuf.Address map(Address address);
}
