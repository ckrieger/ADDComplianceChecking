package com.example.demo.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.example.demo.model.Pattern;
import com.example.demo.repository.PatternRepository;
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

        // Map patternBody in JSON format to object map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(patternBody,
                new TypeReference<Map<String, Object>>(){});

        Pattern pattern = new Pattern();
        pattern.setName(jsonMap.get("name").toString());
        pattern.setpConstraint(jsonMap.get("constraint").toString());

        // Delete Pattern by name, if it exists in Repository
        for(Iterator<Pattern> it = this.repository.findAll().iterator(); it.hasNext();) {
            Pattern refPattern = it.next();
            if(refPattern.getName().equals(pattern.getName())) {
                this.repository.delete(refPattern);
            }
        }
        this.repository.save(pattern);
    }

}
