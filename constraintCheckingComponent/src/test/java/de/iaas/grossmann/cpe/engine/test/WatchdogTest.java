package de.iaas.grossmann.cpe.engine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import de.iaas.grossmann.cpe.engine.handler.CEPEventHandler;
import de.iaas.grossmann.cpe.engine.handler.EsperCEPEngine;
import de.iaas.grossmann.cpe.engine.model.VirtualMachine;
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
public class WatchdogTest {

	@Autowired
	private CEPStatementHandler statementHandler;
	@Autowired
	private StatementViolationService violationService;
	@Autowired
	private WorkloadEventGenerator generator;
	@Autowired
	private PatternStatementUtils patternUtils;
	@Autowired
	private CEPEventHandler eventHandler;
	@Autowired
	private EsperCEPEngine engine;

//	private static final VirtualMachine virtualMachineOneGroupOne = new VirtualMachine(1, 1);
//	private static final VirtualMachine virtualMachineTwoGroupOne = new VirtualMachine(1, );
	@Before
	public void setup() {
		statementHandler.deleteAllSubscribers();
		violationService.deleteStatementsAndViolations();
	}
	@Test
	public void testWatchdogNoViolationStaticNumber() throws FileNotFoundException, IOException {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("Watchdog");

		// generate three VMs in a scaling group
		for (int i = 0; i < 2; i++) {
			eventHandler.handle(new VirtualMachine(Integer.toString(i), "AccountService"));
		}

		assertEquals(0, this.violationService.getViolationsFor(subscriber));
	}

	@Test
	public void testWatchdogViolationDecreasingNumber() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("Watchdog");

		for (int i = 0; i < 5; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(3);
		}
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(2);
		}

		assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
	}

	@Test
	public void shouldDifferntiateBetweenScalingGroups() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("Watchdog");

			for (int i = 0; i < 3; i++) {
				eventHandler.handle(new VirtualMachine(Integer.toString(i), "AccountService"));
			}

			Thread.sleep(500);
			for (int i = 0; i < 2; i++) {
				eventHandler.handle(new VirtualMachine(Integer.toString(i), "InventoryService"));
			}
			assertEquals(0, this.violationService.getViolationsFor(subscriber));
	}

	@Test
	public void testWatchdogNoViolationDecreasingThenIncreasingNumber() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("Watchdog");

		for (int i = 0; i < 5; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(3);
		}
		for (int i = 0; i < 3; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(2);
		}
		for (int i = 0; i < 5; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(3);
		}

		assertEquals(0, this.violationService.getViolationsFor(subscriber));
	}

	@Test
	public void testWatchdogViolationDoubleDecreasingNumber() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("Watchdog");

		for (int i = 0; i < 5; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(3);
		}
		for (int i = 0; i < 3; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(2);
		}
		for (int i = 0; i < 3; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(1);
		}
		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(2);
		}

		assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
	}

	@Test
	public void testWatchdogNoViolationDoubleDecreasingDoubleIncreasingNumber() throws Exception {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("Watchdog");

		for (int i = 0; i < 5; i++) {
			Thread.sleep(100);
			generator.generateRandomCpuWorkload(3);
		}

		Thread.sleep(100);
		generator.generateRandomCpuWorkload(1);
		Thread.sleep(100);

		Thread.sleep(50);
		generator.generateRandomCpuWorkload(2);
		Thread.sleep(10);

		for (int i = 0; i < 5; i++) {
			generator.generateRandomCpuWorkload(3);
			Thread.sleep(100);
		}

		assertTrue(this.violationService.getViolationsFor(subscriber) == 0);
	}

//	@Test
//	public void testStaticWorkloadNotValidWithBigVariations() throws Exception {
//		PatternStatementSubscriber subscriber = patternUtils.preparePattern("StaticWorkload");
//
//		generator.generateStaticCpuWorkload(TEST_DURATION, TEST_EVENT_BREAKTIME, 0.3, 0.2);
//
//		assertTrue(this.violationService.getViolationsFor(subscriber) >= 1);
//	}

}
