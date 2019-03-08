package de.iaas.grossmann.cpe.monitor.listener;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.iaas.grossmann.cpe.monitor.data.EventTypeFactory;
import de.iaas.grossmann.cpe.monitor.data.json.SQSAttributeObjectVM;
import de.iaas.grossmann.cpe.monitor.integrator.EventHandlerAccessObject;

@Component
@Scope(value="prototype")
public class VMDataListener implements MessageListener {

	@Autowired
	private EventHandlerAccessObject engineAccess;
	
	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("LOGGING: " + ((TextMessage) message).getText());
			
			ObjectMapper objectMapper = new ObjectMapper();
	        SQSAttributeObjectVM mappedObject = objectMapper.readValue(((TextMessage) message).getText(), SQSAttributeObjectVM.class);
			
	        String vmId = mappedObject.getVmid().getStringValue();
	    	double vmCpu = Double.parseDouble(mappedObject.getVmcpu().getStringValue());
	    	double vmMemory = Double.parseDouble(mappedObject.getVmmemory().getStringValue());
	    	double vmDisk = Double.parseDouble(mappedObject.getVmdisk().getStringValue());
	        
			System.out.println(vmId + " " + vmCpu + " " + vmMemory + " " + vmDisk);
			
			engineAccess.integrateEvent(EventTypeFactory.createNodeEvent(vmId, vmCpu, vmMemory, vmDisk));
			
		} catch (JMSException | IOException e) {
			e.printStackTrace();
		}
	}

}
