package de.iaas.grossmann.cpe.monitor.connection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

@Component
@Scope(value = "singleton")
public class AWSMessagingConnectionFactory implements MessagingConnectionFactory {

	private static final String awsCredentialsFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "aws.properties";

	public MessageProducer createMessageProducer(Session session, String queueName) {
		MessageProducer producer = null;
		try {
			Queue queue = session.createQueue(queueName);
			producer = session.createProducer(queue);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return producer;
	}

	public MessageConsumer createMessageConsumer(Session session, String queueName) {
		MessageConsumer consumer = null;
		try {
			Queue queue = session.createQueue(queueName);
			consumer = session.createConsumer(queue);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return consumer;
	}
	
	public SQSConnection createConnection(String queueName) {
		try {
			AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_WEST_2)
					.withCredentials(new PropertiesFileCredentialsProvider(awsCredentialsFile)).build();
			SQSConnectionFactory conFactory = new SQSConnectionFactory(new ProviderConfiguration(), sqs);
			// create connection.
			SQSConnection connection = conFactory.createConnection();
			// Get the wrapped client
			AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
			// Create an Amazon SQS FIFO queue named MyQueue.fifo, if it doesn't already
			// exist
			if (!client.queueExists(queueName)) {
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put("FifoQueue", "true");
				// attributes.put("ContentBasedDeduplication", "true");
				client.createQueue(new CreateQueueRequest().withQueueName(queueName).withAttributes(attributes));
			}
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Session createSession(SQSConnection connection) {
		Session session = null;
		try {
			// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			connection.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}

	public TextMessage createTextMessage(Session session, String text) {
		try {
			return session.createTextMessage(text);
		} catch (JMSException e) {
			e.printStackTrace();
			return null;
		}
	}
}
