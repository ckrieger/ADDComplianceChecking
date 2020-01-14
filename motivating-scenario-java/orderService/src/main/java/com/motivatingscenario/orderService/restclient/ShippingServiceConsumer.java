package com.motivatingscenario.orderService.restclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShippingServiceConsumer {
  private RestTemplate restTemplate;

  @Value("${shippingservice.host}")
  private String host;
  @Value("${shippingservice.port}")
  private String port;

  @Autowired
  public ShippingServiceConsumer(RestTemplateBuilder restTemplateBuilder){
      restTemplate = restTemplateBuilder
            //  .errorHandler(new RestTemplateResponseErrorHandler())
              .build();
  }

  public String get(){
      String resourceUrl = "http://" + host + ":" + port +"/api/shippingInfos";
      ResponseEntity<String> response
              = restTemplate.getForEntity(resourceUrl, String.class);
      return response.getBody();
  }

  public String testRateLimit(){
      String resourceUrl = "http://" + host + ":" + port +"/api/testRateLimit";
      ResponseEntity<String> response
              = restTemplate.getForEntity(resourceUrl, String.class);
      return response.getBody();
  }

}
