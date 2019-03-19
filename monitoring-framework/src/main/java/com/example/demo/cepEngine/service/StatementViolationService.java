package com.example.demo.cepEngine.service;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class StatementViolationService {

    private Map<PatternStatementSubscriber, Integer> constraintViolations = new HashMap<>();

    public void addSubscriber(PatternStatementSubscriber subscriber) {
        this.constraintViolations.put(subscriber, 0);
    }

    public void setSubscriber(PatternStatementSubscriber subscriber, int value) {
        this.constraintViolations.put(subscriber, value);
    }

    public void incrementViolationsFor(PatternStatementSubscriber subscriber) {
        this.setSubscriber(subscriber, this.getViolationsFor(subscriber) + 1);
    }

    public int getViolationsFor(PatternStatementSubscriber subscriber) {
        return this.constraintViolations.get(subscriber).intValue();
    }

    public void deleteStatementsAndViolations() {
        constraintViolations = new HashMap<>();
    }
}
