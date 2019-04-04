package com.example.demo.cepEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.model.CircuitBreaker;
import com.example.demo.cepEngine.model.HttpRequestEvent;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.utils.PatternStatementUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-application-context.xml"})
public class CircuitBreakerTest {

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    private CEPEventHandler eventHandler;

    private static final int functionCallFailureThreshold = 3;
    private static final int circuitBreakerTimeOutInMilliseconds = 1000;
    private static final int expectedHttpRequestInterval = 5000;

    private static final HttpRequestEvent httpFailureEvent = new HttpRequestEvent("1", "failed");
    private static final HttpRequestEvent httpSuccessEvent = new HttpRequestEvent("1", "success");

    private static final HttpRequestEvent httpFailureEventService2 = new HttpRequestEvent("2", "failed");
    private static final HttpRequestEvent httpSuccessEventService2 = new HttpRequestEvent("2", "success");

    private static final String CLOSING_VIOLATION = "Closing Violation";
    private static final String TIMEOUT_VIOLATION = "Timeout Violation";

    @Before
    public void setup() {
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
    }


    @Test
    public void shouldThrowViolationIfCircuitBreakerDoesNotTripAfterThresholdExceededAndSleepPeriodIsViolated() throws IOException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);
        // exceed threshold
        for (int i = 0; i <= functionCallFailureThreshold; i++) {
            eventHandler.handle(httpFailureEvent);
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
        // violate timeout
        eventHandler.handle(httpSuccessEvent);
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void shouldOnlyThrowViolationIfFailuresAreConsecutiveAndSleepPeriodIsViolated() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);
        // failures are not consecutive -> threshold not exceeded
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpSuccessEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpSuccessEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        assertEquals(0, this.violationService.getViolationsFor(subscriber));

        // add another two failure event -> threshold exceeded but timeout not violated
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        assertEquals(0, this.violationService.getViolationsFor(subscriber));

        // violate timeout
        eventHandler.handle(httpSuccessEvent);
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void differentiateByCircuitBreakerId() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);

        // no threshold exceeded as events from different Circuit Breaker
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEventService2);
        eventHandler.handle(httpFailureEventService2);

        // fourth consecutive failure of Circuit Breaker 1 -> it should trip
        eventHandler.handle(httpFailureEvent);
        assertEquals(0, this.violationService.getViolationsFor(subscriber));

        // violate timeout
        eventHandler.handle(httpSuccessEvent);
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void shouldNotThrowVioilationIfTimeoutIsAdhered() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);
        Long delta = Math.round(circuitBreakerTimeOutInMilliseconds * 0.1);
        // exceed threshold of consecutive failures
        for (int i = 0; i <= functionCallFailureThreshold; i++) {
            eventHandler.handle(httpFailureEvent);
        }

        // adhere to timeout
        TimeUnit.MILLISECONDS.sleep(circuitBreakerTimeOutInMilliseconds + delta);
        eventHandler.handle(httpSuccessEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        eventHandler.handle(httpFailureEvent);
        assertEquals(0, this.violationService.getViolationsFor(subscriber)); // @todo fails on every third run
    }

    @Test
    public void shouldThrowViolationIfCircuitBreakerDoesNotCloseAfterTimeout() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, CLOSING_VIOLATION);
        // exceed threshold of consecutive failures
        for (int i = 0; i < 4 ; i++) {
            eventHandler.handle(httpFailureEvent);
        }

        // adhere to timeout -> emits a TimeoutPeriodElapsedEvent
        TimeUnit.MILLISECONDS.sleep(circuitBreakerTimeOutInMilliseconds);

        // do not emit another HttpRequest with same serviceId within the given time frame after a TimeOutPeriodEvent --> emits a closing violation.
        // Please note that this is just an indication for a possible closing violation. It could also be possible, that the remote function call wasn't invoked during this time.
        TimeUnit.MILLISECONDS.sleep(expectedHttpRequestInterval + 1000);

        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

}
