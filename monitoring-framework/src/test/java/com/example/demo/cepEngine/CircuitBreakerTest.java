package com.example.demo.cepEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.model.HttpRequestEvent;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.utils.EventTypeUtils;
import com.example.demo.cepEngine.utils.PatternStatementUtils;

import com.example.demo.model.EventInstance;
import com.google.gson.Gson;
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
    @Autowired
    private EventTypeUtils eventTypeUtils;

    private static final int functionCallFailureThreshold = 3;
    private static final int circuitBreakerTimeOutInMilliseconds = 1000;
    private static final int expectedHttpRequestInterval = 5000;

    private static final String FAILED_HTTP_REQUESTEVENT = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '1', 'statusCode': 'failed'}}";
    private static final String SUCCEDED_HTTP_REQUESTEVENT = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '1', 'statusCode': 'success'}}";

    private static final String FAILED_HTTP_REQUESTEVENT2 = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '2', 'statusCode': 'failed'}}";

    private static EventInstance httpFailureEvent;
    private static EventInstance httpSuccessEvent;
    private static EventInstance httpFailureEventService2;

    private static final String CLOSING_VIOLATION = "Closing Violation";
    private static final String TIMEOUT_VIOLATION = "Timeout Violation";

    @Before
    public void setup() throws IOException {
        setupTestEvents();
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        eventTypeUtils.addEventTypes();
    }

    private void setupTestEvents() {
        Gson g = new Gson();
        httpFailureEvent = g.fromJson(FAILED_HTTP_REQUESTEVENT, EventInstance.class);
        httpSuccessEvent = g.fromJson(SUCCEDED_HTTP_REQUESTEVENT, EventInstance.class);
        httpFailureEventService2 = g.fromJson(FAILED_HTTP_REQUESTEVENT2, EventInstance.class);
    }


    @Test
    public void shouldThrowViolationIfCircuitBreakerDoesNotTripAfterThresholdExceededOrSleepPeriodIsViolated() throws IOException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);
        // exceed threshold
        for (int i = 0; i <= functionCallFailureThreshold; i++) {
            eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
        // violate timeout
        eventHandler.handle(httpSuccessEvent.getEvent(), httpSuccessEvent.getType());
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void shouldOnlyThrowViolationIfFailuresAreConsecutiveAndSleepPeriodIsViolated() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);
        // failures are not consecutive -> threshold not exceeded
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpSuccessEvent.getEvent(), httpSuccessEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpSuccessEvent.getEvent(), httpSuccessEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        assertEquals(0, this.violationService.getViolationsFor(subscriber));

        // add another two failure event -> threshold exceeded but timeout not violated
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        assertEquals(0, this.violationService.getViolationsFor(subscriber));

        // violate timeout
        eventHandler.handle(httpSuccessEvent.getEvent(), httpSuccessEvent.getType());
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void differentiateByCircuitBreakerId() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);

        // no threshold exceeded as events from different Circuit Breaker
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEventService2.getEvent(), httpFailureEventService2.getType());
        eventHandler.handle(httpFailureEventService2.getEvent(), httpFailureEventService2.getType());

        // fourth consecutive failure of Circuit Breaker 1 -> it should trip
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        assertEquals(0, this.violationService.getViolationsFor(subscriber));

        // violate timeout
        eventHandler.handle(httpSuccessEvent.getEvent(), httpSuccessEvent.getType());
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void shouldNotThrowVioilationIfTimeoutIsAdhered() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, TIMEOUT_VIOLATION);
        Long delta = Math.round(circuitBreakerTimeOutInMilliseconds * 0.1);
        // exceed threshold of consecutive failures
        for (int i = 0; i <= functionCallFailureThreshold; i++) {
            eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        }

        // adhere to timeout
        TimeUnit.MILLISECONDS.sleep(circuitBreakerTimeOutInMilliseconds + delta);
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        assertEquals(0, this.violationService.getViolationsFor(subscriber)); // @todo fails on every third run
    }

    @Test
    public void shouldThrowViolationIfCircuitBreakerDoesNotCloseAfterTimeout() throws FileNotFoundException, IOException, InterruptedException {
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("CircuitBreaker");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, CLOSING_VIOLATION);
        // exceed threshold of consecutive failures
        for (int i = 0; i < 4 ; i++) {
            eventHandler.handle(httpFailureEvent.getEvent(), httpFailureEvent.getType());
        }

        // adhere to timeout -> emits a TimeoutPeriodElapsedEvent
        TimeUnit.MILLISECONDS.sleep(circuitBreakerTimeOutInMilliseconds);

        // do not emit another HttpRequest with same serviceId within the given time frame after a TimeOutPeriodEvent --> emits a closing violation.
        // Please note that this is just an indication for a possible closing violation. It could also be possible, that the remote function call wasn't invoked during this time.
        TimeUnit.MILLISECONDS.sleep(expectedHttpRequestInterval + 1000);

        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

}
