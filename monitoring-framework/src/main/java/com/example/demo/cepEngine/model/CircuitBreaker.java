package com.example.demo.cepEngine.model;

public class CircuitBreaker implements EventType {
    private String id;
    private String remoteFunctionCallStatus;



    public CircuitBreaker(String id, String remoteFunctionCallStatus) {
        this.id = id;
        this.remoteFunctionCallStatus = remoteFunctionCallStatus;
    }


    public String getEventName() {
        return "NodeEvent";
    }

    @Override
    public String toString() {
        return "id:" + id + " :remoteFunctionCallStatus " + remoteFunctionCallStatus;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemoteFunctionCallStatus() {
        return remoteFunctionCallStatus;
    }

    public void setRemoteFunctionCallStatus(String remoteFunctionCallStatus) {
        this.remoteFunctionCallStatus = remoteFunctionCallStatus;
    }
}
