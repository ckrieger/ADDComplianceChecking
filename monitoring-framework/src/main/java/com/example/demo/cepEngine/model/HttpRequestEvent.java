package com.example.demo.cepEngine.model;

public class HttpRequestEvent implements EventType {
    private String serviceId;
    private String statusCode;



    public HttpRequestEvent(String serviceId, String statusCode) {
        this.serviceId = serviceId;
        this.statusCode = statusCode;
    }


    public String getEventName() {
        return "HttpRequestEvent";
    }

    @Override
    public String toString() {
        return "serviceId:" + serviceId + " statusCode: " + statusCode;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
