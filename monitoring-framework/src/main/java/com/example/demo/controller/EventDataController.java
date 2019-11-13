package com.example.demo.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.example.demo.model.Metric;
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

    @PostMapping(path = "")
    public String eventData(@RequestBody String metrics) throws IOException, TimeoutException {
      System.out.println(metrics);
      return metrics;
    }
}
