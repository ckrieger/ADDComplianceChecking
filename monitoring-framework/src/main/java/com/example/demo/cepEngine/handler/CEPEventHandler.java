package com.example.demo.cepEngine.handler;

import com.example.demo.cepEngine.model.EventType;

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
