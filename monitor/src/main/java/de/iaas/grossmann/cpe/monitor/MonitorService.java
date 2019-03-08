package de.iaas.grossmann.cpe.monitor;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.iaas.grossmann.cpe.monitor.factory.MonitorFactory;

@Component
@Scope(value = "singleton")
public class MonitorService {
	
	@Autowired
	private List<MonitorFactory> monitorFactories;

	public ComponentMonitor createMonitorInstance(String typeName, Map<String, Object> properties) throws NoSuchElementException {
		MonitorFactory factory = this.monitorFactories.stream().filter(f -> f.getComponentName().equals(typeName)).findFirst().get();
		return factory.createInstance(properties);
	}
	
	public List<String> getMonitorNames() {
		return this.monitorFactories.stream().map(mf -> mf.getComponentName()).collect(Collectors.toList());
	}
	
	public List<MonitorFactory> getMonitorFactories() {
		return this.monitorFactories;
	}
	
}
