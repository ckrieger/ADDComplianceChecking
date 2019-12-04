package com.example.demo.cepEngine;
import java.io.IOException;

public class RemoteServiceTestSimulator {

    private long wait;
    private Boolean shouldFail;

    public RemoteServiceTestSimulator(long wait, Boolean shouldFail) throws InterruptedException {
        this.wait = wait;
        this.shouldFail = shouldFail;
    }

    String execute() throws InterruptedException, IOException {
        System.out.println("Call Executed");
        Thread.sleep(wait);
        if (shouldFail) {
            return "Failed";
        }
        return "Success";
    }
}
