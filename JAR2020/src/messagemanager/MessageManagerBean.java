package messagemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import model.ACLMessage;
import model.AID;
import model.Performative;


@Stateless
@Remote(MessageManager.class)
@LocalBean
public class MessageManagerBean implements MessageManager{
	
	@EJB
	private JMSFactory factory;
	
	private Session session;
	private MessageProducer defaultProducer;
	
	@PostConstruct
	public void postConstruct(){
		session = factory.getSession();
		defaultProducer = factory.getDefaultProducer(session);
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			session.close();
		} catch (JMSException e) {
			
		}
	}
	
	@Override
	public List<String> getPerformatives(){
		final Performative[] arr = Performative.values();
		List<String> list = new ArrayList<> (arr.length);
		for(Performative p : arr) {
			list.add(p.toString());
		}
		return list;
	}
	
	@Override
	public void post(ACLMessage msg) {
		for(int i = 0; i<msg.getReceivers().size(); i++) {
			if(msg.getReceivers().get(i) == null) {
				throw new IllegalArgumentException("AID cant be null");
			}
			AID aid = msg.getReceivers().get(i);
			
			try {
				ObjectMessage jmsMsg = session.createObjectMessage(msg);
				jmsMsg.setStringProperty("JMSXGroupId", aid.getName());
				jmsMsg.setIntProperty("AIDIndex", i);
				jmsMsg.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());
				defaultProducer.send(jmsMsg);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	@Override
	public String ping() {
		return "Pong from " + System.getProperty("jboss.node.name");
	}
}
