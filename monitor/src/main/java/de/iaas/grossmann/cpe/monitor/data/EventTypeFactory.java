package de.iaas.grossmann.cpe.monitor.data;

import de.iaas.grossmann.cpe.engine.model.VirtualMachine;

public class EventTypeFactory {

	public static VirtualMachine createNodeEvent(String id, double cpu, double memory, double storage) {
		VirtualMachine nodeEvent = new VirtualMachine();
		nodeEvent.setId(id);
		nodeEvent.setCpu(cpu);
		nodeEvent.setMemory(memory);
		nodeEvent.setStorage(storage);
		return nodeEvent;
	}
	
}
