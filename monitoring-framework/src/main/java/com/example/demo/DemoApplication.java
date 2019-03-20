package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.example.demo.cepEngine.subscriber.SubscriberFactory;
import com.example.demo.model.Pattern;
import com.example.demo.model.PatternInstance;
import com.example.demo.model.PatternVariable;
import com.example.demo.repository.PatternInstanceRepository;
import com.example.demo.repository.PatternRepository;
import com.example.demo.repository.PatternVariableRepository;
import com.example.demo.service.PatternConstraintService;
import com.example.demo.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ImportResource({"classpath*:application-context.xml"})
public class DemoApplication {
	@Autowired
	RabbitMqService rabbitMqService;
	@Autowired
	PatternRepository patternRepository;
	@Autowired
	PatternInstanceRepository patternInstanceRepository;
	@Autowired
	PatternVariableRepository patternVariableRepository;
	@Autowired
	PatternConstraintService patternConstraintService;

	private static final String BASE_PATH = "templates/";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	ApplicationRunner init(){
		return args -> {
			rabbitMqService.start();
			Stream.of("Watchdog").forEach(name -> {
				try {
					addPattern(name);
					PatternInstance savedPatternInstance = addPatternInstance(name);
					patternConstraintService.activatePattern(savedPatternInstance);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		};
	}

	private void addPattern(String patternName) throws IOException {
		Pattern pattern = new Pattern();
		pattern.setName(patternName);
		pattern.setpConstraint(fetchStatementFromFile(patternName));
		patternRepository.save(pattern);
	}

	private PatternInstance addPatternInstance(String patternName) throws IOException {
		Set<PatternVariable> variables = new HashSet<>();
		PatternVariable patternVariable = new PatternVariable();
		patternVariable.setKey("x");
		patternVariable.setValue("1000");
		PatternVariable pSaved = patternVariableRepository.save(patternVariable);
		variables.add(pSaved);

		PatternInstance patternInstance = new PatternInstance();
		patternInstance.setName(patternName);
		patternInstance.setConstraintStatement(fetchStatementFromFile(patternName));
		patternInstance.setActive(false);
		patternInstance.setViolated(false);
		patternInstance.setVariables(variables);

	   return patternInstanceRepository.save(patternInstance);
	}

	private String fetchStatementFromFile(String filename) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:" + filename + ".epl")));
		StringBuilder statementBuilder = new StringBuilder();
		String next;
		while ((next = br.readLine()) != null) {
			statementBuilder.append(next);
		}
		br.close();
		return statementBuilder.toString();
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
