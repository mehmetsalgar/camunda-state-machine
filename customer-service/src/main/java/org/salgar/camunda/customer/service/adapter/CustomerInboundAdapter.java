package org.salgar.camunda.customer.service.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.customer.command.CustomerCommand;
import org.salgar.camunda.customer.command.SourceProcess;
import org.salgar.camunda.customer.model.protobuf.Customer;
import org.salgar.camunda.customer.service.core.memory.CustomerMemory;
import org.salgar.camunda.customer.service.port.CustomerInboundPort;
import org.salgar.camunda.customer.util.PayloadVariableConstants;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.salgar.camunda.customer.util.CommandConstants.CREATE_CUSTOMER;
import static org.salgar.camunda.customer.util.CommandConstants.REVERT_CUSTOMER;
import static org.salgar.camunda.customer.util.SourceProcessConstants.SOURCE_PROCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerInboundAdapter implements CustomerInboundPort {
    private final CustomerMemory customerMemory;

    @Override
    @PulsarListener(
            subscriptionName = "customer-inbound-subscription",
            topics = "${spring.pulsar.consumer.customer.topics}"
    )

    public void processCustomerCommand(Message<CustomerCommand> command) {
        try {
            processCustomerCommandInternal(command);
        } catch(Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public void processCustomerCommandInternal(Message<CustomerCommand> command) throws InvalidProtocolBufferException {
        log.info(
                "We received the command: [{}] with key:[{}] payload: [{}]",
                command.getValue().getCommand(),
                command.getKey(),
                command.getValue().getPayloadMap());

        if(CREATE_CUSTOMER.equals(command.getValue().getCommand())) {
            log.info("Creating Customer");

            Customer customer = command.
                    getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.CREATE_CUSTOMER_VARIABLE)
                    .unpack(Customer.class);

            String orderID = command.getKey();

            Customer customerCreated = Customer
                    .newBuilder(customer)
                    .setCustomerId(UUID.randomUUID().toString())
                    .build();
            customerMemory.persistCustomer(orderID, customerCreated);
        } else if(REVERT_CUSTOMER.equals(command.getValue().getCommand())) {
            SourceProcess sourceProcess = command
                    .getValue()
                    .getPayloadMap()
                    .get(SOURCE_PROCESS)
                    .unpack(SourceProcess.class);

            log.info("Reverting Customer for sourceProcess:[{}]", sourceProcess);
            //String orderId = command.getKey();
            //customerFacade.revertCustomer(orderId);
        } else {
            log.info("Unknown command: [{}]", command.getValue().getCommand());
        }
    }
}
