package messagemanager;

import java.util.List;

import model.ACLMessage;

public interface MessageManager {
	List<String> getPerformatives();
	
	void post(ACLMessage message);
	
	String ping();
}
