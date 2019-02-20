package de.iaas.grossmann.cpe.engine.test.generator;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iaas.grossmann.cpe.engine.handler.CEPEventHandler;
import de.iaas.grossmann.cpe.engine.model.HttpEvent;
import de.iaas.grossmann.cpe.engine.model.VirtualMachine;

@Component
public class HttpEventGenerator {
	
	 @Autowired
	 private CEPEventHandler eventHandler;
	 
	 public void generateHttpRequestSuccessReports(String source, String target, int amountHttpRequests) throws InterruptedException {
		 for (int i = 0; i < amountHttpRequests; i++) {
	    		HttpEvent nodeEvent = new HttpEvent(i, source, target, 0, 1, System.currentTimeMillis());
	    		System.out.println("Sending nodeEvent:" + nodeEvent);
	    		eventHandler.handle(nodeEvent);
	    	}
	 }
	 
	 public void generateHttpRequestFailureReports(String source, String target, int amountHttpRequests) throws InterruptedException {
		 for (int i = 0; i < amountHttpRequests; i++) {
	    		HttpEvent nodeEvent = new HttpEvent(i, source, target, 1, 0, System.currentTimeMillis());
	    		System.out.println("Sending nodeEvent:" + nodeEvent);
	    		eventHandler.handle(nodeEvent);
	    	}
	 }
	 

}
