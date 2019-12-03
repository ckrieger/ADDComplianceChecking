package com.example.demo.cepEngine;

import com.example.demo.cepEngine.utils.RemoteServiceTestCommand;
import com.example.demo.cepEngine.utils.RemoteServiceTestSimulator;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HystrixCircuitBreakerTest {

    @Test
    public void givenCircuitBreakerSetup__whenRemoteSvcCmdExecuted_thenReturnSuccess()
            throws InterruptedException {

        HystrixCommand.Setter config = HystrixCommand
                .Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupCircuitBreaker"));
        HystrixCommandProperties.Setter properties = HystrixCommandProperties.Setter();
        properties.withExecutionTimeoutInMilliseconds(2000); // timeout for considering request as failed

        properties.withCircuitBreakerSleepWindowInMilliseconds(5000);
        properties.withExecutionIsolationStrategy(
                HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
        properties.withCircuitBreakerEnabled(true);
        properties.withCircuitBreakerRequestVolumeThreshold(1);

        config.andCommandPropertiesDefaults(properties);

        config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withMaxQueueSize(1)
                .withCoreSize(1)
                .withQueueSizeRejectionThreshold(1));

        assertThat(this.invokeRemoteService(config, 1000, true), equalTo(null));
        assertThat(this.invokeRemoteService(config, 1000, true), equalTo(null));
        assertThat(this.invokeRemoteService(config, 1000, true), equalTo(null));
        assertThat(this.invokeRemoteService(config, 1000, true), equalTo(null));
        Thread.sleep(5000);

//        assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
//                equalTo("Success"));
//        assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
//                equalTo("Success"));
//        assertThat(new RemoteServiceTestCommand(config, new RemoteServiceTestSimulator(500)).execute(),
//                equalTo("Success"));
    }

    public String invokeRemoteService(HystrixCommand.Setter config, int timeout, Boolean shouldFail)
            throws InterruptedException {
        String response = null;
        try {
            response = new RemoteServiceTestCommand(config,
                    new RemoteServiceTestSimulator(timeout, shouldFail)).execute();
        } catch (HystrixRuntimeException ex) {
            System.out.println("ex = " + ex);
        }
        return response;
    }


}
