package com.example.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nymvno on 15.03.2019.
 */
public class PatternMapper {

    private Map<String, Object> patternMap;

    public PatternMapper(String patternBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        this.patternMap = objectMapper.readValue(patternBody,
                new TypeReference<Map<String, Object>>(){});
    }

    public String getPatternName() {
        return patternMap.get("name").toString();
    }

    public String getPatternConstraint() {
        return patternMap.get("constraint").toString();
    }
}
