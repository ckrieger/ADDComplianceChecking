package com.motivatingscenario.orderService;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("Application started");
    }

    @Bean
    ApplicationRunner init() {
        return args -> {
            System.out.println("Init");
        };
    }
}
