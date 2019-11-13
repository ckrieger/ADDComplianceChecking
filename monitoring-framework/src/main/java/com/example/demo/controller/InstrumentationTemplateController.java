package com.example.demo.controller;

import java.util.Collection;

import com.example.demo.model.InstrumentationTemplate;
import com.example.demo.repository.InstrumentationTemplateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instrumentationTemplates")
public class InstrumentationTemplateController {
    private InstrumentationTemplateRepository repository;

    public  InstrumentationTemplateController(InstrumentationTemplateRepository repository) {this.repository = repository;}

    @GetMapping(path = "/")
    public Collection<InstrumentationTemplate> getTemplates(){
        return repository.findAll();
    }

    @PutMapping(path = "/")
    public ResponseEntity<Object> addPattern(@RequestBody InstrumentationTemplate template) {
        this.repository.save(template);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<Object> removePattern(@RequestBody InstrumentationTemplate template) {
        this.repository.delete(template);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
