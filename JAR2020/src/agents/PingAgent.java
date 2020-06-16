package agents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.Performative;
import ws.WSEndPoint;

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
			
			try {
				Context context = new InitialContext();
				WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
				ws.echoTextMessage("PING RECEIVED MSG: " + message.getContent());
			} catch (NamingException e) {
				e.printStackTrace();
			}
			System.out.println("PING RECEIVED MSG: " + message.getContent());
			
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(this.getId());
			msgToPong.getReceivers().add(pongAid);
			msm().post(msgToPong);
		} else if(message.getPerformative() == Performative.INFORM) {
			ACLMessage msgFromPong = message;
			HashMap<String, Serializable> args = new HashMap<>(msgFromPong.getUserArgs());
			try {
				Context context = new InitialContext();
				WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
				ws.echoTextMessage("Pong returned: " + msgFromPong.getUserArgs().get("pongCounter"));
			} catch (NamingException e) {
				e.printStackTrace();
			}
			System.out.println("Pong returned: " + msgFromPong.getUserArgs().get("pongCounter"));
		}
	}

}
