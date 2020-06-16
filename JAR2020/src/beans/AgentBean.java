package beans;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import agent_manager.AgentManagerBean;
import messagemanager.MessageManagerBean;
import model.ACLMessage;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.Performative;
import node.NodeManager;
import ws.WSEndPoint;


@Stateless
@Path("/agents")
@LocalBean
public class AgentBean {
	
	private Connection connection;
	
	@Resource(lookup = "java:jboss/exported/jms/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@EJB
	WSEndPoint ws;
	
	@EJB
	AgentManagerBean am;
	
	@EJB
	NodeManager nm;
	
	@EJB
	MessageManagerBean msm;
		
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		System.out.println("--------------------");
			for(AgentType at : am.getAgentTypes()) {
				System.out.println(at.getName());
			}
		System.out.println("--------------------");
		return "OK";
	}
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgentsTypes() {
		List<AgentType> agentTypes = am.getAgentTypes();
		if(agentTypes.isEmpty()) {
			return Response.status(400).entity("No agent types found.").build();
		}
		return Response.status(200).entity(am.getAgentTypes()).build();
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningAgents() {
		List<AID> runningAgents = am.getRunningAgents();
		if(runningAgents.isEmpty()) {
			return Response.status(400).entity("No agents running.").build();
		}
		return Response.status(200).entity(am.getRunningAgents()).build();	
	}
	
	
	@PUT
	@Path("/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startAgent(@PathParam("type") String type, @PathParam("name") String name) {
		try {
			AgentCenter agentCenter = nm.getThisNode();
			if(type != null) {	
				for(AgentType at : am.getAgentTypes()) {
					if(type.equals(at.getName())) {
						AID aid = new AID(name, agentCenter, at);
						System.out.println(aid);
						boolean added = false;
						try {
							added = am.startAgent(aid);
						} catch (Exception e) {
							return Response.status(400).entity(aid.getType() + " cannot be added.").build();
						}
						if(added) {
							return Response.status(200).entity(aid).build();
						}
						return Response.status(400).entity("Agent " + name + " already exists.").build();
						
					}
				}
			}
			return Response.status(400).entity("Check type and try again.").build();
		}catch (Exception e) {
			e.printStackTrace();
			return Response.status(400).entity("Failed to start agent").build();
		}
	}
	
	
	
	@DELETE
	@Path("/running/{aid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopAgent(@PathParam("aid") String aid) {
		for(AID agent : am.getRunningAgents()) {
			if(agent.getName().equals(aid)) {
				am.stopAgent(agent);
				return Response.status(200).entity(agent).build();
			}
		}
		
		return Response.status(400).entity("Unable to stop agent").build();
		
	}
	
	@GET
	@Path("/pingpong")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pingpongTest() {
		List<AID> runningAgents = am.getRunningAgents();
		AID pingAgent = new AID();
		boolean pingExists = false;
		boolean pongExists = false;
		for(AID aid : runningAgents) {
			if(aid.getType().getName().equals("PingAgent") && aid.getName().equals("ping")) {
				pingExists = true;
				pingAgent = aid;
			}
			
			if(aid.getType().getName().equals("PongAgent") && aid.getName().equals("pong")) {
				pongExists = true;
			}
		}
		
		if(!(pingExists && pongExists)) {
			return Response.status(400).entity("There is no ping/pong agent!").build();
		}
		
		
		ACLMessage msg = new ACLMessage(Performative.REQUEST);
		List<AID> recs = new ArrayList<>();
		recs.add(pingAgent);
		msg.setReceivers(recs);
		msg.setContent("pong");
		msm.post(msg);
		
		return Response.status(200).entity("Ping sent successfully!").build();
	}
	
	@GET
	@Path("/cnptest/{manager}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cnpTest(@PathParam("manager") String manager) {
		List<AID> runningAgents = am.getRunningAgents();
		AID managerAgent = new AID();
		boolean managerExists = false;
		for(AID aid : runningAgents) {
			if(aid.getType().getName().equals("CNPManagerAgent") && aid.getName().equals(manager)) {
				managerAgent = aid;
				managerExists = true;
			}
		}
		
		if(!managerExists) {
			return Response.status(400).entity("There is no manager agent!").build();
		}
		
		
		ACLMessage msg = new ACLMessage(Performative.REQUEST);
		List<AID> recs = new ArrayList<>();
		recs.add(managerAgent);
		msg.setReceivers(recs);
		msg.setContent("task1");
		msm.post(msg);
		
		return Response.status(200).entity("CNP task sent successfully!").build();
	}
}
