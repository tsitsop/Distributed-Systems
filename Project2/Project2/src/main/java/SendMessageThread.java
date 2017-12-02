package main.java;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.plaf.multi.MultiButtonUI;

import main.java.events.TwitterEvent;
import main.java.messages.Accept;
import main.java.messages.Ack;
import main.java.messages.Commit;
import main.java.messages.PaxosMessage;
import main.java.messages.Prepare;
import main.java.messages.Promise;
import main.java.messages.RecoveryInfo;
import main.java.messages.RecoveryRequest;


public class SendMessageThread implements Runnable{
	private SiteVariables vars;
	private PaxosMessage m;
	private Thread thread;
	private List<Site> sites;

	
	public SendMessageThread(SiteVariables vars, PaxosMessage message, List<Site> sites) {
		this.vars = vars;
		this.m = message;
		this.sites = sites;
	}

	public void run() {
		// determine message type and run corresponding function
        if (m instanceof Prepare) {
        	Prepare pm = (Prepare) m;
        	int logIndex = pm.getLogIndex();
			
			// send message to all sites
         	TweetClient tc;
        	for (Site site : sites) {
             	System.out.println("Sending Prepare message to " + site.getName());
				tc = new TweetClient(m, site);
				tc.start();
			}
        	
        	// start timer
        	long time = System.currentTimeMillis();
			boolean success = false;

			while(true) {
				if (System.currentTimeMillis()-time > 5000) {
					break;
				}

				if (vars.getPaxVal(logIndex).majorityPromises(sites.size())) {
					success = true;
					break;
				}
			}

			// if we successfully received a majority of responses, send accepts
			if (success) {
				System.out.println("We got a majority response for promises");
				
				Accept message = this.createAccept(vars, sites, logIndex);
				SendMessageThread mt = new SendMessageThread(vars, message, sites);
				mt.start();		
			} else {
				System.err.println("failed to get majority response");
				
				// restarting so empty our Promises list
				vars.getPaxosValues().get(logIndex).setPromises(new CopyOnWriteArrayList<>());
				
				// create Prepare message with initial proposal number 1
				Prepare newMessage = new Prepare(vars.getMySite(), vars.getLogSize(), pm.getProposalNum()+1);

				SendMessageThread mt = new SendMessageThread(vars, newMessage, sites);
				mt.start();
			}

        } else if (m instanceof Accept) {
        	Accept am = (Accept) m;

			// send message to all sites
			TweetClient tc;
			for (Site site : sites) {
				System.out.println("Sending Accept message to " + site.getName());
				tc = new TweetClient(am, site);
				tc.start();
			}
			
			// start timer
			long time = System.currentTimeMillis();
			boolean success = false;

			while(true) {
				if (System.currentTimeMillis()-time > 5000) {
					break;
				}

				if (vars.getPaxVal(am.getLogIndex()).majorityAcks(sites.size())) {
					success = true;
					break;
				}
			}
			
			// if we successfully received a majority of responses, send accepts
			if (success) {
				System.out.println("We got a majority response for Acks");
			
				// send Commit messages to all sites
				Commit message = new Commit(vars.getMySite(), am.getLogIndex(), am.getV());
				for (Site site : sites) {
					System.out.println("Sending Commit message to " + site.getName());
					tc = new TweetClient(message, site);
					tc.start();
				}
			} else {
				System.err.println("failed to get majority Ack responses. done.");
			}
        } else if (m instanceof RecoveryRequest) {
			
			int numSites = sites.size();
			int myId = vars.getMySite().getId();
			int receivingId = 0;
			
			if (myId == numSites-1) {
				receivingId = 0;
			} else {
				receivingId = myId + 1;
			}

			Site dest = sites.get(receivingId);
			System.out.println("Sending Recovery message to " + dest.getName());
			
			TweetClient tc = new TweetClient((RecoveryRequest) m, dest);
			tc.start();
		}

	}

	/**
	 * Start a thread if it hasn't been started
	 */
	public void start() {
		if (this.thread == null) {
			this.thread = new Thread(this);
			thread.start();
		}
	}
	
	/**
	 * Send accept message to all acceptors if have received majority promises
	 * @param receiverSite the site that received the promises - the current site
	 * @param sites
	 */
	public Accept createAccept(SiteVariables receiverSite, List<Site> sites, int logIndex) {
		SynodValues synodValues = receiverSite.getPaxVal(logIndex);

		int largestAccNum = 0;
		TwitterEvent bestValue = null;

		// find best value to send
		for (Promise promise : synodValues.getPromises()) {
			if (promise.getAccNum() > largestAccNum) {
				largestAccNum = promise.getAccNum();
				if (bestValue != null) {
					bestValue = promise.getAccVal();
				}
			}
		}

		if (bestValue == null) {
			bestValue = synodValues.getMyProposal();
		}

		Accept message = new Accept(receiverSite.getMySite(), logIndex, synodValues.getMyProposalNum(), bestValue);

		return message;
	}

}
