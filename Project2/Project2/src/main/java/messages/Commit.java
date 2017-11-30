package main.java.messages;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.events.TwitterEvent;

public class Commit extends PaxosMessage {
	private TwitterEvent v;
	private Integer logIndex;
	
	public Commit(Site sender, Integer logIndex, TwitterEvent v) {
		super(sender);
		this.logIndex = logIndex;
		this.v = v;
	}
	
	public void onReceive(SiteVariables receiverSite) {
		// add event to log
		receiverSite.addToLog(v);
		
		// set the leader for this entry to sender
		receiverSite.getPaxosValues().get(logIndex).setLeader(sender.getId());;
	}

}
