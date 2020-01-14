package com.motivatingscenario.orderService.aspect;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class HttpEventLoggingForRateLimitingAspect {

    private static final Logger logger = LoggerFactory.getLogger(HttpEventLoggingForRateLimitingAspect.class);

    @AfterReturning(
            pointcut = "execution(* com.motivatingscenario.orderService.HomeController.getOrdersWithShippingInfo(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) throws UnknownHostException {
        logger.info("{ type: HttpRequestEvent, event: {serviceId: " + InetAddress.getLocalHost() + ", statusCode: 200}}");
    }
}
