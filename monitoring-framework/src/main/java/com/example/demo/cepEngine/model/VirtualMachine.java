package com.example.demo.cepEngine.model;


public class VirtualMachine implements EventType {

    private String vmId;
    private String scalingGroupId;
    private double cpu;


    public VirtualMachine () {

    }

    public VirtualMachine (String id, String scalingGroupId) {
        this.vmId = id;
        this.scalingGroupId = scalingGroupId;
    }
    public String getEventName() {
        return "NodeEvent";
    }

    @Override
    public String toString() {
        return "vmId: " + vmId + " scalingGroupId: " + scalingGroupId + " cpu: " + cpu;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
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
}
