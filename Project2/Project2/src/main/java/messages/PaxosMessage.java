package main.java.messages;

import main.java.Site;
import main.java.SiteVariables;

public class PaxosMessage {
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
