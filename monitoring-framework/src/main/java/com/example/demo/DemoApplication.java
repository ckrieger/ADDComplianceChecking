package com.example.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.example.demo.model.MonitoringArea;
import com.example.demo.model.Pattern;
import com.example.demo.model.PatternInstance;
import com.example.demo.model.PatternVariable;
import com.example.demo.repository.MonitoringAreaRepository;
import com.example.demo.repository.PatternInstanceRepository;
import com.example.demo.repository.PatternRepository;
import com.example.demo.repository.PatternVariableRepository;
import com.example.demo.service.PatternConstraintService;
import com.example.demo.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
	@Autowired
    MonitoringAreaRepository monitoringAreaRepository;

	private static final String BASE_PATH = "templates/";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	ApplicationRunner init(){
		return args -> {
			ArrayList<String> queueNames = new ArrayList<>();
			Stream.of("Watchdog").forEach(name -> {
				try {
					addPattern(name);
					PatternInstance patternInstance = createPatternInstance(name);
					MonitoringArea monitoringArea = addMonitoringArea(patternInstance);
                    Set<PatternInstance> patternInstances = new HashSet<>();
                    patternInstances.add(patternInstance);
					patternConstraintService.activatePatternInstances(patternInstances);
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

	private PatternInstance createPatternInstance(String patternName) throws IOException {
		Set<PatternVariable> variables = new HashSet<>();
		PatternVariable timeThreshold = new PatternVariable();
		timeThreshold.setKey("timeThreshold");
		timeThreshold.setValue("1000");
		PatternVariable scalingGroupId = new PatternVariable();
		scalingGroupId.setKey("scalingGroupId");
		scalingGroupId.setValue("inventoryService");

		variables.add(scalingGroupId);
		variables.add(timeThreshold);

		PatternInstance patternInstance = new PatternInstance();
		patternInstance.setName(patternName);
		patternInstance.setConstraintStatement(fetchStatementFromFile(patternName));
		patternInstance.setIsActive(false);
		patternInstance.setIsViolated(false);
		patternInstance.setVariables(variables);

		return patternInstance;
	}

	private MonitoringArea addMonitoringArea(PatternInstance patternInstance) {
	    MonitoringArea monitoringArea = new MonitoringArea();
	    monitoringArea.setName("motivation-scenario");
	    HashSet<PatternInstance> patternInstances = new HashSet<>();
	    patternInstances.add(patternInstance);
	    monitoringArea.setPatternInstances(patternInstances);
	    monitoringAreaRepository.save(monitoringArea);
	    return monitoringArea;
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
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:4200")
						.allowedMethods("*");
			}
		};
	}


}
