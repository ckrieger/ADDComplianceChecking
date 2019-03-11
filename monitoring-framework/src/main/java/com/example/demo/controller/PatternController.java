package com.example.demo.controller;

import java.util.Collection;

import com.example.demo.model.Pattern;
import com.example.demo.repository.PatternRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patterns")
public class PatternController {
    private PatternRepository repository;

    public PatternController(PatternRepository repository){
        this.repository = repository;
    }

    @GetMapping
    public Collection<Pattern> getPatterns(){
        return repository.findAll();
    }
}
