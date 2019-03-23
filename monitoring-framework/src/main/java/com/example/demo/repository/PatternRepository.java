package com.example.demo.repository;

import com.example.demo.model.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface PatternRepository extends JpaRepository<Pattern, Long> {

    List<Pattern> findByName(String name);

    @Transactional
    Long deleteByName(String name);

}
