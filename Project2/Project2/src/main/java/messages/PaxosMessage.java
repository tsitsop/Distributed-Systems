package main.java.messages;

import java.io.Serializable;

import main.java.Site;
import main.java.SiteVariables;

public class PaxosMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Site sender;
	
	public PaxosMessage(Site sender) {
		this.sender = sender;
	}
	
	public void onReceive(SiteVariables receiverSite) {
		// do something
	}
	
	public String getSender() {
		return this.sender.getName();
	}
}
