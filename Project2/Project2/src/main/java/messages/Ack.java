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
