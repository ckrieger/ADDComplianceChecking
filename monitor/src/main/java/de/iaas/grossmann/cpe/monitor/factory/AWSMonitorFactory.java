package de.iaas.grossmann.cpe.monitor.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iaas.grossmann.cpe.monitor.AWSEC2Monitor;
import de.iaas.grossmann.cpe.monitor.ComponentMonitor;
import de.iaas.grossmann.cpe.monitor.connection.AWSMessagingConnectionFactory;
import de.iaas.grossmann.cpe.monitor.listener.VMDataListener;

@Component
public class AWSMonitorFactory implements MonitorFactory {

	@Autowired
	private AWSMessagingConnectionFactory awsMCF;
	@Autowired
	private VMDataListener listener;
	
	@Override
	public String getComponentName() {
		return AWSEC2Monitor.class.getSimpleName();
	}

	@Override
	public ComponentMonitor createInstance(Map<String, Object> properties) {
		return new AWSEC2Monitor(properties, awsMCF, listener);
	}

	@Override
	public List<String> getRequiredProperties() {
		return Arrays.asList("QUEUE_NAME");
	}
	
}