package com.example.demo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.example.demo.model.EventType;
import com.example.demo.model.InstrumentationTemplate;
import com.example.demo.model.MonitoringArea;
import com.example.demo.model.Pattern;
import com.example.demo.repository.EventTypeRepository;
import com.example.demo.repository.InstrumentationTemplateRepository;
import com.example.demo.repository.MonitoringAreaRepository;
import com.example.demo.repository.PatternInstanceRepository;
import com.example.demo.repository.PatternRepository;
import com.example.demo.repository.PatternVariableRepository;
import com.example.demo.service.EventHandlerService;
import com.example.demo.service.PatternConstraintService;
import com.example.demo.service.RabbitMqService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.apache.log4j.helpers.LogLog.warn;

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
	@Autowired
	InstrumentationTemplateRepository instrumentationTemplateRepository;
	@Autowired
	EventTypeRepository eventTypeRepository;
	@Autowired
	EventHandlerService eventHandlerService;

	private static final String BASE_PATH = "templates/";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	ApplicationRunner init(){
		return args -> {
			this.initPatternRepo();
			this.initEventTypeRepo();
			//this.initTemplateRepo();
			MonitoringArea monitoringArea = new Gson().fromJson(fetchStatementFromFile("json/monitoring-area.json"), MonitoringArea.class);
			monitoringArea.getPatternInstances().forEach(patternInstance -> {
				String filePath = patternInstance.getName() + ".epl";
				try {
					patternInstance.setConstraintStatement(fetchStatementFromFile(filePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			this.monitoringAreaRepository.save(monitoringArea);
		};
	}


	private void initPatternRepo() throws IOException {
		Type templateListType = new TypeToken<ArrayList<InstrumentationTemplate>>(){}.getType();
		String templatesJson = fetchStatementFromFile("json/instrumentation-templates.json");
		List<InstrumentationTemplate> templates = new Gson().fromJson(templatesJson, templateListType);

		Stream.of("Watchdog", "CircuitBreaker", "StaticWorkload", "RateLimiting").forEach(name -> {
			Pattern pattern = new Pattern();
			pattern.setName(name);
			String filePath = name + ".epl";
			try {
				pattern.setpConstraint(fetchStatementFromFile(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(name.contains("CircuitBreaker")){
				pattern.setLinkedInstrumentationTemplates(templates);
			}
			patternRepository.save(pattern);
		});
	}

	private void initTemplateRepo() throws IOException {
		Type templateListType = new TypeToken<ArrayList<InstrumentationTemplate>>(){}.getType();
		String templatesJson = fetchStatementFromFile("json/instrumentation-templates.json");
		List<InstrumentationTemplate> templates = new Gson().fromJson(templatesJson, templateListType);
		templates.forEach(template -> {
			instrumentationTemplateRepository.save(template);
			Pattern pattern = patternRepository.findById(1L).get();
			pattern.getLinkedInstrumentationTemplates().add(template);
			patternRepository.save(pattern);
		});
	}

	private void initEventTypeRepo() throws IOException {
		Type eventTypeListType = new TypeToken<ArrayList<EventType>>(){}.getType();
		String eventTypesJson = fetchStatementFromFile("json/event-types.json");
		List<EventType> eventTypes = new Gson().fromJson(eventTypesJson, eventTypeListType);
		eventTypes.forEach(eventType -> eventTypeRepository.save(eventType));
		eventHandlerService.addAllEventTyes();
	}

	private String fetchStatementFromFile(String filename) throws FileNotFoundException, IOException {
		String data = "";
		ClassPathResource cpr = new ClassPathResource(filename);
		try {
			byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			data = new String(bdata, StandardCharsets.UTF_8);
		} catch (IOException e) {
			warn("IOException", e);
		}
		return data;
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
