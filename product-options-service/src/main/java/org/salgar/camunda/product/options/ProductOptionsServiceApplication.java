package org.salgar.camunda.product.options;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.pulsar.annotation.EnablePulsar;

@EnablePulsar
@SpringBootApplication
public class ProductOptionsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductOptionsServiceApplication.class, args);
    }
}
