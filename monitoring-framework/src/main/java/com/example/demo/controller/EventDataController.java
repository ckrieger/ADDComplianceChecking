package com.example.demo.controller;

import java.beans.EventHandler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.model.EventInstance;
import com.example.demo.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventData")
public class EventDataController {
    @Autowired
    CEPEventHandler eventHandler;

    @PostMapping(path = "")
    public EventInstance eventData(@RequestBody EventInstance eventInstance) throws IOException, TimeoutException {
      eventHandler.handle(eventInstance.getEvent(), eventInstance.getType());
      return eventInstance;
    }
}
