package model;

import java.util.Hashtable;

import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import messagemanager.MessageManager;
import messagemanager.MessageManagerBean;

@Singleton
public abstract class Agent implements AgentInterface{
	
	
	protected AID Id;

	public AID getId() {
		return Id;
	}

	public void setId(AID id) {
		Id = id;
	}
	
	public MessageManager msm() {
		try {
			Hashtable<String, Object> jndiProps = new Hashtable<>();
			jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			Context context = new InitialContext(jndiProps);
			MessageManager msm = (MessageManager) context.lookup("ejb:EAR2020/JAR2020//" + MessageManagerBean.class.getSimpleName() + "!" + MessageManager.class.getName());
			return msm;
		} catch (NamingException ex) {
			System.out.println("Context init error!");
			return null;
		}
	}

}
