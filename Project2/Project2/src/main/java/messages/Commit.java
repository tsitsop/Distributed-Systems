package main.java.messages;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.events.Block;
import main.java.events.TwitterEvent;
import main.java.UtilityFunctions;
public class Commit extends PaxosMessage {
	private TwitterEvent v;

	public Commit(Site sender, Integer logIndex, TwitterEvent v) {
		super(sender, logIndex);
		this.v = v;
	}

	public void onReceive(SiteVariables receiverSite) {
		// add event to log
		receiverSite.addToLog(v,logIndex);

		// set the leader for this entry to sender
		receiverSite.getPaxosValues().get(logIndex).setLeader(sender.getId());

		//receiverSite.clearTimeline();

		receiverSite.recreateTimeline();


		// store our site variables to disk to maintain memory (includes blocking/unblocking)
		UtilityFunctions.writeVars(receiverSite);
		

	}

}
