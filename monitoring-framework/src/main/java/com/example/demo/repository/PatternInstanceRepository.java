package com.example.demo.repository;

import com.example.demo.model.PatternInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatternInstanceRepository extends JpaRepository<PatternInstance, Long> {
}
