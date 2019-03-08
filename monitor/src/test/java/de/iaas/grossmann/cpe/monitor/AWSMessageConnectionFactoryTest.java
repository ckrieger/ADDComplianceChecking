package de.iaas.grossmann.cpe.monitor;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazon.sqs.javamessaging.SQSConnection;

import de.iaas.grossmann.cpe.monitor.connection.AWSMessagingConnectionFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-application-context.xml")
public class AWSMessageConnectionFactoryTest {

	private static final String MESSAGE_DEDUPLICATION_ID_KEY = "JMS_SQS_DeduplicationId";
	private static final String MESSAGE_GROUP_ID_KEY = "JMSXGroupID";
	private static final String MESSAGE_SENDER_KEY = "sender";
	private static final String queueName = "UnitTestQueue.fifo";

	@Autowired
	AWSMessagingConnectionFactory awsMCF;

	@Test
	public void testSendAndReceiveMessage() throws JMSException {
		SQSConnection connection = awsMCF.createConnection(queueName);
		Session session = awsMCF.createSession(connection);
		MessageProducer producer = awsMCF.createMessageProducer(session, queueName);
		MessageConsumer consumer = awsMCF.createMessageConsumer(session, queueName);
		TextMessage inputMessage = awsMCF.createTextMessage(session, "Test message text");
		inputMessage.setStringProperty(MESSAGE_SENDER_KEY, "grossmsn");
		inputMessage.setStringProperty(MESSAGE_GROUP_ID_KEY, "Test");
		inputMessage.setStringProperty(MESSAGE_DEDUPLICATION_ID_KEY, UUID.randomUUID().toString());
		connection.start();

		producer.send(inputMessage);
		TextMessage outputMessage = (TextMessage) consumer.receive();

		assertEquals(inputMessage.getText(), outputMessage.getText());
		assertEquals(inputMessage.getStringProperty(MESSAGE_SENDER_KEY),
				outputMessage.getStringProperty(MESSAGE_SENDER_KEY));
		assertEquals(inputMessage.getStringProperty(MESSAGE_GROUP_ID_KEY),
				outputMessage.getStringProperty(MESSAGE_GROUP_ID_KEY));
		assertEquals(inputMessage.getStringProperty(MESSAGE_DEDUPLICATION_ID_KEY),
				outputMessage.getStringProperty(MESSAGE_DEDUPLICATION_ID_KEY));
		connection.close();
	}

}
