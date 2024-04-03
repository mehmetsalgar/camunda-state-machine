package org.salgar.camunda.core.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.schema.ProtobufSchema;
import org.salgar.camunda.customer.response.CustomerResponse;
import org.salgar.camunda.inventory.response.InventoryResponse;
import org.salgar.camunda.invoice.response.InvoiceResponse;
import org.salgar.camunda.order.response.OrderResponse;
import org.salgar.camunda.port.CustomerInboundPort;
import org.salgar.camunda.port.InventoryInboundPort;
import org.salgar.camunda.port.InvoiceInboundPort;
import org.salgar.camunda.port.OrderInboundPort;
import org.salgar.camunda.port.ProductOptionsInboundPort;
import org.salgar.camunda.product.options.response.ProductOptionsResponse;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PulsarConfig {
    @Bean
    @ConfigurationProperties("spring.pulsar")
    public PulsarProperties pulsarProperties() {
        return new PulsarProperties();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.customer")
    public PulsarProperties.Producer customerProducerProperties() {
        return new PulsarProperties.Producer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.customer")
    public PulsarProperties.Consumer customerConsumerProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.inventory")
    public PulsarProperties.Producer inventoryProducerProperties() {
        return new PulsarProperties.Producer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.inventory")
    public PulsarProperties.Consumer inventoryConsumerProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.invoice")
    public PulsarProperties.Producer invoiceProducerProperties() {
        return new PulsarProperties.Producer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.invoice")
    public PulsarProperties.Consumer invoiceConsumerProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.order")
    public PulsarProperties.Producer orderProducerProperties() {
        return new PulsarProperties.Producer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.order")
    public PulsarProperties.Consumer orderConsumerProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.producer.product-options")
    public PulsarProperties.Producer productOptionsProducerProperties() {
        return new PulsarProperties.Producer();
    }

    @Bean
    @ConfigurationProperties("spring.pulsar.consumer.product-options")
    public PulsarProperties.Consumer productOptionsConsumerProperties() {
        return new PulsarProperties.Consumer();
    }

    @Bean
    @SneakyThrows
    public PulsarClient pulsarClient(PulsarProperties pulsarProperties) {
        return PulsarClient
                .builder()
                .serviceUrl(pulsarProperties.getClient().getServiceUrl())
                .build();
                //.connectionsPerBroker(pulsarProperties.getClient(). pulsarClientConfig.connectionsPerBroker())
                //.enableBusyWait(pulsarProperties.getConsumer(). pulsarClientConfig.enableBusyWait())
                //.enableTcpNoDelay(pulsarClientConfig.enableTcpNoDelay())
                //.enableTlsHostnameVerification(pulsarClientConfig.enableTlsHostnameVerification())
                //.enableTransaction(pulsarClientConfig.enableTransaction())
    }

    @Bean
    @SneakyThrows
    public Producer<org.salgar.camunda.order.command.OrderCommand> pulsarOrderProducer(
            PulsarClient pulsarClient,
            PulsarProperties.Producer orderProducerProperties) {
        return pulsarClient
                .newProducer(ProtobufSchema.of(org.salgar.camunda.order.command.OrderCommand.class))
                //.newProducer(Schema.STRING)
                .topic(orderProducerProperties.getTopicName())
                .create();
    }

    @Bean
    @SneakyThrows
    public Consumer<OrderResponse> pulsarOrderConsumer(
            PulsarClient pulsarClient,
            PulsarProperties.Consumer orderConsumerProperties,
            OrderInboundPort orderInboundPort) {

        final MessageListener<OrderResponse> messageListener = (consumer, msg) -> {
            try {
                log.info("We received the Message from Pulsar with Key:[{}] and Value: [{}] ", msg.getKey(), msg.getValue());
                orderInboundPort.processOrderResponse(msg);

                consumer.acknowledge(msg);

            } catch(PulsarClientException pce) {
                log.error(pce.getMessage(), pce);
            }
        };

        return pulsarClient
                .newConsumer(ProtobufSchema.of(org.salgar.camunda.order.response.OrderResponse.class))
                .topic(orderConsumerProperties.getTopics().stream().findFirst().isPresent()?orderConsumerProperties.getTopics().stream().findFirst().get():"")
                .subscriptionName(orderConsumerProperties.getSubscription().getName())
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(messageListener)
                .subscribe();
    }

    @Bean
    @SneakyThrows
    public Producer<org.salgar.camunda.customer.command.CustomerCommand> pulsarCustomerProducer(
            PulsarClient pulsarClient,
            PulsarProperties.Producer customerProducerProperties) {
        return pulsarClient
                .newProducer(ProtobufSchema.of(org.salgar.camunda.customer.command.CustomerCommand.class))
                //.newProducer(Schema.STRING)
                .topic(customerProducerProperties.getTopicName())
                .create();
    }

    @Bean
    @SneakyThrows
    public Consumer<CustomerResponse> pulsarCustomerConsumer(
            PulsarClient pulsarClient,
            PulsarProperties.Consumer customerConsumerProperties,
            CustomerInboundPort customerInboundPort) {

        final MessageListener<CustomerResponse> messageListener = (consumer, msg) -> {
            try {
                log.info("We received the Message from Pulsar with Key:[{}] and Value: [{}] ", msg.getKey(), msg.getValue());
                customerInboundPort.processCustomerResponse(msg);

                consumer.acknowledge(msg);

            } catch(PulsarClientException pce) {
                log.error(pce.getMessage(), pce);
            }
        };

        return pulsarClient
                .newConsumer(ProtobufSchema.of(CustomerResponse.class))
                .topic(customerConsumerProperties.getTopics().stream().findFirst().isPresent()?customerConsumerProperties.getTopics().stream().findFirst().get():"")
                .subscriptionName(customerConsumerProperties.getSubscription().getName())
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(messageListener)
                .subscribe();
    }

    @Bean
    @SneakyThrows
    public Producer<org.salgar.camunda.product.options.command.ProductOptionsCommand> pulsarProductOptionsProducer(
            PulsarClient pulsarClient,
            PulsarProperties.Producer productOptionsProducerProperties) {
        return pulsarClient
                .newProducer(ProtobufSchema.of(org.salgar.camunda.product.options.command.ProductOptionsCommand.class))
                //.newProducer(Schema.STRING)
                .topic(productOptionsProducerProperties.getTopicName())
                .create();
    }

    @Bean
    @SneakyThrows
    public Consumer<ProductOptionsResponse> pulsarProductOptionsConsumer(
            PulsarClient pulsarClient,
            PulsarProperties.Consumer productOptionsConsumerProperties,
            ProductOptionsInboundPort productOptionsInboundPort) {

        final MessageListener<ProductOptionsResponse> messageListener = (consumer, msg) -> {
            try {
                log.info("We received the Message from Pulsar with Key:[{}] and Value: [{}] ", msg.getKey(), msg.getValue());
                productOptionsInboundPort.processProductOptionsResponse(msg);

                consumer.acknowledge(msg);

            } catch(PulsarClientException pce) {
                log.error(pce.getMessage(), pce);
            }
        };

        return pulsarClient
                .newConsumer(ProtobufSchema.of(ProductOptionsResponse.class))
                .topic(productOptionsConsumerProperties.getTopics().stream().findFirst().isPresent()?productOptionsConsumerProperties.getTopics().stream().findFirst().get():"")
                .subscriptionName(productOptionsConsumerProperties.getSubscription().getName())
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(messageListener)
                .subscribe();
    }

    @Bean
    @SneakyThrows
    public Producer<org.salgar.camunda.inventory.command.InventoryCommand> pulsarInventoryProducer(
            PulsarClient pulsarClient,
            PulsarProperties.Producer inventoryProducerProperties) {
        return pulsarClient
                .newProducer(ProtobufSchema.of(org.salgar.camunda.inventory.command.InventoryCommand.class))
                //.newProducer(Schema.STRING)
                .topic(inventoryProducerProperties.getTopicName())
                .create();
    }

    @Bean
    @SneakyThrows
    public Consumer<InventoryResponse> pulsarInventoryConsumer(
            PulsarClient pulsarClient,
            PulsarProperties.Consumer inventoryConsumerProperties,
            InventoryInboundPort inventoryInboundPort) {

        final MessageListener<InventoryResponse> messageListener = (consumer, msg) -> {
            try {
                log.info("We received the Message from Pulsar with Key:[{}] and Value: [{}] ", msg.getKey(), msg.getValue());
                inventoryInboundPort.processInventoryResponse(msg);

                consumer.acknowledge(msg);

            } catch(PulsarClientException pce) {
                log.error(pce.getMessage(), pce);
            }
        };

        return pulsarClient
                .newConsumer(ProtobufSchema.of(InventoryResponse.class))
                .topic(inventoryConsumerProperties.getTopics().stream().findFirst().isPresent()?inventoryConsumerProperties.getTopics().stream().findFirst().get():"")
                .subscriptionName(inventoryConsumerProperties.getSubscription().getName())
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(messageListener)
                .subscribe();
    }

    @Bean
    @SneakyThrows
    public Producer<org.salgar.camunda.invoice.command.InvoiceCommand> pulsarInvoiceProducer(
            PulsarClient pulsarClient,
            PulsarProperties.Producer invoiceProducerProperties) {
        return pulsarClient
                .newProducer(ProtobufSchema.of(org.salgar.camunda.invoice.command.InvoiceCommand.class))
                //.newProducer(Schema.STRING)
                .topic(invoiceProducerProperties.getTopicName())
                .create();
    }

    @Bean
    @SneakyThrows
    public Consumer<InvoiceResponse> pulsarInvoiceConsumer(
            PulsarClient pulsarClient,
            PulsarProperties.Consumer invoiceConsumerProperties,
            InvoiceInboundPort invoiceInboundPort) {

        final MessageListener<InvoiceResponse> messageListener = (consumer, msg) -> {
            try {
                log.info("We received the Message from Pulsar with Key:[{}] and Value: [{}] ", msg.getKey(), msg.getValue());
                invoiceInboundPort.processInvoiceResponse(msg);

                consumer.acknowledge(msg);

            } catch(PulsarClientException pce) {
                log.error(pce.getMessage(), pce);
            }
        };

        return pulsarClient
                .newConsumer(ProtobufSchema.of(InvoiceResponse.class))
                .topic(invoiceConsumerProperties.getTopics().stream().findFirst().isPresent()?invoiceConsumerProperties.getTopics().stream().findFirst().get():"")
                .subscriptionName(invoiceConsumerProperties.getSubscription().getName())
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(messageListener)
                .subscribe();
    }
}
