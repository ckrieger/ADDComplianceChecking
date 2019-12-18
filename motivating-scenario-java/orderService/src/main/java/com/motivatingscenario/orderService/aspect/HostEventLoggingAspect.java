package com.motivatingscenario.orderService.aspect;

import java.util.Timer;
import java.util.TimerTask;

import com.motivatingscenario.orderService.HomeController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class HostEventLoggingAspect {

    private  static final Logger logger = LoggerFactory.getLogger(HostEventLoggingAspect.class);

    class Task extends TimerTask{
        public void run(){
            logger.info("hello");
        }
    }

    @After("execution(* com.motivatingscenario.orderService.OrderServiceApplication.main(..))")
    public void logAfterReturning(JoinPoint joinPoint) {
        Timer timer = new Timer();
        timer.schedule(new Task(), 3000);
    }
}
