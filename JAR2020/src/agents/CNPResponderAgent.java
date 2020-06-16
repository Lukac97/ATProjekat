package agents;

import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.Performative;

public class CNPResponderAgent extends Agent{

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		if(message.getPerformative() == Performative.CALL_FOR_PROPOSAL) {
			int ea = evaluateAction();
			if(ea > 2) {
				ACLMessage msgToManager = message.makeReply(Performative.PROPOSE);
				msgToManager.setSender(getId());
				msgToManager.setContent(message.getContent());
				msgToManager.getUserArgs().put("evaluateAction", ea);
				msm().post(msgToManager);
			}else {
				ACLMessage msgToManager = message.makeReply(Performative.REJECT);
				msgToManager.setSender(getId());
				msgToManager.setContent(message.getContent());
				msgToManager.getUserArgs().put("evaluateAction", ea);
				msm().post(msgToManager);
			}
		}else if(message.getPerformative() == Performative.ACCEPT) {
			if(evaluateAction() > 2) {
				ACLMessage msgToManager = message.makeReply(Performative.INFORM);
				msgToManager.setSender(getId());
				msgToManager.setContent(message.getContent());
				msm().post(msgToManager);
			}else {
				ACLMessage msgToManager = message.makeReply(Performative.CANCEL);
				msgToManager.setSender(getId());
				msgToManager.setContent(message.getContent());
				msm().post(msgToManager);
			}
		}else if(message.getPerformative() == Performative.REJECT) {
			System.out.println("(Responder-" + getId().getName() + ") was REJECTED by (Manager-" + message.getSender().getName() +").");
		}
	}
	
	public int evaluateAction() {
		return (int) (Math.random() * 10);
	}
	
}
