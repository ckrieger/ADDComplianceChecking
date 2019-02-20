package de.iaas.grossmann.cpe.engine.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.iaas.grossmann.cpe.engine.handler.CEPEventHandler;
import de.iaas.grossmann.cpe.engine.handler.CEPStatementHandler;
import de.iaas.grossmann.cpe.engine.handler.StatementViolationService;
import de.iaas.grossmann.cpe.engine.model.CircuitBreaker;
import de.iaas.grossmann.cpe.engine.subscriber.PatternStatementSubscriber;
import de.iaas.grossmann.cpe.engine.test.util.PatternStatementUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-application-context.xml")
public class CircuitBreakerTest {
	
	@Autowired
	private CEPStatementHandler statementHandler;
	@Autowired
	private StatementViolationService violationService;
	@Autowired
	private PatternStatementUtils patternUtils;
	@Autowired
	private CEPEventHandler eventHandler;

	private static final int functionCallFailureThresholdExceeded = 3;
	private static final int circuitBreakerTimeOutInMilliseconds = 100;
	
	private static final  CircuitBreaker circuitBreakerFailureEvent = new CircuitBreaker("1", "failure");
	private static final  CircuitBreaker circuitBreakerSuccessEvent = new CircuitBreaker("1", "succes");
	private static final  CircuitBreaker circuitBreakerRemoteCallBlockedEvent = new CircuitBreaker("1", "blocked");
	  
	private static final CircuitBreaker circuitBreaker2FailureEvent = new CircuitBreaker("2", "failure");
	private static final CircuitBreaker circuitBreaker2SuccessEvent = new CircuitBreaker("2", "success");
	private static final CircuitBreaker circuitBreaker2RemoteCallBlockedEvent = new CircuitBreaker("2", "blocked");
	
	@Before
	public void setup() {
		statementHandler.deleteAllSubscribers();
		violationService.deleteStatementsAndViolations();
	}

	@Test
	public void shouldThrowViolationIfCircuitBreakerDoesNotTripAfterThresholdExceededAndSleepPeriodIsViolated() throws FileNotFoundException, IOException, InterruptedException {
	  PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker");
	  // exceed threshold
	  for (int i = 0; i < functionCallFailureThresholdExceeded; i++) {
		  eventHandler.handle(circuitBreakerFailureEvent);
	  }
	  assertEquals(0, this.violationService.getViolationsFor(subscriber));
	  // violate timeout
	  eventHandler.handle(circuitBreakerSuccessEvent);
	  assertEquals(1, this.violationService.getViolationsFor(subscriber));
	}
	
	@Test
	public void shouldOnlyThrowViolationIfFailuresAreConsecutiveAndSleepPeriodIsViolated() throws FileNotFoundException, IOException, InterruptedException {
	  PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker");
	  
	  // failures are not consecutive -> threshold not exceeded
	  eventHandler.handle(circuitBreakerFailureEvent);
	  eventHandler.handle(circuitBreakerSuccessEvent);
	  eventHandler.handle(circuitBreakerFailureEvent);
	  eventHandler.handle(circuitBreakerFailureEvent);
	  eventHandler.handle(circuitBreakerSuccessEvent);
	  eventHandler.handle(circuitBreakerFailureEvent);
	  eventHandler.handle(circuitBreakerFailureEvent);
	  assertEquals(0, this.violationService.getViolationsFor(subscriber));
	  
	  // add another failure event -> threshold exceeded but timeout not violated
	  eventHandler.handle(circuitBreakerFailureEvent);
	  assertEquals(0, this.violationService.getViolationsFor(subscriber));
	  
	  // violate timeout
	  eventHandler.handle(circuitBreakerSuccessEvent);
	  assertEquals(1, this.violationService.getViolationsFor(subscriber));
	}
	
	@Test
	public void differentiateByCircuitBreakerId() throws FileNotFoundException, IOException, InterruptedException {
	  PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker");
	
	  // no threshold exceeded as events from different Circuit Breaker
	  eventHandler.handle(circuitBreakerFailureEvent);
	  eventHandler.handle(circuitBreakerFailureEvent);
	  eventHandler.handle(circuitBreaker2FailureEvent);
	  eventHandler.handle(circuitBreaker2FailureEvent);
	  
	  // third consecutive failure of Circuit Breaker 1 -> it should trip
	  eventHandler.handle(circuitBreakerFailureEvent);
	  assertEquals(0, this.violationService.getViolationsFor(subscriber));
	  
	  // violate timeout
	  eventHandler.handle(circuitBreakerSuccessEvent);
	  assertEquals(1, this.violationService.getViolationsFor(subscriber));
	}
	
	@Test 
	public void shouldNotThrowVioilationIfTimeoutIsAdhered() throws FileNotFoundException, IOException, InterruptedException {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker");
		// exceed threshold of consecutive failures
		 for (int i = 0; i < functionCallFailureThresholdExceeded; i++) {
			  eventHandler.handle(circuitBreakerFailureEvent);
		  }
		 
		 // adhere to timeout
		 TimeUnit.MILLISECONDS.sleep(circuitBreakerTimeOutInMilliseconds);
		 eventHandler.handle(circuitBreakerSuccessEvent);
		 eventHandler.handle(circuitBreakerFailureEvent);
		 eventHandler.handle(circuitBreakerFailureEvent);
		 eventHandler.handle(circuitBreakerFailureEvent);
		 assertEquals(0, this.violationService.getViolationsFor(subscriber)); // @todo fails on every third run
	}
	
	@Test
	public void shouldThrowViolationIfCircuitBreakerDoesNotCloseAfterTimeout() throws FileNotFoundException, IOException, InterruptedException {
		PatternStatementSubscriber subscriber = patternUtils.preparePattern("CircuitBreaker");
		// exceed threshold of consecutive failures
		 for (int i = 0; i < functionCallFailureThresholdExceeded; i++) {
			  eventHandler.handle(circuitBreakerFailureEvent);
		  }
		 
		 // adhere to timeout
		 TimeUnit.MILLISECONDS.sleep(circuitBreakerTimeOutInMilliseconds);
		 eventHandler.handle(circuitBreakerRemoteCallBlockedEvent);
		 assertEquals(1, this.violationService.getViolationsFor(subscriber));
	}

}
