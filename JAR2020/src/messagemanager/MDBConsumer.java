package messagemanager;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agent_manager.AgentManagerBean;
import model.ACLMessage;
import model.AID;
import model.Agent;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue")
})
public class MDBConsumer implements MessageListener {
	@EJB
	private AgentManagerBean agm;
	
	@Override
	public void onMessage(Message msg) {
		try {
			//processMessage(msg);
			ACLMessage acl = (ACLMessage) ((ObjectMessage) msg).getObject();
			int i = msg.getIntProperty("AIDIndex");
			AID aid = acl.getReceivers().get(i);
			
			//deliverMessage(acl,aid);
			Agent agent = agm.getAgentReference(aid);
			if(agent != null) {
				agent.handleMessage(acl);
			} else {
				System.out.println("No such agent : " + aid.getName());
			}
		} catch (JMSException ex) {
			System.out.println("Cannot process an incoming message!");
		}
	}
}
