package org.salgar.camunda.core.adapter;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractInboundAdapter {
    private final ZeebeClient zeebeClient;

    protected void processZeebeMessage(
            String correlationId,
            String zeebeMessage,
            Map<String, Object> variables) {
        zeebeClient
                .newPublishMessageCommand()
                .messageName(zeebeMessage)
                .correlationKey(correlationId)
                .variables(variables)
                .send()
                .join();
    }
}
