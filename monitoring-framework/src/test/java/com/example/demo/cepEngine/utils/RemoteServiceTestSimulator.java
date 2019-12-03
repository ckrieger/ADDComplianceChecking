package com.example.demo.cepEngine.utils;

public class RemoteServiceTestSimulator {

    private long wait;
    private Boolean shouldFail;

    public RemoteServiceTestSimulator(long wait, Boolean shouldFail) throws InterruptedException {
        this.wait = wait;
        this.shouldFail = shouldFail;
    }

    String execute() throws InterruptedException {
        System.out.println("Call Executed");
        Thread.sleep(wait);
        if(shouldFail){
            return "Failed";
        }
        return "Success";
    }

}
