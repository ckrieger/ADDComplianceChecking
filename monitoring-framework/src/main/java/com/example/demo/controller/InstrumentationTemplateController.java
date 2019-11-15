package com.example.demo.controller;

import java.util.Collection;
import java.util.List;

import com.example.demo.model.InstrumentationTemplate;
import com.example.demo.model.Pattern;
import com.example.demo.repository.InstrumentationTemplateRepository;
import com.example.demo.repository.PatternRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instrumentationTemplates")
public class InstrumentationTemplateController {
    private InstrumentationTemplateRepository repository;
    private PatternRepository patternRepository;

    public InstrumentationTemplateController(InstrumentationTemplateRepository repository, PatternRepository patternRepository) {
        this.repository = repository;
        this.patternRepository = patternRepository;
    }

    @GetMapping(path = "/")
    public Collection<InstrumentationTemplate> getTemplates() {
        return repository.findAll();
    }

    @GetMapping(path = "/{id}")
    public InstrumentationTemplate getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));
    }

    @GetMapping(path = "/{id}/patterns")
    public Collection<Pattern> getPatternsOfTemplate(@PathVariable Long id) {
        //InstrumentationTemplate template = repository.findById(id).get().getPatterns();
        return repository.findById(id).get().getPatterns();
    }

    @PutMapping(path = "/")
    public InstrumentationTemplate addTemplate(@RequestBody InstrumentationTemplate template) {
        return  repository.save(template);
    }

    @PutMapping(path = "/{id}/patterns")
    public ResponseEntity<Object> updatePatternsOfTemplate(@PathVariable Long id, @RequestBody List<Pattern> patterns) {
       InstrumentationTemplate template = this.repository.findById(id).get();
       patterns.forEach(pattern -> {
           Pattern patternToUpdate = this.patternRepository.findById(pattern.getId()).get();
           patternToUpdate.getLinkedInstrumentationTemplates().add(template);
           this.patternRepository.save(patternToUpdate);
       });
       //InstrumentationTemplate updatedTemplate = this.repository.save(template);
       return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<Object> removeTemplate(@RequestBody InstrumentationTemplate template) {
        this.repository.delete(template);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
