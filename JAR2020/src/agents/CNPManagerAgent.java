package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import helper.CNPTaskInfo;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.Performative;
import ws.WSEndPoint;

public class CNPManagerAgent extends Agent{
	
	HashMap<String, CNPTaskInfo> taskMap = new HashMap<>();
	
	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		boolean taskExists = false;
		for(String taskName : taskMap.keySet()) {
			if(taskName.equals(message.getContent())) {
				taskExists = true;
				break;
			}
		}
		if(message.getContent() != "") {
			if(message.getPerformative() == Performative.REQUEST && !taskExists) {
				ACLMessage msgToResponders = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
				msgToResponders.setSender(this.getId());
				msgToResponders.setContent(message.getContent());
				taskMap.put(message.getContent(), new CNPTaskInfo());
				for(AID respAid : agm().getResponderAgents()){
					taskMap.get(message.getContent()).incCallsSent();
					msgToResponders.getReceivers().add(respAid);
					try {
						Context context = new InitialContext();
						WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
						ws.echoTextMessage("(Manager - " + getId().getName() + ") sent CALL_FOR_PROPOSAL to (Responder - " + respAid.getName() + "). calls sent: " + taskMap.get(message.getContent()).getCallsSent());
					} catch (NamingException e) {
						e.printStackTrace();
					}
					System.out.println("(Manager - " + getId().getName() + ") sent CALL_FOR_PROPOSAL to (Responder - " + respAid.getName() + "). calls sent: " + taskMap.get(message.getContent()).getCallsSent());
				}
				msm().post(msgToResponders);
			}else if(message.getPerformative() == Performative.REJECT && taskExists) {
				taskMap.get(message.getContent()).incProposalsReceived();
				try {
					Context context = new InitialContext();
					WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
					ws.echoTextMessage("(Manager - " + getId().getName() + ") was REJECTED ("+ message.getUserArgs().get("evaluateAction") +" by (Responder - " + message.getSender().getName() + ").");
				} catch (NamingException e) {
					e.printStackTrace();
				}
				System.out.println("(Manager - " + getId().getName() + ") was REJECTED by (Responder - " + message.getSender().getName() + ").");

				if(taskMap.get(message.getContent()).getCallsSent() == taskMap.get(message.getContent()).getProposalsReceived()) {
					acceptBestProposal(message.getContent());
				}
			}else if(message.getPerformative() == Performative.PROPOSE && taskExists) {
				try {
					Context context = new InitialContext();
					WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
					ws.echoTextMessage("(Manager - " + getId().getName() + ") was PROPOSED ("+ message.getUserArgs().get("evaluateAction") +") by (Responder - " + message.getSender().getName() + ").");
				} catch (NamingException e) {
					e.printStackTrace();
				}
				System.out.println("(Manager - " + getId().getName() + ") was PROPOSED by (Responder - " + message.getSender().getName() + ").");

				taskMap.get(message.getContent()).incProposalsReceived();
				taskMap.get(message.getContent()).addProposalMessage(message);
				
				if(taskMap.get(message.getContent()).getCallsSent() == taskMap.get(message.getContent()).getProposalsReceived()) {
					acceptBestProposal(message.getContent());
				}
			}else if(message.getPerformative() == Performative.INFORM && taskExists) {
				taskMap.remove(message.getContent());
				try {
					Context context = new InitialContext();
					WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
					ws.echoTextMessage("(Manager - " + getId().getName() + ") was INFORMED by (Responder - " + message.getSender().getName() + ") about task completion.");
				} catch (NamingException e) {
					e.printStackTrace();
				}
				System.out.println("(Manager - " + getId().getName() + ") was INFORMED by (Responder - " + message.getSender().getName() + ") about task completion.");
			} else if(message.getPerformative() == Performative.CANCEL && taskExists) {
				taskMap.remove(message.getContent());
				try {
					Context context = new InitialContext();
					WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
					ws.echoTextMessage("(Manager - " + getId().getName() + ") was CANCELLED by (Responder - " + message.getSender().getName() + ") - task failure.");
				} catch (NamingException e) {
					e.printStackTrace();
				}
				System.out.println("(Manager - " + getId().getName() + ") was CANCELLED by (Responder - " + message.getSender().getName() + ") - task failure.");
			}
		}
	}
	
	public void acceptBestProposal(String taskName) {
		if(taskMap.get(taskName).getProposalMessages().size() == 0) {
			//Ako nema nijedan propose, saljemo opet call for proposal
			
			ACLMessage msgToSelf = new ACLMessage(Performative.REQUEST);
			msgToSelf.setSender(this.getId());
			msgToSelf.setContent(taskName);
			List<AID> receivers = new ArrayList<>();
			receivers.add(this.getId());
			msgToSelf.setReceivers(receivers);
			msm().post(msgToSelf);
			taskMap.remove(taskName);
		}else {
			ACLMessage bestMsg = new ACLMessage();
			int startInd = -1;
			
			for(ACLMessage msg : taskMap.get(taskName).getProposalMessages()) {
				if(startInd == -1) {
					startInd = 0;
					bestMsg = msg;
				}
				if((int)msg.getUserArgs().get("evaluateAction") > (int)bestMsg.getUserArgs().get("evaluateAction")) {
					bestMsg = msg;
				}
			}
			
			for(ACLMessage msg : taskMap.get(taskName).getProposalMessages()) {
				if(msg.getSender().getName().equals(bestMsg.getSender().getName())) {
					ACLMessage msgToResponder = msg.makeReply(Performative.ACCEPT);
					msgToResponder.setSender(getId());
					msgToResponder.setContent(msg.getContent());
					msm().post(msgToResponder);
				} else {
					ACLMessage msgToResponder = msg.makeReply(Performative.REJECT);
					msgToResponder.setSender(getId());
					msgToResponder.setContent(msg.getContent());
					msm().post(msgToResponder);
				}
			}
		}
		
	}
	
	public int evaluateAction() {
		return (int) (Math.random() * 10);
	}
	
}
