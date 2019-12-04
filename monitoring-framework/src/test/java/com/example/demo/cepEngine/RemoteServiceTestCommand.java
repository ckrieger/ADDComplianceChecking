package com.example.demo.cepEngine;

import java.beans.EventHandler;

import com.example.demo.cepEngine.RemoteServiceTestSimulator;
import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.utils.EventTypeUtils;
import com.example.demo.model.EventInstance;
import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-application-context.xml"})
public class RemoteServiceTestCommand extends HystrixCommand<String> {
    private RemoteServiceTestSimulator remoteService;
    private CEPEventHandler eventHandler;

    private static final String EVENTTYPE = "HttpRequestEvent";
    private static final String FAILEDHTTPREQUESTEVENT = "{'type': 'HttpRequestEvent', 'event' : {'serviceId': '1', 'statusCode': 'failed'}}";
    private static final String SUCCEDEDHTTPREQUESTEVENT = "{'serviceId': '1', 'statusCode': 'success'}";

    public RemoteServiceTestCommand(Setter config, RemoteServiceTestSimulator remoteService, CEPEventHandler eventHandler) {
        super(config);
        this.remoteService = remoteService;
        this.eventHandler = eventHandler;
    }

    @Override
    protected String run() throws Exception {
        Gson g = new Gson();
        String response = remoteService.execute();
        if(response.equals("Failed")){
            EventInstance eventInstance = g.fromJson(FAILEDHTTPREQUESTEVENT, EventInstance.class);
            eventHandler.handle(eventInstance.getEvent(), eventInstance.getType());
            throw new RuntimeException("Unexpected error retrieving todays races");
        }else {
            EventInstance eventInstance = g.fromJson(SUCCEDEDHTTPREQUESTEVENT, EventInstance.class);
            eventHandler.handle(eventInstance.getEvent(), eventInstance.getType());
            return response;
        }
    }

    @Override
    protected String getFallback() {
        System.out.println("Fallback called");
        return null;
    }

}
