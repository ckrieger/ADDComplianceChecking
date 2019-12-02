package com.example.demo.cepEngine;

import java.util.Map;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.model.EventType;
import com.example.demo.cepEngine.model.VirtualMachine;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class EsperCEPEngine implements InitializingBean, CEPEventHandler, CEPStatementHandler {

    private EPServiceProvider cepProvider;

    public void initService() {
        setupEngine();
    }

    private void setupEngine() {
        Configuration config = setupConfig();
        cepProvider = EPServiceProviderManager.getProvider("myCEPEngine", config);
    }

    private Configuration setupConfig() {
        Configuration config = new Configuration();

        //config.addEventType("VirtualMachine", VirtualMachine.class.getName());
        //config.addEventType("HttpRequestEvent", HttpRequestEvent.class.getName());
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        return config;
    }

    public void addEventType(EventType eventType) {
        cepProvider.getEPAdministrator().getConfiguration().addEventType(eventType.getEventName(), eventType.getClass());
    }

    public void addEventType(String type, String[] propertyNames, Object[] propertyTypes){
        cepProvider.getEPAdministrator().getConfiguration().addEventType(type, propertyNames, propertyTypes);
    }

    public void addEventType(String type, Map<String, Object> eventDefinition){
        cepProvider.getEPAdministrator().getConfiguration().addEventType(type, eventDefinition);
    }

    public void addEplStatement(String statement) {
        System.out.println("added statement: " + statement);
        cepProvider.getEPAdministrator().createEPL(statement);
    }

    public void addStatementSubscriber(PatternStatementSubscriber subscriber) {
        System.out.println("add new Subscriber Statement for pattern " + subscriber.getPatternName());
        System.out.println("Subscriber Statement: " + subscriber.getStatement());
        EPStatement newStatement = cepProvider.getEPAdministrator().createEPL(subscriber.getStatement());
        newStatement.setSubscriber(subscriber);
    }

    public void deleteAllSubscribers() {
        cepProvider.getEPAdministrator().destroyAllStatements();
    }

    public void handle(Object event) {
        cepProvider.getEPRuntime().sendEvent(event);
        System.out.println("handled " + event);
    }

    public void handle(Object[] event, String type){
        cepProvider.getEPRuntime().sendEvent(event, type);
        System.out.println("handled " + event);
    }

    public void handle(Map<String, Object> event, String type){
        cepProvider.getEPRuntime().sendEvent(event, type);
        System.out.println("handled " + event);
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("Configuring...");
        initService();
    }
}
