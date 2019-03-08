package de.iaas.grossmann.cpe.monitor;

import static org.junit.Assert.assertNotNull;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;

public class GeneralSQSTest {

	final static Logger logger = LoggerFactory.getLogger(GeneralSQSTest.class);

	private static SQSConnection connection;
	private static Session session;
	private static MessageProducer producer;
	private static MessageConsumer consumer;
	private static SQSConnectionFactory conFactory;
	private static Queue queue;

	private static final String queueName = "VMNodeQueue.fifo";

	// properties file containing the AWS API credentials (access key and secret key)
	private static final String awsCredentialsFile = "src\\main\\resources\\aws.properties";

	@Ignore
	@Test
	public void sendReceiveTest() {
		Message receivedMessage = null;
		try {
			send();
			receivedMessage = receive();
		} finally {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		assertNotNull(receivedMessage);
	}

	@Before
	public void setup() {
		// http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-java-message-service-jms-client.html
		try {
			AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_WEST_2)
					.withCredentials(new PropertiesFileCredentialsProvider(awsCredentialsFile)).build();
			conFactory = new SQSConnectionFactory(new ProviderConfiguration(), sqs);
			// create connection.
			connection = conFactory.createConnection();
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
			// Create the nontransacted session with AUTO_ACKNOWLEDGE mode
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			queue = session.createQueue(queueName);
			producer = session.createProducer(queue);
			consumer = session.createConsumer(queue);
			// Start receiving incoming messages
			connection.start();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void send() {
		try {
			// create and send text message
			TextMessage msg = session.createTextMessage("Hello World!");
			// set property
			msg.setStringProperty("sender", "grossmsn");
			// Set the message group ID
			msg.setStringProperty("JMSXGroupID", "Default");
			msg.setStringProperty("JMS_SQS_DeduplicationId", UUID.randomUUID().toString());
			producer.send(msg);
			// sender.send(msg);
			logger.debug("Sent message: {}", msg);
			logger.info("Sent: {}", msg.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private Message receive() {
		try {
			// receive message
			// Message msg = receiver.receive();
			Message msg = consumer.receive();
			logger.debug("Received message: {}", msg);
			if (msg instanceof TextMessage) {
				logger.info("Received: {}", ((TextMessage) msg).getText());
				@SuppressWarnings("unchecked")
				Enumeration<String> propNames = msg.getPropertyNames();
				while (propNames.hasMoreElements()) {
					String pn = propNames.nextElement();
					logger.info("- {} = {}", pn, msg.getObjectProperty(pn));
				}
			}
			return msg;
			// required when session is set to Session.CLIENT_ACKNOWLEDGE
			// msg.acknowledge();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

}
