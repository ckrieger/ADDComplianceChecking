package de.iaas.grossmann.cpe.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class MyConfig {

	@Bean(name="eventGenerator")
    @Description("This is a sample Event Generator")
    public RandomTickEventGenerator eventGenerator() {
        return new RandomTickEventGenerator();
    }
}
