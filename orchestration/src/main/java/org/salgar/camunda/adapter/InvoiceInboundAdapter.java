package org.salgar.camunda.adapter;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.salgar.camunda.core.adapter.AbstractInboundAdapter;
import org.salgar.camunda.core.util.ZeebeMessageConstants;
import org.salgar.camunda.invoice.response.InvoiceCancellationSuccessful;
import org.salgar.camunda.invoice.response.InvoiceCreated;
import org.salgar.camunda.invoice.response.InvoiceResponse;
import org.salgar.camunda.invoice.utils.PayloadVariableConstants;
import org.salgar.camunda.invoice.utils.ResponseConstants;
import org.salgar.camunda.port.InvoiceInboundPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InvoiceInboundAdapter extends AbstractInboundAdapter implements InvoiceInboundPort {
    public InvoiceInboundAdapter(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    @Override
    @SneakyThrows
    public void processInvoiceResponse(Message<InvoiceResponse> message) {
        if(ResponseConstants.INVOICE_CREATED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            InvoiceCreated invoiceCreated = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.INVOICE_CREATED)
                    .unpack(InvoiceCreated.class);

            variables.put(
                    PayloadVariableConstants.INVOICE_CREATED,
                    invoiceCreated.getInvoiceCreated());

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.INVOICE_CREATED_MESSAGE,
                    variables);
        } else if(ResponseConstants.INVOICE_FAILED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            InvoiceCreated invoiceCreated = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.INVOICE_CREATED)
                    .unpack(InvoiceCreated.class);

            variables.put(
                    PayloadVariableConstants.INVOICE_CREATED,
                    invoiceCreated.getInvoiceCreated());

            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.INVOICE_CREATED_MESSAGE,
                    variables);
        } else if(ResponseConstants.INVOICE_CANCELED.equals(message.getValue().getResponse())) {
            Map<String, Object> variables = new HashMap<>();
            InvoiceCancellationSuccessful invoiceCancellationSuccessful = message
                    .getValue()
                    .getPayloadMap()
                    .get(PayloadVariableConstants.INVOICE_CANCELLATION_SUCCESSFUL)
                    .unpack(InvoiceCancellationSuccessful.class);
            variables.put(PayloadVariableConstants.INVOICE_CANCELLATION_SUCCESSFUL,
                    invoiceCancellationSuccessful.getInvoiceCancelationSuccessful());
            processZeebeMessage(
                    message.getKey(),
                    ZeebeMessageConstants.INVOICE_CANCELLATION_MESSAGE,
                    variables);
        } else {
            log.info("Unknown command: [{}]", message.getValue().getResponse());
        }
    }
}
