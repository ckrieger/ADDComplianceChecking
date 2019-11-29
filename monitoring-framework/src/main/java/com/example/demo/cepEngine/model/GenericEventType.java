package com.example.demo.cepEngine.model;

public class GenericEventType {
    private String type;
    private String[] propertyNames;
    private Object[] propertyTypes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }

    public Object[] getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(Object[] propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
}
