package de.iaas.grossmann.cpe.monitor.factory;

import java.util.List;
import java.util.Map;

import de.iaas.grossmann.cpe.monitor.ComponentMonitor;

public interface MonitorFactory {

	public String getComponentName();
	
	public List<String> getRequiredProperties();
	
	public ComponentMonitor createInstance(Map<String, Object> properties);
	
}
