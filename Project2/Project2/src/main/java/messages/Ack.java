package main.java.messages;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;
import main.java.ValContainer;
import main.java.events.TwitterEvent;

public class Ack extends PaxosMessage {
	private Integer accNum;
	private TwitterEvent accVal;
	
	public Ack(Site sender, Integer logIndex, Integer accNum, TwitterEvent accVal) {
		super(sender, logIndex);
		
		this.accNum = accNum;
		this.accVal = accVal;
	}
	
	public TwitterEvent getAccVal() {
		return this.accVal;
	} 


	public void onReceive(SiteVariables receiverSite, List<Site> sites) {
		// add ack value to list of acks received
		receiverSite.getPaxVal(logIndex).addAck(this);
		
		
		
		// if this is the first promise received, set a timer to wait and see if have received from majority
		//   - will first run 1 second after the first promise is received and then every 1/2 second
		if (receiverSite.getPaxosValues().get(logIndex).addAck(this) == 0) {
			long time = System.currentTimeMillis();
			Timer timer = new Timer();

			ValContainer<Boolean> success = new ValContainer<>(false);

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// if it has been 5 seconds and we still haven't gotten majority, then quit and restart paxos
					if (System.currentTimeMillis()-time > 5000) {
						this.cancel();
					}
					
					// if we have received a majority, we can continue with this execution
					if (receiverSite.getPaxosValues().get(logIndex).majorityAcks(sites.size())) {
						this.cancel();
						success.setVal(true);
					}
				}
			}, time + 1000, 500);	
		
			// if we successfully received a majority of responses, send commits
			if (success.getVal()) {
				this.sendCommits(receiverSite, sites);
			} else {
				// restarting so empty our Acks list
				receiverSite.getPaxosValues().get(logIndex).setAcks(new CopyOnWriteArrayList<>());
				
				// do nothing?
			}
		}	
	}
	
	/**
	 * Send commit message to all acceptors if have received majority promises
	 * @param receiverSite the site that received the promises - the current site
	 * @param sites
	 */
	public void sendCommits(SiteVariables receiverSite, List<Site> sites) {
		SynodValues synodValues = receiverSite.getPaxosValues().get(logIndex);

		// send Commit message to everyone
		Commit message = new Commit(receiverSite.getMySite(), logIndex, accVal);
		for (Site site : sites) {
			TweetClient tc = new TweetClient(message, site);
			tc.start();
		}			
	}
}
