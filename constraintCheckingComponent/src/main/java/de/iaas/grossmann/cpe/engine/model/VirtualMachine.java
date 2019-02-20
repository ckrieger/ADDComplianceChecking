package de.iaas.grossmann.cpe.engine.model;

public class VirtualMachine implements EventType {

	private String id;
	private String scalingGroupId;
	private double cpu;
	private double memory;
	private double storage;

	public VirtualMachine () {

	}

	public VirtualMachine (String id, String scalingGroupId) {
		this.id = id;
		this.scalingGroupId = scalingGroupId;
	}
	public String getEventName() {
		return "NodeEvent";
	}
	
	@Override
	public String toString() {
		return "id: " + id + " scalingGroupId: " + scalingGroupId + " cpu: " + cpu + " memory: " + memory + " storage: " + storage;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getScalingGroupId() {
		return scalingGroupId;
	}

	public void setScalingGroupId(String scalingGroupId) {
		this.scalingGroupId = scalingGroupId;
	}

	public double getCpu() {
		return cpu;
	}
	
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	
	public double getMemory() {
		return memory;
	}
	
	public void setMemory(double memory) {
		this.memory = memory;
	}
	
	public double getStorage() {
		return storage;
	}
	
	public void setStorage(double storage) {
		this.storage = storage;
	}
}
