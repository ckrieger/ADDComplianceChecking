package de.iaas.grossmann.cpe.monitor.connection;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.SQSConnection;

public class RabbitMqMessagingConnectionFactory implements MessagingConnectionFactory {

    @Override
    public MessageProducer createMessageProducer(Session session, String queueName) {
        return null;
    }

    @Override
    public MessageConsumer createMessageConsumer(Session session, String queueName) {
        return null;
    }

    @Override
    public Session createSession(SQSConnection connection) {
        return null;
    }

    @Override
    public TextMessage createTextMessage(Session session, String text) {
        return null;
    }
}
