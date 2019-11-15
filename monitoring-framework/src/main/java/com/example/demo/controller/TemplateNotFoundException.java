package com.example.demo.controller;

class TemplateNotFoundException extends RuntimeException{
    TemplateNotFoundException(Long id){
        super("Could not find Instrumentation Template" + id);
    }
}
