package com.example.demo.model;

import java.util.Map;

public class EventInstance {
    private String type;
    private Map<String, Object> event;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getEvent() {
        return event;
    }

    public void setEvent(Map<String, Object> event) {
        this.event = event;
    }
}
