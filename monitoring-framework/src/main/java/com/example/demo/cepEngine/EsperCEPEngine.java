package com.example.demo.cepEngine;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.model.CircuitBreaker;
import com.example.demo.cepEngine.model.EventType;
import com.example.demo.cepEngine.model.HttpRequestEvent;
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

        config.addEventType("VirtualMachine", VirtualMachine.class.getName());
        config.addEventType("HttpRequestEvent", HttpRequestEvent.class.getName());
        config.addEventType("CircuitBreaker", CircuitBreaker.class.getName());
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        return config;
    }

    public void addEventType(EventType eventType) {
        cepProvider.getEPAdministrator().getConfiguration().addEventType(eventType.getEventName(), eventType.getClass());
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

    public void afterPropertiesSet() throws Exception {
        System.out.println("Configuring...");
        initService();
    }
}
