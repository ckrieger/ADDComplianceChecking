package com.motivatingscenario.orderService;

import com.motivatingscenario.orderService.restclient.ShippingServiceConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {
    private  static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    ShippingServiceConsumer shippingServiceConsumer;

    @GetMapping("/hello")
    public String hello(){
        shippingServiceConsumer.get();
        return "Hello Shafique!";
    }
}
