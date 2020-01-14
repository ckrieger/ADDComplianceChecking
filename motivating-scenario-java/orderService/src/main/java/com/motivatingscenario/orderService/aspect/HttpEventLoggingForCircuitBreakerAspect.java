package com.motivatingscenario.orderService.aspect;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@Aspect
@Configuration
public class HttpEventLoggingForCircuitBreakerAspect {
    private static final Logger logger = LoggerFactory.getLogger(HttpEventLoggingForCircuitBreakerAspect.class);

    @AfterReturning(
            pointcut = "execution(* com.motivatingscenario.orderService.restclient.ShippingServiceConsumer.get(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) throws UnknownHostException {
        logger.info("{ type: HttpRequestEvent, event: {serviceId: " + InetAddress.getLocalHost() + ", statusCode: success}}");
    }

    @AfterThrowing(
            pointcut = "execution(* com.motivatingscenario.orderService.restclient.*.*(..))",
            throwing = "ex")
    public void logAfterThrowing(HttpStatusCodeException ex)
            throws Throwable {
        // log 404 not found exceptions
        if (ex instanceof HttpClientErrorException.NotFound) {
            logger.info("{ type: HttpRequestEvent, event: {serviceId: " + InetAddress.getLocalHost() + ", statusCode: failed}}");
        }
    }
}
