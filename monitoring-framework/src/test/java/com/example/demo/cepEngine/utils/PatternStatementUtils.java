package com.example.demo.cepEngine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternStatementSubscriber;
import com.example.demo.cepEngine.subscriber.SubscriberFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "singleton")
public class PatternStatementUtils {

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private SubscriberFactory subscriberFactory;

    private static final String BASE_PATH = "src/test/resources/";

    public PatternStatementSubscriber createAndPrepareSubscriberFromConstraint(String patternName, String constraint) {
        List<String> eplStatements = Arrays.stream(constraint.toString().split(";")).collect(Collectors.toList());
        PatternStatementSubscriber subscriber = insertStatementsIntoEventHandlerAndCreateSubscriber(patternName, eplStatements);
        return subscriber;
    }

    public PatternStatementSubscriber preparePattern(String patternName) throws FileNotFoundException, IOException {
        String statementBuilder = fetchStatementFromFile(patternName);
        List<String> eplStatements = Arrays.stream(statementBuilder.toString().split(";")).collect(Collectors.toList());
        PatternStatementSubscriber subscriber = insertStatementsIntoEventHandlerAndCreateSubscriber(patternName, eplStatements);
        return subscriber;
    }

    public List<PatternStatementSubscriber> preparePatternWithMultipleSubscriber(String patternName) throws FileNotFoundException, IOException {
        String statementBuilder = fetchStatementFromFile(patternName);
        List<String> eplStatements = Arrays.stream(statementBuilder.toString().split(";")).collect(Collectors.toList());
        List <PatternStatementSubscriber> subscriber = insertStatementsIntoEventHandlerAndCreateSubscriberList(patternName, eplStatements);
        return subscriber;
    }

    private String fetchStatementFromFile(String filename) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(BASE_PATH + filename + ".epl")));
        StringBuilder statementBuilder = new StringBuilder();
        String next;
        while ((next = br.readLine()) != null) {
            statementBuilder.append(next);
        }
        br.close();
        return statementBuilder.toString();
    }

    private PatternStatementSubscriber insertStatementsIntoEventHandlerAndCreateSubscriber(String patternName, List<String> eplStatements) {
        PatternStatementSubscriber subscriber = null;
        for (String statementString: eplStatements) {
            if (statementString.contains("@")) {
//				subscriber = createSubscriber(patternName, statementString);
                subscriber = subscriberFactory.createViolationSubscriber(statementString, patternName);
                violationService.addSubscriber(subscriber);
                statementHandler.addStatementSubscriber(subscriber);
            } else {
                statementHandler.addEplStatement(statementString);
            }
        }
        return subscriber;
    }

    private List<PatternStatementSubscriber> insertStatementsIntoEventHandlerAndCreateSubscriberList(String patternName, List<String> eplStatements) {
        List <PatternStatementSubscriber> subscriberList = new ArrayList<>();
        for (String statementString: eplStatements) {
            if (statementString.contains("@")) {
                PatternStatementSubscriber subscriber = subscriberFactory.createViolationSubscriber(statementString, patternName);
                violationService.addSubscriber(subscriber);
                statementHandler.addStatementSubscriber(subscriber);
                subscriberList.add(subscriber);
            } else {
                statementHandler.addEplStatement(statementString);
            }
        }
        return subscriberList;
    }

    public PatternStatementSubscriber getSubscriberByAnnotation (List<PatternStatementSubscriber> subscriberList, String annotation){
       return subscriberList
               .stream()
               .filter(subscriber -> subscriber.getStatement().contains(annotation))
               .findFirst()
               .get();
    }
}
