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
import com.example.demo.cepEngine.utils.PatternStatementUtils;
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

    private static final String INTERVAL = "1";
    private static final String REQUEST_LIMIT = "4";
    private static final String PATTERN_NAME = "RateLimiting";

    private static final HttpRequestEvent httpEventClient1 = new HttpRequestEvent("1", "success");
    private static final HttpRequestEvent httpEventClient2 = new HttpRequestEvent("2", "success");
    private static final Map<String, String> PARAMETERS = new HashMap<String, String>() {{
        put("interval", INTERVAL);
        put("requestLimit", REQUEST_LIMIT);
    }};
    private static PatternStatementSubscriber subscriber;

    @Before
    public void setup() throws IOException {
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        this.subscriber = patternUtils.preparePattern(PATTERN_NAME, PARAMETERS);
    }

    @Test
    public void testViolateRateLimitShouldBeDetected() throws FileNotFoundException, IOException, InterruptedException {
        for (int i = 0; i <= Integer.parseInt(REQUEST_LIMIT); i++) {
            eventHandler.handle(httpEventClient1);
        }
        TimeUnit.MILLISECONDS.sleep(2000); // wait till timebatch window elapsed
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void testNotThrowVioliationIfLimitIsAdhered() {
        for (int i = 1; i < Integer.parseInt(REQUEST_LIMIT); i++) {
            eventHandler.handle(httpEventClient1);
        }
        // test if events are grouped by service Id
        for (int i = 1; i < Integer.parseInt(REQUEST_LIMIT); i++) {
            eventHandler.handle(httpEventClient2);
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }




}
