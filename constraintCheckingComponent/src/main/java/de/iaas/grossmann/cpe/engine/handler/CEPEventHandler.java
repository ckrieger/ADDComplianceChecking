package de.iaas.grossmann.cpe.engine.handler;

import de.iaas.grossmann.cpe.engine.model.EventType;

public interface CEPEventHandler {

	/**
     * Handle the incoming Event.
     */
    public void handle(Object event);
    /**
     * Register a new {@link EventType}
     * @param eventType
     */
    public void addEventType(EventType eventType);
    
}
