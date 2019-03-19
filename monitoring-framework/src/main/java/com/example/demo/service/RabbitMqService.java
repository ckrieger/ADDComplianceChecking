package com.example.demo.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.example.demo.cepEngine.handler.CEPEventHandler;
import com.example.demo.cepEngine.model.VirtualMachine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqService {

    @Autowired
    private CEPEventHandler eventHandler;


    private final static String QUEUE_NAME = "virtualMachineEvents";
    private final static String HOST = "localhost";

    public void start() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            onMessage(message);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    private void onMessage(String message) {
        System.out.println(" [x] Received '" + message + "'");
        Gson g = new Gson();
        VirtualMachine vmEvent =  g.fromJson(message, VirtualMachine.class);
        eventHandler.handle(vmEvent);
    }
}
