package com.motivatingscenario.orderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableZuulProxy
@SpringCloudApplication
public class OrderServiceApplication {
    private  static final Logger logger = LoggerFactory.getLogger(OrderServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("Application started");
        logger.info("{ type: VirtualMachine, event: {vmId: 1 , scalingGroupId: 2}}");
    }

    @Bean
    ApplicationRunner init() {
        return args -> {
            System.out.println("Init");
        };
    }
}
