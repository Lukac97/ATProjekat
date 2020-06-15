package agents;

import model.ACLMessage;
import model.Agent;
import model.Performative;

public class PongAgent extends Agent{
	
	private int counter;
	
	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		if(message.getPerformative() == Performative.REQUEST) {
			ACLMessage reply = message.makeReply(Performative.INFORM);
			reply.setSender(getId());
			reply.getUserArgs().put("pongCounter", ++counter);
			msm().post(reply);
		}
	}

}
