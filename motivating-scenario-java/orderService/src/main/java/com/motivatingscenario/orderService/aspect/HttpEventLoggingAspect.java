package com.motivatingscenario.orderService.aspect;

import java.net.InetAddress;

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
public class HttpEventLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(HttpEventLoggingAspect.class);

    @AfterReturning(
            pointcut = "execution(* com.motivatingscenario.orderService.restclient.ShippingServiceConsumer.get(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("after execution of {}", joinPoint);
    }

    @AfterThrowing(
            pointcut = "execution(* com.motivatingscenario.orderService.restclient.*.*(..))",
            throwing = "ex")
    public void logAfterThrowing(HttpStatusCodeException ex)
            throws Throwable {
        // log 404 not found exceptions
        if (ex instanceof HttpClientErrorException.NotFound) {
            logger.info("{ type: HttpRequestEvent, event: {serviceId: " + InetAddress.getLocalHost() + ", statusCode: 404}}");
        }
    }
}
