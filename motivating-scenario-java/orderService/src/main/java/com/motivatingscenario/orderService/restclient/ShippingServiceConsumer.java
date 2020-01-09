package com.motivatingscenario.orderService.restclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShippingServiceConsumer {
  private RestTemplate restTemplate;

  @Autowired
  public ShippingServiceConsumer(RestTemplateBuilder restTemplateBuilder){
      restTemplate = restTemplateBuilder
            //  .errorHandler(new RestTemplateResponseErrorHandler())
              .build();
  }

  public String get(){
      String fooResourceUrl
              = "http://localhost:8080/spring-rest/foos";
      ResponseEntity<String> response
              = restTemplate.getForEntity(fooResourceUrl + "/1", String.class);
      return response.getBody();
  }

}
