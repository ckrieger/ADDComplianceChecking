package com.example.demo.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import com.example.demo.cepEngine.handler.CEPStatementHandler;
import com.example.demo.cepEngine.service.StatementViolationService;
import com.example.demo.model.MonitoringArea;
import com.example.demo.repository.MonitoringAreaRepository;
import com.example.demo.service.EventHandlerService;
import com.example.demo.service.PatternConstraintService;
import com.example.demo.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitoring-areas")
public class MonitoringAreaController {

    private MonitoringAreaRepository repository;

    @Autowired
    PatternConstraintService patternConstraintService;
    @Autowired
    RabbitMqService rabbitMqService;
    @Autowired
    EventHandlerService eventHandlerService;
    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;

    public MonitoringAreaController(MonitoringAreaRepository repository){
        this.repository = repository;
    }

    @GetMapping(path = "/")
    public Collection<MonitoringArea> getMonitoringAreas(){
        return repository.findAll();
    }

    @PutMapping(path= "/{id}")
    public MonitoringArea updateMonitoringArea(@RequestBody MonitoringArea monitoringArea, @PathVariable Long id){
        return this.repository.save(monitoringArea);
    }

    @GetMapping(path = "/{id}")
    public MonitoringArea getMonitoringAreaById(@PathVariable Long id){
        return repository.findById(id).get();
    }

    @PostMapping(path= "/start")
    public MonitoringArea startMonitoring(@RequestBody MonitoringArea monitoringArea) throws IOException, TimeoutException {
        patternConstraintService.activatePatternInstances(monitoringArea.getPatternInstances());
        rabbitMqService.start(monitoringArea.getQueueHost(), monitoringArea.getQueueName(), eventHandlerService.deliverCallback);
        monitoringArea.setIsActive(true);
        return this.repository.save(monitoringArea);
    }

    @PostMapping(path="/stop")
    public MonitoringArea stopMonitoring(@RequestBody MonitoringArea monitoringArea) throws IOException, TimeoutException {
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        rabbitMqService.stop();
        monitoringArea.getPatternInstances().forEach(patternInstance -> {
            patternInstance.setIsViolated(false);
        });
        monitoringArea.setIsActive(false);
        return this.repository.save(monitoringArea);
    }

//    @PostMapping(path= "/")
//    public MonitoringArea addMonitoringArea(@RequestBody MonitoringArea monitoringArea){
//        return this.repository.save(monitoringArea);
//    }

//    @PostMapping(path="/{id}/addPatternInstance")
//    public MonitoringArea addPatternInstanceToMonitoringArea(@RequestBody Pattern pattern){
//
//    }

}
