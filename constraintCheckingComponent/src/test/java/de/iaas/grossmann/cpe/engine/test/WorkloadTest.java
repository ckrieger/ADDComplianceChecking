package de.iaas.grossmann.cpe.engine.test;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iaas.grossmann.cpe.engine.handler.CEPStatementHandler;
import de.iaas.grossmann.cpe.engine.handler.StatementViolationService;
import de.iaas.grossmann.cpe.engine.subscriber.PatternStatementSubscriber;
import de.iaas.grossmann.cpe.engine.test.generator.WorkloadEventGenerator;
import de.iaas.grossmann.cpe.engine.test.util.PatternStatementUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-application-context.xml")
public class WorkloadTest {

	private static final int TEST_EVENT_BREAKTIME = 10;

	private static final int TEST_DURATION = 100;

	@Autowired
	private CEPStatementHandler eventHandler;
	@Autowired
	private StatementViolationService violationService;
	@Autowired
	private WorkloadEventGenerator generator;
	@Autowired
	private PatternStatementUtils patternUtils;

	@Before
	public void setup() {
		eventHandler.deleteAllSubscribers();
		violationService.deleteStatementsAndViolations();
	}
	
	@Test
	public void testStaticWorkloadNotValidWithBigVariations() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("StaticWorkload");
		
		generator.generateStaticCpuWorkload(TEST_DURATION, TEST_EVENT_BREAKTIME, 0.3, 0.2);
		
		assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
	}
	
	@Test
	public void testStaticWorkloadValidWithSmallVariations() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("StaticWorkload");
		
		generator.generateStaticCpuWorkload(TEST_DURATION, TEST_EVENT_BREAKTIME, 0.3, 0.05);
		
		assertTrue(this.violationService.getViolationsFor(subscriber) == 0);
	}
	
	@Test
	public void testStaticWorkloadNotValidWithGrowingWorkload() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("StaticWorkload");
		
		generator.generateContinuouslyGrowingCpuWorkload(TEST_DURATION, TEST_EVENT_BREAKTIME, 0.2, 0.05);
		
		assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
	}

	@Test
	public void testGrowingWorkloadNotValidWithStaticWorkload() throws FileNotFoundException, IOException, InterruptedException {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("GrowingWorkload");
		
		generator.generateStaticCpuWorkload(TEST_DURATION, TEST_EVENT_BREAKTIME, 0.3, 0.05);
		
		assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
	}
	
	@Test
	public void testGrowingWorkloadValidWithGrowingWorkload() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("GrowingWorkload");
		
		generator.generateContinuouslyGrowingCpuWorkload(TEST_DURATION, TEST_EVENT_BREAKTIME, 0.3, 0.01);
		
		assertTrue(this.violationService.getViolationsFor(subscriber) == 0);
	}
	
}
