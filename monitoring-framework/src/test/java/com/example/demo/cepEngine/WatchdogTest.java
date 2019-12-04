package com.example.demo.cepEngine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.utils.EventTypeUtils;
import com.example.demo.cepEngine.utils.PatternStatementUtils;
import com.example.demo.cepEngine.utils.VirtualMachineEventGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-application-context.xml")
public class WatchdogTest {
    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    private VirtualMachineEventGenerator vmEventGenerator;
    @Autowired
    private EventTypeUtils eventTypeUtils;

    private static final String ACCOUNT_SERVICE_ID = "AccountService";
    private static final String INVENTORY_SERVICE_ID = "InventoryService";
    private static final String SHIPPING_SERVICE_ID = "ShippingService";
    private static final String PATTERN_NAME = "Watchdog";
    private static final String TIME_THRESHOLD = "300";
    private static final Map<String, String> PARAMETERS = new HashMap<String, String>() {{
            put("scalingGroupId", INVENTORY_SERVICE_ID);
            put("timeThreshold", TIME_THRESHOLD);
        }};

    private static PatternStatementSubscriber subscriber;

    @Before
    public void setup() throws IOException {
        eventTypeUtils.addEventTypes();
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        this.subscriber = patternUtils.preparePattern(PATTERN_NAME, PARAMETERS);
    }

    @Test
    public void testWatchdogNoViolationStaticNumber() throws FileNotFoundException, IOException, InterruptedException {
        // generate events of three VMs in the Account Service scaling group
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(ACCOUNT_SERVICE_ID, 3);
        }
        // generate events of two VMs in the Inventory Service scaling group
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void testWatchdogViolationDecreasingNumber() throws Exception {

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 3);
        }
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        Thread.sleep(10000); // temporary fix since the violations are not immediately available at this point

        assertTrue(this.violationService.getViolationsFor(subscriber) >= 1); // warum ist die violation nicht immer 1?
    }

    @Test
    public void shouldDifferentiateBetweenScalingGroups() throws Exception {

        for (int i = 0; i < 3; i++) {
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(ACCOUNT_SERVICE_ID, 3);
        }

        Thread.sleep(500);
        for (int i = 0; i < 3; i++) {
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void testWatchdogNoViolationDecreasingThenIncreasingNumber() throws Exception {

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 3);
        }
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 3);
        }

        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void testWatchdogViolationDoubleDecreasingNumber() throws Exception {

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 3);
        }
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 1);
        }
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        Thread.sleep(10000); // temporary fix since the violations are not immediately available at this point

        assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
    }

    @Test
    public void testWatchdogNoViolationDoubleDecreasingDoubleIncreasingNumber() throws Exception {

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 3);
        }

        Thread.sleep(100);
        vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 1);
        Thread.sleep(100);

        Thread.sleep(50);
        vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        Thread.sleep(10);

        for (int i = 0; i < 5; i++) {
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 3);
            Thread.sleep(100);
        }

        assertTrue(this.violationService.getViolationsFor(subscriber) == 0);
    }
}
