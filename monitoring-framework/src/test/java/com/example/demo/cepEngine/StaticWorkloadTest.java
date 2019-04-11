package com.example.demo.cepEngine;

import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.utils.PatternStatementUtils;
import com.example.demo.cepEngine.utils.VirtualMachineEventGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-application-context.xml"})
public class StaticWorkloadTest {
    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    private VirtualMachineEventGenerator vmGenerator;

    private static PatternStatementSubscriber subscriber;

    private static final String PATTERN_NAME = "StaticWorkload";
    private static final int TEST_DURATION = 100;
    private static final double CPU_LEVEL = 0.5;
    private static final double HIGH_VARIATION = 0.1;
    private static final double SMALL_VARIATION = 0.04;

    @Before
    public void setup() throws IOException {
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        this.subscriber = patternUtils.preparePattern(PATTERN_NAME);
    }

    @Test
    public void testStaticWorkloadViolatedWithBigVariations() {
        vmGenerator.generateChangingCpuWorkload(HIGH_VARIATION, TEST_DURATION, CPU_LEVEL);
        assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
    }

    @Test
    public void testStaticWorkloadCompliantWithSmallVariations() {
        vmGenerator.generateStaticCpuWorkload(SMALL_VARIATION, TEST_DURATION, CPU_LEVEL);
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }

    @Test
    public void testStaticWorkloadViolatedWithContinuouslyGrowingWorkload() {
        vmGenerator.generateContinuouslyGrowingCpuWorkload(HIGH_VARIATION, TEST_DURATION, CPU_LEVEL);
        assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
    }
}
