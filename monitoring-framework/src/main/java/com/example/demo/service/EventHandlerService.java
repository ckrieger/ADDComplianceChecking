package com.example.demo.service;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.model.HttpRequestEvent;
import com.example.demo.cepEngine.model.VirtualMachine;
import com.google.gson.Gson;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventHandlerService {

    @Autowired
    private CEPEventHandler eventHandler;

    public DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        Gson g = new Gson();

        // hardcoded checking for message type
        if(message.indexOf("statusCode") > 0){
            HttpRequestEvent httpEvent =  g.fromJson(message, HttpRequestEvent.class);
            eventHandler.handle(httpEvent);
        } else {
            VirtualMachine vmEvent = g.fromJson(message, VirtualMachine.class);
            eventHandler.handle(vmEvent);
        }
    };
}
