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

    public void generateBigChangingCpuWorkload(double toleratedVariation, int duration, double cpuLevel) {
        long workloadStart = System.currentTimeMillis();
        Random workloadChange = new Random();
        double currentCpu = cpuLevel;
        while(workloadStart + duration > System.currentTimeMillis()) {
            generateCpuHandle(currentCpu);
            double workloadFluctuation = workloadChange.nextDouble() * toleratedVariation;
            currentCpu = setCpuWithVariation(cpuLevel, toleratedVariation + workloadFluctuation);
        }
    }

    public void generateSmallChangingCpuWorkload(double toleratedVariation, int duration, double cpuLevel) {
        long workloadStart = System.currentTimeMillis();
        double currentCpu = cpuLevel;
        Random workloadChange = new Random();
        while(workloadStart + duration > System.currentTimeMillis()) {
            generateCpuHandle(currentCpu);
            double workloadFluctuation = workloadChange.nextDouble() * toleratedVariation;
            currentCpu = setCpuWithVariation(cpuLevel, toleratedVariation - workloadFluctuation - toleratedVariation*0.1);
        }
    }

    public void generateContinuouslyGrowingCpuWorkload(double toleratedVariation, int duration, double cpuLevel) {
        long workloadStart = System.currentTimeMillis();
        double currentCpu = cpuLevel + toleratedVariation;
        Random workloadChange = new Random();
        generateCpuHandle(cpuLevel);
        while(workloadStart + duration > System.currentTimeMillis()) {
            generateCpuHandle(currentCpu);
            double workloadFluctuation = workloadChange.nextDouble() * toleratedVariation;
            currentCpu = setCpuWithVariation(currentCpu, workloadFluctuation);
        }
    }

    private double setCpuWithVariation(double cpuLevel, double workloadChange) {
        return cpuLevel + workloadChange;
    }

    private void generateCpuHandle(double cpu) {
        VirtualMachine nodeEventCpu = new VirtualMachine(cpu);
        eventHandler.handle(nodeEventCpu);
    }
}
