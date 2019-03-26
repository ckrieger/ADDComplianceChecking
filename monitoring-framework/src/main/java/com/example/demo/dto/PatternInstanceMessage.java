package com.example.demo.dto;

import com.example.demo.model.PatternInstance;

public class PatternInstanceMessage {

    private PatternInstance pattern;
    private String violation;

    public PatternInstanceMessage(PatternInstance pattern, String violation) {
        this.setPattern(pattern);
        this.setViolation(violation);
    }

    public PatternInstance getPattern() {
        return pattern;
    }

    public void setPattern(PatternInstance pattern) {
        this.pattern = pattern;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }
}
