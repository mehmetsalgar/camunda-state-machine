package org.salgar.camunda.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.salgar.camunda.core.mapping.OrchestrationInvoice2Invoice;
import org.salgar.camunda.core.model.BankInformation;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.OrderItem;
import org.salgar.camunda.invoice.command.InvoiceCommand;
import org.salgar.camunda.invoice.model.protobuf.OrderItems;
import org.salgar.camunda.invoice.utils.InvoiceCommandConstants;
import org.salgar.camunda.invoice.utils.PayloadVariableConstants;
import org.salgar.camunda.port.InvoiceOutboundPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceOutboundAdapter implements InvoiceOutboundPort {
    private final Producer<InvoiceCommand> pulsarInvoiceProducer;
    private final OrchestrationInvoice2Invoice orchestrationInvoice2Invoice;

    @Override
    @SneakyThrows
    public void createInvoice(
            String correlationId,
            Customer customer,
            BankInformation bankInformation,
            List<OrderItem> orderItems) {
        log.info("Creating Invoice: [{}]", orderItems);
        log.info("Key: [{}]", correlationId);

        log.info("Mapping");
        org.salgar.camunda.invoice.model.protobuf.Customer customerExternal =
                orchestrationInvoice2Invoice.map(customer);

        org.salgar.camunda.invoice.model.protobuf.BankInformation bankInformationExternal =
                orchestrationInvoice2Invoice.map(bankInformation);

        OrderItems.Builder orderItemsBuilder = OrderItems.newBuilder();
        for (OrderItem orderItem : orderItems) {
            orderItemsBuilder.addOrderItem(orchestrationInvoice2Invoice.map(orderItem));
        }
        OrderItems orderItemsExternal = orderItemsBuilder.build();

        log.info("Creating Command");
        InvoiceCommand.Builder builder = InvoiceCommand.newBuilder();
        builder.setCommand(InvoiceCommandConstants.CREATE_INVOICE);
        builder.putPayload(
                PayloadVariableConstants.CUSTOMER,
                Any.pack(customerExternal)
        );
        builder.putPayload(
                PayloadVariableConstants.BANK_INFORMATION,
                Any.pack(bankInformationExternal)
        );
        builder.putPayload(
                PayloadVariableConstants.ORDER_ITEMS,
                Any.pack(orderItemsExternal)
        );

        InvoiceCommand invoiceCommand = builder.build();

        pulsarInvoiceProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(invoiceCommand)
                .send();

        log.info("Create Invoice Command sent: [{}]", invoiceCommand);
    }

    @Override
    @SneakyThrows
    public void cancelInvoice(String correlationId) {
        log.info("Canceling Invoice");
        log.info("Key: [{}]", correlationId);

        InvoiceCommand.Builder builder = InvoiceCommand.newBuilder();
        builder.setCommand(InvoiceCommandConstants.CANCEL_INVOICE);

        InvoiceCommand invoiceCommand = builder.build();

        pulsarInvoiceProducer
                .newMessage()
                .eventTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
                .key(correlationId)
                .value(invoiceCommand)
                .send();

        log.info("Cancel Invoice Command sent: [{}]", invoiceCommand);
    }
}
