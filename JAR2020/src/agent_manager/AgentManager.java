package agent_manager;

import java.util.List;

import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

public interface AgentManager {
	public void startInit(AgentCenter center);
	public List<AID> getRunningAgents();
	public List<AID> getResponderAgents();
	public Agent getAgentReference(AID aid);
	public List<AgentType> getAgentTypes();
	public boolean startAgent(AID agent);
	public void addAgentType(AgentType at);
	public void stopAgent(AID agent);
}
