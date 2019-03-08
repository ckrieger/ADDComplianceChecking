package de.iaas.grossmann.cpe.monitor;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import com.amazon.sqs.javamessaging.SQSConnection;

import de.iaas.grossmann.cpe.monitor.connection.AWSMessagingConnectionFactory;
import de.iaas.grossmann.cpe.monitor.listener.VMDataListener;

public class AWSEC2Monitor implements ComponentMonitor {

	private AWSMessagingConnectionFactory awsMCF;
	private VMDataListener listener;
	
	private MonitorStatus status = MonitorStatus.INACTIVE;
	private SQSConnection connection;
	private Map<String, Object> properties;
	
	public AWSEC2Monitor(Map<String, Object> properties, AWSMessagingConnectionFactory awsMCF, VMDataListener listener) {
		this.properties = properties;
		this.awsMCF = awsMCF;
		this.listener = listener;
	}
	
	public void setup() {
		if (invalid(properties)) {
			this.setup("VMNodeQueue.fifo", listener);
		} else {
			this.setup((String) properties.get("QUEUE_NAME"), listener);
		}
	}

	private boolean invalid(Map<String, Object> properties) {
		return properties == null || properties.size() == 0 || properties.get("QUEUE_NAME") == null;
	}
	
	/**
	 * public for tests....
	 */
	public void setup(String queueName, MessageListener listener) {
		this.status = MonitorStatus.INACTIVE;
		this.connection = awsMCF.createConnection(queueName);
		System.out.println("created connection: " + connection);
		Session session = awsMCF.createSession(connection);
		System.out.println("created session: " + session);
		MessageConsumer consumer = awsMCF.createMessageConsumer(session, queueName);
		System.out.println("created consumer: " + consumer);
		try {
			if (listener != null) {
				consumer.setMessageListener(listener);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			if (this.connection == null) this.setup();
			this.connection.start();
			this.status = MonitorStatus.ACTIVE;
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			this.connection.stop();
			this.status = MonitorStatus.INACTIVE;
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public MonitorStatus getStatus() {
		return this.status;
	}

}
