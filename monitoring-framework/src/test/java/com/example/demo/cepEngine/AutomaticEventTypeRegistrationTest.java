package com.example.demo.cepEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.model.EventInstance;
import com.example.demo.cepEngine.model.GenericEventType;
import com.example.demo.model.EventType;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.utils.PatternStatementUtils;
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
public class AutomaticEventTypeRegistrationTest {

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    private CEPEventHandler eventHandler;

    private static final String STATICWORKLOAD_VIOLATION = "StaticWorkload Violation";
    private static final String EVENTTYPE = "{ 'type': 'FirstEvent', 'propertyNames': ['carId', 'direction'], 'propertyTypes': [String.class, int.class]}";
    private static final String MAPEEVENTTYPE = "{'id': 1, 'type': 'FirstEvent', 'properties': [{'id': 1, 'name': 'carId', 'type': 'java.lang.String'}, {'id': 2, 'name': 'direction', 'type': 'java.lang.Integer'}]}";
    private static final String EVENT = "{'type': 'FirstEvent', 'event': {'carId' : 'hollywood', 'direction': 1}}";
    @Before
    public void setup() {
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
    }


    @Test
    public void defineViaObjectArrayProperties() throws IOException {
        Gson gson = new Gson();
        GenericEventType eventType = gson.fromJson(EVENTTYPE, GenericEventType.class);
        Object[] propertyTypes = {String.class, int.class};
        eventHandler.addEventType(eventType.getType(), eventType.getPropertyNames(), propertyTypes);
        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("RuleWithUnregisteredEventType");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, STATICWORKLOAD_VIOLATION);
        Object[] event = {"hollywood", 1};

        eventHandler.handle(event, eventType.getType());
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void defineViaMapProperties() throws IOException, ClassNotFoundException {
        Gson gson = new Gson();
        EventType eventType = gson.fromJson(MAPEEVENTTYPE, EventType.class);

        Map<String, Object> eventProperties = new HashMap<String, Object>();
        eventType.getProperties().forEach(p -> {
            try {
                eventProperties.put(p.getName(), Class.forName((String) p.getType()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        eventHandler.addEventType("FirstEvent", eventProperties);

        List<PatternStatementSubscriber> subscriberList = patternUtils.preparePatternWithMultipleSubscriber("RuleWithUnregisteredEventType");
        PatternStatementSubscriber subscriber = patternUtils.getSubscriberByAnnotation(subscriberList, STATICWORKLOAD_VIOLATION);
        EventInstance eventInstance = gson.fromJson(EVENT, EventInstance.class);
        eventHandler.handle(eventInstance.getEvent(), eventInstance.getType());
        assertEquals(1, this.violationService.getViolationsFor(subscriber));
    }
}
