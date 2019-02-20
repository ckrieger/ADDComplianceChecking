package de.iaas.grossmann.cpe.engine;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

//	@Autowired
//	RandomTickEventGenerator generator;
	
	Main() {
		// Load spring config
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "application-context.xml" });
        BeanFactory factory = (BeanFactory) appContext;
        
        // Start Demo
        RandomTickEventGenerator generator = (RandomTickEventGenerator) factory.getBean("eventGenerator");
        
//		AbstractApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
//		RandomTickEventGenerator generator = (RandomTickEventGenerator) context.getBean("eventGenerator");
		
        generator.startDemo(10);
	}
	
	public static void main(String args[]) {
        new Main();
	}
	
}
