package com.motivatingscenario.shippingService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_LIMIT;
import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_REMAINING;
import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_RESET;
import static javafx.scene.control.ButtonType.OK;
import static org.junit.Assert.assertEquals;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ZuulRateLimitTest {

    private static final String ENDPOINT = "/api/testRateLimit";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenRequestNotExceedingCapacity_thenReturnOkResponse() {
        ResponseEntity<String> response = restTemplate.getForEntity(ENDPOINT, String.class);
        HttpHeaders headers = response.getHeaders();
        String key = "rate-limit-application_serviceTestRateLimit_127.0.0.1";

        String limit = headers.getFirst(HEADER_LIMIT + key);
        String remaining = headers.getFirst(HEADER_REMAINING + key);
        String reset = headers.getFirst(HEADER_RESET + key);

        assertEquals(limit, "5");
        assertEquals(remaining, "4");
        assertEquals(reset, "60000");

        assertEquals(OK, response.getStatusCode());
    }

}
