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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Aspect
@Configuration
public class HttpEventLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(HttpEventLoggingAspect.class);

    @AfterReturning(
            pointcut = "execution(* com.motivatingscenario.orderService.restclient.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) throws UnknownHostException {
        logger.info("{ type: HttpRequestEvent, event: {serviceId: " + InetAddress.getLocalHost() + ", statusCode: success}}");
    }

    @AfterThrowing(
            pointcut = "execution(* com.motivatingscenario.orderService.restclient.*.*(..))",
            throwing = "ex")
    public void logAfterThrowing(Exception ex)
            throws Throwable {

        if (ex instanceof HttpServerErrorException|| ex instanceof ResourceAccessException) {
            logger.info("{ type: HttpRequestEvent, event: {serviceId: " + InetAddress.getLocalHost() + ", statusCode: failed}}");
        }
    }
}
