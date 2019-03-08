package de.iaas.grossmann.cpe.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-application-context.xml")
public class MonitorServiceTest {

	@Autowired
	private MonitorService monitorService;
	
	@Test
	public void testIfMonitorsExist() {
		List<String> monitors = monitorService.getMonitorNames();
		
		assertTrue(monitors.size() > 0);
	}
	
	@Test
	public void testAWSMonitorCreation() {
		String monitorName = AWSEC2Monitor.class.getSimpleName();
		
		ComponentMonitor monitor = this.monitorService.createMonitorInstance(monitorName, null);
		
		assertNotNull(monitor);
		assertEquals(MonitorStatus.INACTIVE, monitor.getStatus());
	}
	
}
