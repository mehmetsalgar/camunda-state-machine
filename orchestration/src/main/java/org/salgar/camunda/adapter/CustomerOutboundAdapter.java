package org.salgar.camunda.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.salgar.camunda.core.mapping.OrchestrationCustomer2Customer;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.customer.command.CustomerCommand;
import org.salgar.camunda.customer.util.CommandConstants;
import org.salgar.camunda.customer.util.PayloadVariableConstants;
import org.salgar.camunda.port.CustomerOutboundPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerOutboundAdapter implements CustomerOutboundPort {
    private final Producer<org.salgar.camunda.customer.command.CustomerCommand> pulsarCustomerProducer;
    private final OrchestrationCustomer2Customer orchestrationCustomer2Customer;
    @Override
    @SneakyThrows
    public void createCustomer(String correlationId, Customer customer) {
        log.info("Creating Customer: [{}]", customer);
        log.info("Key: [{}]", correlationId);

        log.info("Mapping");
        org.salgar.camunda.customer.model.protobuf.Customer customerExternal =
                orchestrationCustomer2Customer.map(customer);

        log.info("Creating Command");
        CustomerCommand.Builder builder = CustomerCommand.newBuilder();
        builder.setCommand(CommandConstants.CREATE_CUSTOMER);
        builder.putPayload(
                PayloadVariableConstants.CREATE_CUSTOMER_VARIABLE,
                Any.pack(customerExternal));


        pulsarCustomerProducer
            .newMessage()
            .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
            .key(correlationId)
            .value(builder.build())
            .send();

        log.info("Create Command sent: [{}]", customerExternal);
    }

    @Override
    @SneakyThrows
    public void revertCustomerChanges(String correlationId) {
        log.info("Reverting Customer");
        log.info("Key: [{}]", correlationId);

        //log.info("Mapping");
        //org.salgar.camunda.customer.model.protobuf.Customer customerExternal =
        //        orchestrationCustomer2Customer.map(customer);

        log.info("Creating Command");
        CustomerCommand.Builder builder = CustomerCommand.newBuilder();
        builder.setCommand(CommandConstants.REVERT_CUSTOMER);

        CustomerCommand customerCommand = builder.build();

        //builder.putPayload(
        //        PayloadVariableConstants.CREATED_CUSTOMER_VARIABLE,
        //        Any.pack(customerExternal));

        pulsarCustomerProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(builder.build())
                .send();

        log.info("Revert Customer Command sent: [{}]", customerCommand);
    }
}
