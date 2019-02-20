package de.iaas.grossmann.cpe.engine.test.generator;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.iaas.grossmann.cpe.engine.handler.CEPEventHandler;
import de.iaas.grossmann.cpe.engine.model.VirtualMachine;

@Component
public class WorkloadEventGenerator {

    @Autowired
    private CEPEventHandler eventHandler;

    public void startDemo(int numberOfEvents) {
    	for (int i = 0; i < numberOfEvents; i++) generateRandomCpuWorkload();
    }

    public void generateRandomCpuWorkload() {
    	Random generator = new Random();
		double cpu = (double) generator.nextInt(100) / 100;
		VirtualMachine nodeEvent = createNodeEventCpu(cpu);
		System.out.println("Sending nodeEvent:" + nodeEvent);
		eventHandler.handle(nodeEvent);
	}
    
    public void generateRandomCpuWorkload(int amountInstances) {
    	for (int i = 0; i < amountInstances; i++) {
    		Random generator = new Random();
    		double cpu = (double) generator.nextInt(100) / 100;
    		VirtualMachine nodeEvent = createNodeEventCpu(cpu, "testID" + i);
    		System.out.println("Sending nodeEvent:" + nodeEvent);
    		eventHandler.handle(nodeEvent);
    	}
	}

	private VirtualMachine createNodeEventCpu(double cpu) {
		return this.createNodeEventCpu(cpu, "testID");
	}
	
	private VirtualMachine createNodeEventCpu(double cpu, String id) {
		VirtualMachine nodeEvent = new VirtualMachine();
		nodeEvent.setCpu(cpu);
		nodeEvent.setId(id);
		nodeEvent.setMemory(1.0 - cpu);
		nodeEvent.setStorage(cpu * 100.0);
		return nodeEvent;
	}
    
    public void generateStaticCpuWorkload(long duration, long breaktime, double level, double variation) throws InterruptedException {
    	long now = System.currentTimeMillis();
    	Random generator = new Random();
    	double cpu = level;
    	while (now + duration > System.currentTimeMillis()) {
    		VirtualMachine nodeEventCpu = createNodeEventCpu(cpu);
    		System.out.println("Sending nodeEvent:" + nodeEventCpu);
			eventHandler.handle(nodeEventCpu);
			Thread.sleep(breaktime);
			cpu = setCpuPlusMinusVariation(level, variation, generator);
    	}
    }
    
    public void generateContinuouslyGrowingCpuWorkload(long duration, long breaktime, double startLevel, double growthrate) throws InterruptedException {
    	long now = System.currentTimeMillis();
    	double cpu = startLevel;
    	while (now + duration > System.currentTimeMillis()) {
    		VirtualMachine nodeEventCpu = createNodeEventCpu(cpu);
    		System.out.println("Sending nodeEvent:" + nodeEventCpu);
			eventHandler.handle(nodeEventCpu);
			Thread.sleep(breaktime);
			cpu = cpu + growthrate;
    	}
    }

	private double setCpuPlusMinusVariation(double level, double variation, Random generator) {
		return level - variation  + generator.nextDouble() * variation * 2;
	}
	
}
