package de.iaas.grossmann.cpe.engine.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.SQSConnection;
import de.iaas.grossmann.cpe.engine.handler.CEPStatementHandler;
import de.iaas.grossmann.cpe.engine.handler.StatementViolationService;
import de.iaas.grossmann.cpe.engine.subscriber.PatternStatementSubscriber;
import de.iaas.grossmann.cpe.engine.test.generator.VirtualMachineEventGenerator;
import de.iaas.grossmann.cpe.engine.test.util.PatternStatementUtils;
import de.iaas.grossmann.cpe.monitor.AWSEC2Monitor;
import de.iaas.grossmann.cpe.monitor.connection.AWSMessagingConnectionFactory;
import de.iaas.grossmann.cpe.monitor.factory.AWSMonitorFactory;
import de.iaas.grossmann.cpe.monitor.listener.VMDataListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("test-application-context.xml")
public class WatchdogIntegrationTest {

    @Autowired
    private CEPStatementHandler statementHandler;
    @Autowired
    private StatementViolationService violationService;
    @Autowired
    private PatternStatementUtils patternUtils;
    @Autowired
    private VirtualMachineEventGenerator vmEventGenerator;

    private VMDataListener vmDataListener;
    AWSEC2Monitor monitor;
    AWSMessagingConnectionFactory awsMCF;

    private static final String QUEUE_NAME = "UnitTestQueue.fifo";


    private static final String ACCOUNT_SERVICE_ID = "AccountService";
    private static final String INVENTORY_SERVICE_ID = "InventoryService";
    private static final String SHIPPING_SERVICE_ID = "ShippingService";
    private static final String PATTERN_NAME = "Watchdog";

    private static PatternStatementSubscriber subscriber;

    @Before
    public void setup() throws IOException {
        this.monitor = (AWSEC2Monitor) new AWSMonitorFactory().createInstance(null);
        monitor.setup(QUEUE_NAME, vmDataListener);
        monitor.start();
        statementHandler.deleteAllSubscribers();
        violationService.deleteStatementsAndViolations();
        this.subscriber = patternUtils.preparePattern(PATTERN_NAME);
    }

    @After
    public void tearDown() {
        System.out.println("Test tear down process ...");
        monitor.stop();
        System.out.println("Finished test tear down process");
    }

    @Test
    public void testWatchdogNoViolationStaticNumber() throws FileNotFoundException, IOException, InterruptedException {
        // generate events of three VMs in the Account Service scaling group
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(ACCOUNT_SERVICE_ID, 3);
        }
        // generate events of two VMs in the Inventory Service scaling group
        for (int i = 0; i < 3; i++) {
            Thread.sleep(100);
            vmEventGenerator.generateVirtualMachineEventsOfScalingGroup(INVENTORY_SERVICE_ID, 2);
        }
        assertEquals(0, this.violationService.getViolationsFor(subscriber));
    }

    private void sendMessage() throws JMSException {
        SQSConnection connection = awsMCF.createConnection(QUEUE_NAME);
        Session session = awsMCF.createSession(connection);
        MessageProducer producer = awsMCF.createMessageProducer(session, QUEUE_NAME);
        String messageText = ""
        TextMessage inputMessage = awsMCF.createTextMessage(session, "Test message text");
//        inputMessage.setStringProperty(MESSAGE_SENDER_KEY, "grossmsn");
//        inputMessage.setStringProperty(MESSAGE_GROUP_ID_KEY, "Test");
//        inputMessage.setStringProperty(MESSAGE_DEDUPLICATION_ID_KEY, UUID.randomUUID().toString());
        connection.start();
        producer.send(inputMessage);
    }

}
