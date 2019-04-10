package com.example.demo.cepEngine.utils;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.model.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VirtualMachineEventGenerator {
    @Autowired
    private CEPEventHandler eventHandler;
    public void startDemo() throws InterruptedException {
        System.out.println("Start Application Components and scale them to 3 instances per component");
        generateVirtualMachineEventsOfScalingGroup("AccountService", 3);
        generateVirtualMachineEventsOfScalingGroup("InventoryService", 3);
        generateVirtualMachineEventsOfScalingGroup("ShoppingService", 3);
        Thread.sleep(500);
        System.out.println("DecreaseInstances of AccountService");
        generateVirtualMachineEventsOfScalingGroup("AccountService", 2);
        generateVirtualMachineEventsOfScalingGroup("InventoryService", 3);
        generateVirtualMachineEventsOfScalingGroup("ShoppingService", 3);
    }

    public void generateVirtualMachineEventsOfScalingGroup(String scalingGroupId, int amountInstances){
        for (int i = 0; i < amountInstances; i++) {
            VirtualMachine vmEvent = new VirtualMachine(Integer.toString(i), scalingGroupId);
            System.out.println("Sending nodeEvent:" + vmEvent);
            eventHandler.handle(vmEvent);
        }
    }

    public void generateChangingCpuWorkload(double variation, int duration, double cpuLevel) {
        long workloadStart = System.currentTimeMillis();
        Random workloadChange = new Random();
        double currentCpu = cpuLevel;
        while(workloadStart + duration > System.currentTimeMillis()) {
            generateCpuHandle(currentCpu);
            currentCpu = setCpuWithVariation(cpuLevel, variation, workloadChange.nextDouble());
        }
    }

    public void generateStaticCpuWorkload(double variation, int duration, double cpuLevel) {
        long workloadStart = System.currentTimeMillis();
        double currentCpu = cpuLevel;
        Random workloadChange = new Random();
        while(workloadStart + duration > System.currentTimeMillis()) {
            generateCpuHandle(currentCpu);
            currentCpu = setCpuWithVariation(cpuLevel, variation, workloadChange.nextDouble());
        }
    }

    public void generateContinuouslyGrowingCpuWorkload(double variation, int duration, double cpuLevel) {
        long workloadStart = System.currentTimeMillis();
        double currentCpu = cpuLevel + variation;
        Random workloadChange = new Random();
        generateCpuHandle(cpuLevel);
        while(workloadStart + duration > System.currentTimeMillis()) {
            generateCpuHandle(currentCpu);
            currentCpu = setCpuWithVariation(currentCpu, 0.0, workloadChange.nextDouble());
        }
    }

    private double setCpuWithVariation(double cpuLevel, double variation, double workloadChange) {
        return cpuLevel + variation + (workloadChange * 0.05);
    }

    private void generateCpuHandle(double cpu) {
        VirtualMachine nodeEventCpu = new VirtualMachine(cpu);
        eventHandler.handle(nodeEventCpu);
    }
}
