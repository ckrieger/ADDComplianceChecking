package com.example.demo.cepEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
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

    private static final String FAILED_HTTP_REQUESTEVENT = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '1', 'statusCode': 'failed'}}";
    private static final String SUCCEDED_HTTP_REQUESTEVENT = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '1', 'statusCode': 'success'}}";

    private static final String FAILED_HTTP_REQUESTEVENT2 = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '2', 'statusCode': 'failed'}}";

    private static EventInstance httpFailureEvent;
    private static EventInstance httpSuccessEvent;
    private static EventInstance httpFailureEventService2;

    private static final Map<String, String> PARAMETERS = new HashMap<String, String>() {{
        put("failureThreshold", String.valueOf(functionCallFailureThreshold));
        put("timeout", String.valueOf(circuitBreakerTimeOutInMilliseconds));
    }};
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
        PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker", PARAMETERS);
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
        PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker", PARAMETERS);
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
        PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker", PARAMETERS);

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
}
