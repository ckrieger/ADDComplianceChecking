package com.example.demo.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.cepEngine.subscriber.PatternViolationStatementSubscriber;
import com.example.demo.cepEngine.subscriber.SubscriberFactory;
import com.example.demo.dto.PatternInstanceViolationMessage;
import com.example.demo.model.PatternInstance;
import com.example.demo.model.PatternVariable;
import com.example.demo.repository.PatternInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
public class PatternConstraintService {

    private final Logger log = LoggerFactory.getLogger(PatternConstraintService.class);

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private SubscriberFactory subscriberFactory;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternInstanceRepository patternInstanceRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Map<PatternInstance, PatternViolationStatementSubscriber> patterns = new HashMap<PatternInstance, PatternViolationStatementSubscriber>();

    public void activatePatternInstances(Set<PatternInstance> patternInstances) {
        Iterator<PatternInstance> itr = patternInstances.iterator();
        while(itr.hasNext()) {
            PatternInstance patternInstance = itr.next();
            log.debug("activating pattern " + patternInstance.getName());
            if (this.findPatternById(patternInstance.getId()) == null) {
                PatternViolationStatementSubscriber subscriber = createAndPrepareSubscriberFromConstraint(patternInstance);
                patternInstance.setIsActive(true);
                subscriber.setActive(true);
                this.patterns.put(patternInstance, subscriber);
            }
        }
    }

    public void deactivatePattern(PatternInstance pattern) {
        log.debug("deactivating pattern " + pattern.getName());
        PatternInstance key = findPatternById(pattern.getId());
        if (key != null) {
            key.setIsActive(false);
            this.patterns.get(key).setActive(false);
        }
    }

    private PatternInstance findPatternById(long id) {
        return this.patterns.keySet().stream().filter(key -> key.getId().equals(id)).findFirst().orElse(null);
    }

    public PatternViolationStatementSubscriber createAndPrepareSubscriberFromConstraint(PatternInstance pattern) {
        String preparedConstraint = prepareConstraint(pattern);
        List<String> eplStatements = Arrays.stream(preparedConstraint.split(";")).collect(Collectors.toList());
        PatternViolationStatementSubscriber subscriber = insertStatementsIntoEventHandlerAndCreateSubscriber(pattern, eplStatements);
        return subscriber;
    }

    public void deleteAllEplStatements() {

        this.statementHandler.deleteAllSubscribers();
        this.patterns = new HashMap<PatternInstance, PatternViolationStatementSubscriber>();
    }

    private String prepareConstraint(PatternInstance pattern) {
        String preparedConstraint = pattern.getConstraintStatement();
        for (PatternVariable variable : pattern.getVariables()) {
            String regex = "\\$\\{" + variable.getKey() + "\\}";
            System.out.println(regex);
            preparedConstraint = preparedConstraint.replaceAll(regex, variable.getValue());
        }
        return preparedConstraint;
    }

    private PatternViolationStatementSubscriber insertStatementsIntoEventHandlerAndCreateSubscriber(PatternInstance pattern, List<String> eplStatements) {
        PatternViolationStatementSubscriber subscriber = null;
        for (String statementString: eplStatements) {
            if (statementString.contains("@")) {
                subscriber = subscriberFactory.createViolationSubscriber(statementString, pattern.getName());
                subscriber.setCallback(createCallback(pattern));
                violationService.addSubscriber(subscriber);
                statementHandler.addStatementSubscriber(subscriber);
            } else {
                statementHandler.addEplStatement(statementString);
            }
        }
        return subscriber;
    }

    private PatternViolationStatementSubscriber.Callback createCallback(PatternInstance pattern) {
        return (violation) -> {
            PatternInstance patternInstance = findPatternById(pattern.getId());
            patternInstance.setIsViolated(true);
            patternInstanceRepository.save(patternInstance);
            messagingTemplate.convertAndSend("/topic/violation", new PatternInstanceViolationMessage(patternInstance, violation));
            log.debug("Set patter " + patternInstance.getName() + " as violated and saved it");
        };
    }


}
