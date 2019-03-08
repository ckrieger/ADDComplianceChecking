package de.iaas.grossmann.cpe.monitor.integrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iaas.grossmann.cpe.engine.handler.CEPEventHandler;
import de.iaas.grossmann.cpe.engine.model.EventType;

@Component
public class EventHandlerAccessObject {

	@Autowired
	private CEPEventHandler eventHandler;
	
	public void integrateEvent(Object event) {
		eventHandler.handle(event);
	}

	public void integrateEventType(EventType eventType) {
		eventHandler.addEventType(eventType);
	}

}
