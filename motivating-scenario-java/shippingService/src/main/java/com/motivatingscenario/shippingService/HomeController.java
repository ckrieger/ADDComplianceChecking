package com.motivatingscenario.shippingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class HomeController {

    // returns always a 503
    @GetMapping("/shippingInfos")
    public String getShippingInfos() {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "I Always fail");
    }

    @GetMapping("/testRateLimit")
    public ResponseEntity<String> endpointWithRateLimit() {
        return ResponseEntity.ok("Hi from endpoint with rate limit");
    }
}
