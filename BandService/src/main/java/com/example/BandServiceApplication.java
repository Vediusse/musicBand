package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example", "Kafka", "Aspects"})
public class BandServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BandServiceApplication.class, args);
    }
}
