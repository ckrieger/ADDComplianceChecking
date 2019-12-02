package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.model.HttpRequestEvent;
import com.example.demo.cepEngine.model.VirtualMachine;
import com.example.demo.model.EventInstance;
import com.example.demo.model.EventType;
import com.example.demo.model.EventTypeProperty;
import com.example.demo.repository.EventTypeRepository;
import com.google.gson.Gson;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventHandlerService {

    private final Logger log = LoggerFactory.getLogger(EventHandlerService.class);

    @Autowired
    private CEPEventHandler eventHandler;
    @Autowired
    private EventTypeRepository eventTypeRepository;
    private Boolean useHardcodedMessageTypes = false;

    public DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        Gson g = new Gson();

        if(!useHardcodedMessageTypes){
            EventInstance eventInstance = g.fromJson(message, EventInstance.class);
            eventHandler.handle(eventInstance.getEvent(), eventInstance.getType());
        } else {
            // hardcoded checking for message type
            if(message.indexOf("statusCode") > 0){
                HttpRequestEvent httpEvent =  g.fromJson(message, HttpRequestEvent.class);
                eventHandler.handle(httpEvent);
            } else {
                VirtualMachine vmEvent = g.fromJson(message, VirtualMachine.class);
                eventHandler.handle(vmEvent);
            }
        }
    };

    public void addAllEventTyes(){
        List<EventType> listOfEventTypes = this.eventTypeRepository.findAll();
        listOfEventTypes.forEach(eventType -> {
            eventHandler.addEventType(eventType.getType(), getEventPropertiesAsMap(eventType.getProperties()));
        });
    }

    private Map<String, Object> getEventPropertiesAsMap(List<EventTypeProperty> eventTypeProperties){
        Map<String, Object> eventProperties = new HashMap<String, Object>();
        eventTypeProperties.forEach(p -> {
            try {
                eventProperties.put(p.getName(), Class.forName((String) p.getType()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return eventProperties;
    }
}
