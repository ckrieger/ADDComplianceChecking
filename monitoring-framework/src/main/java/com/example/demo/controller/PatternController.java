package com.example.demo.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.example.demo.model.InstrumentationTemplate;
import com.example.demo.model.Pattern;
import com.example.demo.repository.PatternRepository;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patterns")
public class PatternController {
    private PatternRepository repository;

    public PatternController(PatternRepository repository){
        this.repository = repository;
    }

    @GetMapping(path = "/")
    public Collection<Pattern> getPatterns(){
        return repository.findAll();
    }

    @GetMapping(path = "/{id}/templates")
    public Collection<InstrumentationTemplate> getTemplatesOfPattern(@PathVariable Long id) {
        //InstrumentationTemplate template = repository.findById(id).get().getPatterns();
        return repository.findById(id).get().getLinkedInstrumentationTemplates();
    }

//    @PutMapping(path = "/")
//    public ResponseEntity<Object> addPattern(@RequestBody Pattern pattern) {
//        if(this.repository.findByName(pattern.getName()).isEmpty()) {
//            this.repository.save(pattern);
//            return ResponseEntity.status(HttpStatus.OK).build();
//        } else {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
//    }

    @PutMapping(path = "/")
    public Pattern addPattern(@RequestBody Pattern pattern) {
        this.repository.deleteByName(pattern.getName());
        return this.repository.save(pattern);
    }

    @PutMapping(path = "/{id}/templates")
    public Pattern updatePatternsOfTemplate(@PathVariable Long id, @RequestBody List<InstrumentationTemplate> templates) {
        Pattern pattern = this.repository.findById(id).get();
        pattern.setLinkedInstrumentationTemplates(templates);
        //InstrumentationTemplate updatedTemplate = this.repository.save(template);
        return repository.save(pattern);
    }

    @DeleteMapping(path = "/{id}")
    public Collection<Pattern> removePattern(@PathVariable Long id) {
        this.repository.deleteById(id);
        return this.repository.findAll();
    }
}
