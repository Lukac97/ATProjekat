package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ACLMessage implements Serializable{
	
	private Performative performative;
	
	private AID sender;
	
	private List<AID> receivers;
	
	private AID replyTo;
	
	private String content;
	
	private Object contentObj;
	
	private HashMap<String,Serializable> userArgs;
	
	private String language;
	
	private String encoding;
	
	private String ontology;
	
	private String protocol;
	
	private String conversationID;
	
	private String replyWith;
	
	private Long replyBy;
	
	
	public ACLMessage() {
		receivers = new ArrayList<>();
		performative = Performative.NOT_UNDERSTOOD;
		userArgs = new HashMap<>();
	}
	
	public ACLMessage(Performative performative) {
		this.performative = performative;
		receivers = new ArrayList<>();
		userArgs = new HashMap<>();
	}
	
	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	public AID getSender() {
		return sender;
	}

	public void setSender(AID sender) {
		this.sender = sender;
	}

	public List<AID> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<AID> receivers) {
		this.receivers = receivers;
	}

	public AID getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getContentObj() {
		return contentObj;
	}

	public void setContentObj(Object contentObj) {
		this.contentObj = contentObj;
	}

	public HashMap<String, Serializable> getUserArgs() {
		return userArgs;
	}

	public void setUserArgs(HashMap<String, Serializable> userArgs) {
		this.userArgs = userArgs;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConversationID() {
		return conversationID;
	}

	public void setConversationID(String conversationID) {
		this.conversationID = conversationID;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public Long getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(Long replyBy) {
		this.replyBy = replyBy;
	}
	
	public boolean canReplyTo() {
		return sender != null || replyTo != null;
	}
	
	public ACLMessage makeReply(Performative performative) {
		if(!canReplyTo())
			throw new IllegalArgumentException("Theres noone to receive a reply.");
		ACLMessage reply = new ACLMessage(performative);
		reply.receivers.add(replyTo != null ? replyTo:sender);
		reply.language = language;
		reply.ontology = ontology;
		reply.encoding = encoding;
		reply.protocol = protocol;
		reply.conversationID = conversationID;
		return reply;
		
	}
	

}
