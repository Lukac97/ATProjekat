package messagemanager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;

@Singleton
@LocalBean
public class JMSFactory {
	private Connection connection;
	
	@Resource(lookup = "java:jboss/exported/jms/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@Resource(lookup = "java:jboss/exported/jms/queue/mojQueue")
	private Queue defaultQueue;
	
	@PostConstruct
	public void postConstruct() {
		try {
			connection = connectionFactory.createConnection("guest", "guest.guest.1");
			connection.setClientID("JAR2020");
			connection.start();
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			connection.close();
		} catch (JMSException ex) {
			System.out.println("Exception while closing the JMS connection.");
		}
	}
	
	public Session getSession() {
		try {
			return connection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	public MessageProducer getDefaultProducer(Session session) {
		try {
			return session.createProducer(defaultQueue);
		} catch (JMSException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
