package de.iaas.grossmann.cpe.monitor.connection;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.SQSConnection;

public interface MessagingConnectionFactory {

	public MessageProducer createMessageProducer(Session session, String queueName);
	public MessageConsumer createMessageConsumer(Session session, String queueName);
	public Session createSession(SQSConnection connection);
	public TextMessage createTextMessage(Session session, String text);
}
