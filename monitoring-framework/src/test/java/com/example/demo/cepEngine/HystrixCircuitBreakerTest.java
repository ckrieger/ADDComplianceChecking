package com.example.demo.cepEngine;

import java.io.IOException;
import java.util.List;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.utils.EventTypeUtils;
import com.example.demo.cepEngine.utils.PatternStatementUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-application-context.xml"})
public class HystrixCircuitBreakerTest {

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    private CEPEventHandler eventHandler;
    @Autowired
    private EventTypeUtils eventTypeUtils;

    private static final int functionCallFailureThreshold = 3;
    private static final String TIMEOUT_VIOLATION = "Timeout Violation";
    HystrixCommand.Setter config;

    @Before
    public void setup() throws IOException {
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        eventTypeUtils.addEventTypes();
        config = HystrixCommand
                .Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceGroupCircuitBreaker"));
        HystrixCommandProperties.Setter properties = HystrixCommandProperties.Setter();

        properties.withExecutionTimeoutInMilliseconds(2000); // timeout for considering request as failed

        properties.withCircuitBreakerSleepWindowInMilliseconds(5000);
        properties.withExecutionIsolationStrategy(
                HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
        properties.withCircuitBreakerEnabled(true);
        properties.withCircuitBreakerRequestVolumeThreshold(functionCallFailureThreshold + 1); // configuration of circuit breaker violates expected failure threshold

        config.andCommandPropertiesDefaults(properties);

        config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withMaxQueueSize(1)
                .withCoreSize(1)
                .withQueueSizeRejectionThreshold(1));
    }

    @Test
    public void shouldThrowViolationIfCircuitBreakerDoesNotTripAfterThresholdExceededOrSleepPeriodIsViolated() throws InterruptedException, IOException {

        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);

        // exceed threshold
        for (int i = 0; i <= functionCallFailureThreshold; i++) {
            this.invokeRemoteService(config, 1000, true);
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
        // violate timeout
        this.invokeRemoteService(config, 1000, true);
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    public String invokeRemoteService(HystrixCommand.Setter config, int timeout, Boolean shouldFail)
            throws InterruptedException {
        String response = null;
        try {
            response = new RemoteServiceTestCommand(config,
                    new RemoteServiceTestSimulator(timeout, shouldFail), eventHandler).execute();
        } catch (HystrixRuntimeException ex) {
            System.out.println("ex = " + ex);
        }
        return response;
    }
}
