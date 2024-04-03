package org.salgar.camunda.invoice.service.adapter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.salgar.camunda.invoice.response.InvoiceResponse;
import org.salgar.camunda.invoice.service.port.InvoiceOutboundPort;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceOutboundAdapter implements InvoiceOutboundPort {
    private final PulsarTemplate<InvoiceResponse> pulsarTemplate;
    private final PulsarProperties.Producer invoiceOutboundProperties;

    @Override
    @SneakyThrows
    public void deliverInvoiceResponse(String correlationId, InvoiceResponse inventoryResponse) {
        log.info("Sending InvoiceResponse: [{}]", inventoryResponse);
        pulsarTemplate
                .newMessage(inventoryResponse)
                .withMessageCustomizer(mc -> {
                    mc.key(correlationId);
                })
                .withTopic(invoiceOutboundProperties.getTopicName())
                .send();
    }
}
