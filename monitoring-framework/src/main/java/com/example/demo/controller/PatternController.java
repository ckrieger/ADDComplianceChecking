package com.example.demo.controller;

import java.util.Collection;
import java.util.Iterator;

import com.example.demo.model.Pattern;
import com.example.demo.repository.PatternRepository;
import org.springframework.http.MediaType;
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

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addPattern(@RequestBody Pattern pattern) {
        deleteFromRepository(pattern, this.repository);
        this.repository.save(pattern);
    }

    @PostMapping(path = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removePattern(@RequestBody Pattern pattern) {
        deleteFromRepository(pattern, this.repository);
    }

    /**
     * Deletes existing pattern by name from passed repository.
     * Maybe find a better place for this method...
     * @param pattern Pattern that needs to be deleted from repository if existing
     * @param repository Repository from which pattern will be deleted
     */
    private void deleteFromRepository(Pattern pattern, PatternRepository repository) {
        for(Iterator<Pattern> it = repository.findAll().iterator(); it.hasNext();) {
            Pattern refPattern = it.next();
            if(refPattern.getName().equals(pattern.getName())) {
                repository.delete(refPattern);
            }
        }
    }
}
