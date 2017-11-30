package main.java.messages;

import main.java.Site;
import main.java.SiteVariables;
import main.java.TweetClient;
import main.java.events.TwitterEvent;

public class Accept extends PaxosMessage {
	private Integer logIndex;
	private Integer n;
	private TwitterEvent v;
	
	public Accept(Site sender, Integer logIndex, Integer n, TwitterEvent v) {
		super(sender);
		
		this.logIndex = logIndex;
		this.n = n;
		this.v = v;
	}
	
	@Override
	public void onReceive(SiteVariables receiverSite) {
		if (n >= receiverSite.getPaxosValues().get(logIndex).getMaxPrepare()) {
			// update AccNum, AccVal, MaxPrepare
			receiverSite.getPaxosValues().get(logIndex).setAccNum(n);
			receiverSite.getPaxosValues().get(logIndex).setAccVal(v);
			receiverSite.getPaxosValues().get(logIndex).setMaxPrepare(n);
			
			// send back an ack with data
			Ack ack = new Ack(receiverSite.getMySite(), logIndex, n, v);
			TweetClient tc = new TweetClient(ack, sender);
			tc.start();
		}
	}

}
