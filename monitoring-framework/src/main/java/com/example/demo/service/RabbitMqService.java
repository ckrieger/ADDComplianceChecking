package com.example.demo.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqService {

    private final static String HOST = "localhost";

    private Connection connection;
    private Channel channel;

    public void start(String host, String queueName, DeliverCallback deliverCallback) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    public void stop() throws IOException, TimeoutException {
        connection.close();
    }

}
