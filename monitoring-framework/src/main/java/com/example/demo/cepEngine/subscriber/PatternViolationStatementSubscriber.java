package com.example.demo.cepEngine.subscriber;

import java.util.Map;

import com.example.demo.cepEngine.service.StatementViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component(value="violationSubscriber")
@Scope(value="prototype")
@Lazy(value=true)
public class PatternViolationStatementSubscriber implements PatternStatementSubscriber {

    private String statement;
    private String patternName;

    @Autowired
    private StatementViolationService violationService;

    private boolean active = true;
    private Callback callback;

    @Autowired
    public PatternViolationStatementSubscriber(String statement, String patternName) {
        this.statement = statement;
        this.patternName = patternName;
//		violationService.addSubscriber(this);
    }

    @Override
    public String getStatement() {
        return this.statement;
    }

    @Override
    public String getPatternName() {
        return this.patternName;
    }

    @Override
    public void update(Map<String, Object> eventMap) {
        System.out.println("*************UPDATE UPDATE UPDATE*****************");
        if (this.active) {
            System.out.println("*************VIOLATION*****************");
            PatternStatementSubscriber.super.update(eventMap);
            violationService.incrementViolationsFor(this);

            String violationMessage = "";
            for (String key : eventMap.keySet()) {
                violationMessage += key + " = " + eventMap.get(key) + ";\n";
            }

            if (this.callback != null) this.callback.callback(violationMessage);
        }
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Service
    public interface Callback {

        public void callback(String violation);

    }

}
