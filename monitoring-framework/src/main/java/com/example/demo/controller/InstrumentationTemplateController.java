package com.example.demo.controller;

import java.util.Collection;
import java.util.List;

import javax.persistence.Lob;

import com.example.demo.dto.UploadFileResponse;
import com.example.demo.model.DBFile;
import com.example.demo.model.InstrumentationTemplate;
import com.example.demo.model.Pattern;
import com.example.demo.repository.InstrumentationTemplateRepository;
import com.example.demo.repository.PatternRepository;
import com.example.demo.service.DBFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/instrumentationTemplates")
public class InstrumentationTemplateController {
    private InstrumentationTemplateRepository repository;
    private PatternRepository patternRepository;
    @Autowired
    private DBFileStorageService dbFileStorageService;

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

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> removeTemplate(@PathVariable Long id) {
        InstrumentationTemplate template = repository.findById(id).get();
        for (Pattern p : template.getPatterns()) {
            p.getLinkedInstrumentationTemplates().remove(template);
            this.patternRepository.save(p);
        }

        this.repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/uploadFile")
    public UploadFileResponse uploadFileForTemplate(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        InstrumentationTemplate template = repository.findById(id).get();
        DBFile dbFile = dbFileStorageService.storeFile(file);
        template.setFile(dbFile);
        repository.save(template);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(dbFile.getId())
                .toUriString();

        return new UploadFileResponse(dbFile.getId(), dbFile.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
    }
}
