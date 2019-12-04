package com.example.demo.cepEngine.utils;

import java.beans.EventHandler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.model.EventType;
import com.example.demo.model.EventTypeProperty;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import static org.apache.log4j.helpers.LogLog.warn;

@Component
@Scope(value = "singleton")
public class EventTypeUtils {

    @Autowired
    private CEPEventHandler eventHandler;

    public void addEventTypes() throws IOException {
        Type eventTypeListType = new TypeToken<ArrayList<EventType>>(){}.getType();
        String eventTypesJson = fetchStatementFromFile("json/event-types.json");
        List<EventType> eventTypes = new Gson().fromJson(eventTypesJson, eventTypeListType);
        eventTypes.forEach(eventType -> {
            eventHandler.addEventType(eventType.getType(), getEventPropertiesAsMap(eventType.getProperties()));
        });
    }

    private String fetchStatementFromFile(String filename) throws FileNotFoundException, IOException {
        String data = "";
        ClassPathResource cpr = new ClassPathResource(filename);
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            warn("IOException", e);
        }
        return data;
    }

    private Map<String, Object> getEventPropertiesAsMap(List<EventTypeProperty> eventTypeProperties){
        Map<String, Object> eventProperties = new HashMap<String, Object>();
        eventTypeProperties.forEach(p -> {
            try {
                eventProperties.put(p.getName(), Class.forName((String) p.getType()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return eventProperties;
    }

}
