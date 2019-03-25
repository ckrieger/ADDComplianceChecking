package com.example.demo.controller;

import java.util.Collection;
import java.util.Iterator;

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
    public ResponseEntity<Object> addPattern(@RequestBody Pattern pattern) {
        this.repository.deleteByName(pattern.getName());
        this.repository.save(pattern);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<Object> removePattern(@RequestBody Pattern pattern) {
        this.repository.deleteByName(pattern.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
