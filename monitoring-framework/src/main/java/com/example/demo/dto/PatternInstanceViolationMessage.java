package com.example.demo.dto;

import com.example.demo.model.PatternInstance;

public class PatternInstanceViolationMessage {

    private PatternInstance patternInstance;
    private String violation;

    public PatternInstanceViolationMessage(PatternInstance pattern, String violation) {
        this.setPatternInstance(pattern);
        this.setViolation(violation);
    }

    public PatternInstance getPatternInstance() {
        return patternInstance;
    }

    public void setPatternInstance(PatternInstance patternInstance) {
        this.patternInstance = patternInstance;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }
}
