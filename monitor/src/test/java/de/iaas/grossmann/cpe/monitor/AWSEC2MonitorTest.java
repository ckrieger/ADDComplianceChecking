package de.iaas.grossmann.cpe.monitor;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazon.sqs.javamessaging.SQSConnection;

import de.iaas.grossmann.cpe.monitor.connection.AWSMessagingConnectionFactory;
import de.iaas.grossmann.cpe.monitor.factory.AWSMonitorFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-application-context.xml")
public class AWSEC2MonitorTest {

	private static final String MESSAGE_DEDUPLICATION_ID_KEY = "JMS_SQS_DeduplicationId";
	private static final String MESSAGE_GROUP_ID_KEY = "JMSXGroupID";
	private static final String MESSAGE_SENDER_KEY = "sender";
	private static final String queueName = "UnitTestQueue.fifo";
	
	@Autowired
	AWSMessagingConnectionFactory awsMCF;
	@Autowired
	AWSMonitorFactory monitorFactory;
	
	AWSEC2Monitor monitor;
	
	private TestDataListener listener;
	
	@Before
	public void setup() throws JMSException {
		System.out.println("Test setup process ...");
		this.monitor = (AWSEC2Monitor) monitorFactory.createInstance(null);
		listener = new TestDataListener();
		monitor.setup(queueName, listener);
		System.out.println("Finished test setup process");
	}
	
	@After
	public void tearDown() {
		System.out.println("Test tear down process ...");
		monitor.stop();
		System.out.println("Finished test tear down process");
	}
	
	@Test
	public void testMessageIntegration() throws Exception {
		monitor.start();
		Message sentMessage = sendTestMessage();

		System.out.println("Start pause for listener ...");
		listener.waitUntilReceived(20000);
        System.out.println( "Returning after pause" );

		Message receivedMessage = listener.getReceivedMessages().get(0);
		
		assertEquals(((TextMessage) sentMessage).getText()
				, ((TextMessage) receivedMessage).getText());
		assertEquals(sentMessage.getStringProperty(MESSAGE_SENDER_KEY),
				receivedMessage.getStringProperty(MESSAGE_SENDER_KEY));
		assertEquals(sentMessage.getStringProperty(MESSAGE_GROUP_ID_KEY),
				receivedMessage.getStringProperty(MESSAGE_GROUP_ID_KEY));
		assertEquals(sentMessage.getStringProperty(MESSAGE_DEDUPLICATION_ID_KEY),
				receivedMessage.getStringProperty(MESSAGE_DEDUPLICATION_ID_KEY));
	}

	private Message sendTestMessage() throws JMSException {
		SQSConnection connection = awsMCF.createConnection(queueName);
		Session session = awsMCF.createSession(connection);
		MessageProducer producer = awsMCF.createMessageProducer(session, queueName);
		TextMessage inputMessage = awsMCF.createTextMessage(session, "Test message text");
		inputMessage.setStringProperty(MESSAGE_SENDER_KEY, "grossmsn");
		inputMessage.setStringProperty(MESSAGE_GROUP_ID_KEY, "Test");
		inputMessage.setStringProperty(MESSAGE_DEDUPLICATION_ID_KEY, UUID.randomUUID().toString());
		connection.start();
		
		producer.send(inputMessage);
		
		return inputMessage;
	}
	
	private class TestDataListener implements MessageListener {

		private List<Message> receivedMessages = new ArrayList<Message>();
		private boolean receivedAMessage = false;
		
		public void waitUntilReceived(long timeout) {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() < start + timeout) {
				try {
					Thread.sleep(100);
					if (receivedAMessage == true) break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void onMessage(Message message) {
			getReceivedMessages().add(message);
			this.receivedAMessage = true;
		}

		public List<Message> getReceivedMessages() {
			return receivedMessages;
		}
		
	}
	
}
