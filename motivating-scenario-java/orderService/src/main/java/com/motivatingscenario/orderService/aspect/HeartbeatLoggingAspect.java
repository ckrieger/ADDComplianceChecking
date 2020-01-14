package com.motivatingscenario.orderService.aspect;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class HeartbeatLoggingAspect {

    private  static final Logger logger = LoggerFactory.getLogger(HeartbeatLoggingAspect.class);

    class Task extends TimerTask{
        public void run(){
            logger.info("{type:VirtualMachine, event:{vmId:" + getVMId() + ", scalingGroupId:" + getScalingGroup() + "}}");
        }
    }

    @After("execution(* com.motivatingscenario.orderService.OrderServiceApplication.*(..))")
    public void logAfterReturning(JoinPoint joinPoint) {
        System.out.println("Advice");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Task(), 0, 3000);
    }

    private String getVMId() {
        String vmId = "";
        try {
            vmId = InetAddress.getLocalHost().toString().split("-")[1];
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return vmId;
    }

    private String getScalingGroup() {
        String scalingGroup = "";
        try {
            scalingGroup = InetAddress.getLocalHost().toString().split("-")[0];
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return scalingGroup;
    }
}
