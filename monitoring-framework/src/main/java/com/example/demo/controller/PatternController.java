package com.example.demo.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.example.demo.model.Pattern;
import com.example.demo.repository.PatternRepository;
import com.example.demo.util.PatternMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patterns")
public class PatternController {
    private PatternRepository repository;

    public PatternController(PatternRepository repository){
        this.repository = repository;
    }

    @GetMapping(path = "/get")
    public Collection<Pattern> getPatterns(){
        return repository.findAll();
    }

    @PostMapping(path = "/add", consumes = "text/plain")
    public void addPattern(@RequestBody String patternBody) throws IOException {
        PatternMapper pm = new PatternMapper(patternBody);

        Pattern pattern = new Pattern(
                pm.getPatternName(),
                pm.getPatternConstraint()
        );

        // Delete Pattern by name, if it exists in repository
        for(Iterator<Pattern> it = this.repository.findAll().iterator(); it.hasNext();) {
            Pattern refPattern = it.next();
            if(refPattern.getName().equals(pattern.getName())) {
                this.repository.delete(refPattern);
            }
        }
        this.repository.save(pattern);
    }

    @PostMapping(path = "/remove", consumes = "text/plain")
    public void removePattern(@RequestBody String patternBody) throws IOException {
        PatternMapper pm = new PatternMapper(patternBody);

        // Delete Pattern by name, if it exists in repository
        for(Iterator<Pattern> it = this.repository.findAll().iterator(); it.hasNext();) {
            Pattern refPattern = it.next();
            if(refPattern.getName().equals(pm.getPatternName())) {
                this.repository.delete(refPattern);
            }
        }
    }
}
