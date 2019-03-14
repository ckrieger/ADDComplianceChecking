package com.example.demo;

import java.util.stream.Stream;

import com.example.demo.model.Pattern;
import com.example.demo.repository.PatternRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	ApplicationRunner init(PatternRepository repository){
		return args -> {
			Stream.of("Watchdog", "Static Workload", "Circuit Breaker").forEach(name -> {
				Pattern pattern = new Pattern();
				pattern.setName(name);
				pattern.setpConstraint("Create a CarRepository class to perform CRUD (create, read, update, and delete) on t");
				repository.save(pattern);
			});
		};
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:4200");
			}
		};
	}

}
