package org.salgar.camunda.port;

import org.salgar.camunda.core.model.BankInformation;
import org.salgar.camunda.core.model.Customer;
import org.salgar.camunda.core.model.OrderItem;

import java.util.List;

public interface InvoiceOutboundPort {
    void createInvoice(
            String correlationId,
            Customer customer,
            BankInformation bankInformation,
            List<OrderItem> orderItems);

    void cancelInvoice(String correlationId, String sourceProcess);
}
