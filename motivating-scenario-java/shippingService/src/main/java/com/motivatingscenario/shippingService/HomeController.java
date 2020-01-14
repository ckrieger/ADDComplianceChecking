package com.motivatingscenario.shippingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/shippingInfos")
    public String getShippingInfos() {
        return "Shipping Infos";
    }
}
