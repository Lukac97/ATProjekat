package agents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.Performative;

@Stateful
public class PingAgent extends Agent{
	
	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		if(message.getPerformative() == Performative.REQUEST) {
			AID pongAid = new AID();
			pongAid.setHost(new AgentCenter("master", "localhost:8080"));
			pongAid.setName(message.getContent());
			pongAid.setType(new AgentType("PongAgent", "agents"));
			
			System.out.println("PING RECEIVED MSG: " + message.getContent());
			
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(this.getId());
			msgToPong.getReceivers().add(pongAid);
			msm().post(msgToPong);
		} else if(message.getPerformative() == Performative.INFORM) {
			ACLMessage msgFromPong = message;
			HashMap<String, Serializable> args = new HashMap<>(msgFromPong.getUserArgs());
			System.out.println("Pong returned: " + msgFromPong.getUserArgs().get("pongCounter"));
		}
	}

}
