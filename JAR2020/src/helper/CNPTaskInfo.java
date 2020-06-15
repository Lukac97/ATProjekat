package helper;

import java.util.ArrayList;
import java.util.List;

import model.ACLMessage;

public class CNPTaskInfo {
	private int callsSent;
	private int proposalsReceived;
	private List<ACLMessage> proposalMessages;
	
	public CNPTaskInfo() {
		callsSent = 0;
		proposalsReceived = 0;
		proposalMessages = new ArrayList<>();
	}
	
	public int getCallsSent() {
		return callsSent;
	}
	
	public int getProposalsReceived() {
		return proposalsReceived;
	}
	
	public List<ACLMessage> getProposalMessages() {
		return proposalMessages;
	}
	
	public void setCallsSent(int callsSent) {
		this.callsSent = callsSent;
	}

	public void setProposalsReceived(int proposalsReceived) {
		this.proposalsReceived = proposalsReceived;
	}

	public void setProposalMessages(List<ACLMessage> proposalMessages) {
		this.proposalMessages = proposalMessages;
	}
	
	public void incCallsSent() {
		this.callsSent = this.callsSent+1;
	}
	
	public void incProposalsReceived() {
		this.proposalsReceived = this.proposalsReceived+1;
	}
	
	public void addProposalMessage(ACLMessage msg) {
		this.proposalMessages.add(msg);
	}
}
