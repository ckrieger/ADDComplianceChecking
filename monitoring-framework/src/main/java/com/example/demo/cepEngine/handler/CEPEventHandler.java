package com.example.demo.cepEngine.handler;

import java.util.Map;

import com.example.demo.cepEngine.model.EventType;

public interface CEPEventHandler {

    /**
     * Handle the incoming Event.
     */
    public void handle(Object event);

    public void handle(Object[] event, String type);

    public void handle(Map<String, Object> event, String type);

    /**
     * Register a new {@link EventType}
     * @param eventType
     */
    public void addEventType(EventType eventType);

    public void addEventType(String type, String[] propertyNames, Object[] propertyTypes);

    public void addEventType(String type, Map<String, Object> eventDefinition);
}
