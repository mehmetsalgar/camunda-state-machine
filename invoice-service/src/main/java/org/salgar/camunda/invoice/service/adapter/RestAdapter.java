package org.salgar.camunda.invoice.service.adapter;

import com.google.protobuf.Any;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.invoice.response.InvoiceCancellationSuccessful;
import org.salgar.camunda.invoice.response.InvoiceCreated;
import org.salgar.camunda.invoice.response.InvoiceResponse;
import org.salgar.camunda.invoice.service.port.RestPort;
import org.salgar.camunda.invoice.utils.PayloadVariableConstants;
import org.salgar.camunda.invoice.utils.ResponseConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAdapter implements RestPort {
    private final InvoiceOutboundAdapter invoiceOutboundAdapter;

    @PostMapping("/invoice/response")
    public void prepare(ServerWebExchange exchange) {
        log.info("Preparing response");
        String correlationId = exchange.getRequest().getQueryParams().getFirst("correlationId");
        if (correlationId == null) {
            correlationId = "";
        }

        String response = exchange.getRequest().getQueryParams().getFirst("response");
        if (response == null) {
            correlationId = "";
        }
        processInvoiceResponse(correlationId, response);
    }

    @Override
    public void processInvoiceResponse(String correlationId, String response) {
        String customerResponse = null;
        if("create".equals(response)) {
            customerResponse = ResponseConstants.INVOICE_CREATED;
            prepareInvoiceCreated(correlationId, customerResponse);
        } else if("fail".equals(response)) {
            customerResponse = ResponseConstants.INVOICE_FAILED;
            prepareInvoiceFailed(correlationId, customerResponse);
        } else if("cancel".equals(response)) {
            customerResponse = ResponseConstants.INVOICE_CANCELED;
            prepareInvoiceCanceled(correlationId, customerResponse);
        } else {
            log.info("Unknown response: [{}]", response);
        }
    }

    private void prepareInvoiceCreated(String correlationId, String invoiceResponse) {
        InvoiceResponse.Builder builder = InvoiceResponse.newBuilder();
        builder.setResponse(invoiceResponse);
        builder.putPayload(
                PayloadVariableConstants.INVOICE_CREATED,
                Any.pack(InvoiceCreated.newBuilder().setInvoiceCreated(true).build())
        );

        invoiceOutboundAdapter.deliverInvoiceResponse(correlationId, builder.build());
    }

    private void prepareInvoiceFailed(String correlationId, String invoiceResponse) {
        InvoiceResponse.Builder builder = InvoiceResponse.newBuilder();
        builder.setResponse(invoiceResponse);
        builder.putPayload(
                PayloadVariableConstants.INVOICE_CREATED,
                Any.pack(InvoiceCreated.newBuilder().setInvoiceCreated(false).build())
        );

        invoiceOutboundAdapter.deliverInvoiceResponse(correlationId, builder.build());
    }

    private void prepareInvoiceCanceled(String correlationId, String invoiceResponse) {
        InvoiceResponse.Builder builder = InvoiceResponse.newBuilder();
        builder.setResponse(invoiceResponse);
        builder.putPayload(
                PayloadVariableConstants.INVOICE_CANCELLATION_SUCCESSFUL,
                Any.pack(InvoiceCancellationSuccessful.newBuilder().setInvoiceCancelationSuccessful(true).build())
        );

        invoiceOutboundAdapter.deliverInvoiceResponse(correlationId, builder.build());
    }
}
