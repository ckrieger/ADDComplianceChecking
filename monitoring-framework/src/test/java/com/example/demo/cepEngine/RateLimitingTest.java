package com.example.demo.cepEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
@ContextConfiguration("/test-application-context.xml")
public class RateLimitingTest {

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private CEPEventHandler eventHandler;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    EventTypeUtils eventTypeUtils;

    private static final String INTERVAL = "1";
    private static final String REQUEST_LIMIT = "4";
    private static final String PATTERN_NAME = "RateLimiting";

    private static EventInstance httpEventClient1;
    private static EventInstance httpEventClient2;
    private static final Map<String, String> PARAMETERS = new HashMap<String, String>() {{
        put("interval", INTERVAL);
        put("requestLimit", REQUEST_LIMIT);
    }};
    private static PatternStatementSubscriber subscriber;

    @Before
    public void setup() throws IOException {
        setupTestEvents();
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        eventTypeUtils.addEventTypes();
        this.subscriber = patternUtils.preparePattern(PATTERN_NAME, PARAMETERS);
    }

    private void setupTestEvents() {
        Gson g = new Gson();
        httpEventClient1 = g.fromJson("{'type': 'HttpRequestEvent', 'event' : {'serviceId': '1', 'statusCode': '200'}}", EventInstance.class);
        httpEventClient2 = g.fromJson("{'type': 'HttpRequestEvent', 'event' : {'serviceId': '2', 'statusCode': '200'}}", EventInstance.class);
    }

    @Test
    public void testViolateRateLimitShouldBeDetected() throws FileNotFoundException, IOException, InterruptedException {
        for (int i = 0; i <= Integer.parseInt(REQUEST_LIMIT); i++) {
            eventHandler.handle(httpEventClient1.getEvent(), httpEventClient1.getType());
        }
        TimeUnit.MILLISECONDS.sleep(2000); // wait till timebatch window elapsed
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void testNotThrowVioliationIfLimitIsAdhered() {
        for (int i = 1; i < Integer.parseInt(REQUEST_LIMIT); i++) {
            eventHandler.handle(httpEventClient1.getEvent(), httpEventClient1.getType());
        }
        // test if events are grouped by service Id
        for (int i = 1; i < Integer.parseInt(REQUEST_LIMIT); i++) {
            eventHandler.handle(httpEventClient2.getEvent(), httpEventClient2.getType());
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }




}
