package com.example.demo.controller;

import java.awt.Event;
import java.util.Collection;
import java.util.List;

import com.example.demo.model.EventType;
import com.example.demo.model.InstrumentationTemplate;
import com.example.demo.model.Pattern;
import com.example.demo.repository.EventTypeRepository;
import com.example.demo.repository.InstrumentationTemplateRepository;
import com.example.demo.repository.PatternRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventTypes")
public class EventTypeController {

    private EventTypeRepository repository;

    public EventTypeController(EventTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/")
    public List<EventType> getEventTypes() {
        return repository.findAll();
    }

    @GetMapping(path = "/{id}")
    public EventType getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));
    }


    @PutMapping(path = "/")
    public EventType addEventType(@RequestBody EventType eventType) {
        return  repository.save(eventType);
    }
}
