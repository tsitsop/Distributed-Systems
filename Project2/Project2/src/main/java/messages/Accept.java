package main.java.messages;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;
import main.java.events.TwitterEvent;

public class Accept extends PaxosMessage {
	private Integer n;
	private TwitterEvent v;
	
	public Accept(Site sender, Integer logIndex, Integer n, TwitterEvent v) {
		super(sender, logIndex);
		
		this.n = n;
		this.v = v;
	}

	public TwitterEvent getV() {
		return this.v;
	}
	
	public void onReceive(SiteVariables receiverSite) {
		// System.out.println("n = " + n);
		// System.out.println("MaxPrepare = " + receiverSite.getPaxVal(logIndex).getMaxPrepare());
		if (n >= receiverSite.getPaxVal(logIndex).getMaxPrepare()) {
			// System.out.println("n big enough");
			// update AccNum, AccVal, MaxPrepare
			SynodValues synodValues = receiverSite.getPaxVal(logIndex);
			synodValues.setAccNum(n);
			synodValues.setAccVal(v);
			synodValues.setMaxPrepare(n);
			receiverSite.modifyPaxosValues(logIndex, synodValues);
			
			// send back an ack with data
			Ack ack = new Ack(receiverSite.getMySite(), logIndex, n, v);
			TweetClient tc = new TweetClient(ack, sender);
			tc.start();
		}
	}

}
