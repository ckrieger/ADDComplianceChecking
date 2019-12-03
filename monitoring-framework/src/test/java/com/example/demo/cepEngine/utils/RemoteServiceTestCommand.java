package com.example.demo.cepEngine.utils;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class RemoteServiceTestCommand extends HystrixCommand<String> {
    private RemoteServiceTestSimulator remoteService;

    public RemoteServiceTestCommand(Setter config, RemoteServiceTestSimulator remoteService) {
        super(config);
        this.remoteService = remoteService;
    }

    @Override
    protected String run() throws Exception {
        String response = remoteService.execute();
        if(response.equals("Failed")){
            throw new RuntimeException("Unexpected error retrieving todays races");
        }
        return response;
    }

    @Override
    protected String getFallback() {
        System.out.println("Fallback called");
        return null;
    }

}
