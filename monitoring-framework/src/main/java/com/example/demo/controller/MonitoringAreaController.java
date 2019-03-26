package com.example.demo.controller;

import java.util.Collection;

import com.example.demo.model.MonitoringArea;
import com.example.demo.model.Pattern;
import com.example.demo.repository.MonitoringAreaRepository;
import com.example.demo.service.PatternConstraintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    public MonitoringArea startMonitoring(@RequestBody MonitoringArea monitoringArea){
        patternConstraintService.activatePatternInstances(monitoringArea.getPatternInstances());
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
