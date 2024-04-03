package org.salgar.camunda;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources =  {"classpath*:*.bpmn", "classpath*:*.dmn"})
public class OrchestrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrchestrationApplication.class, args);
    }
}
