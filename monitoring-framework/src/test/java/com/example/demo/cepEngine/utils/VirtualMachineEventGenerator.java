package com.example.demo.cepEngine.utils;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.model.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
