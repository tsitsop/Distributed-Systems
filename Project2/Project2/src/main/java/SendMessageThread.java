package main.java;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.events.TwitterEvent;
import main.java.messages.Accept;
import main.java.messages.Ack;
import main.java.messages.Commit;
import main.java.messages.PaxosMessage;
import main.java.messages.Prepare;
import main.java.messages.Promise;


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
        	//System.out.println(logIndex);
    		//System.out.println("Proposal: "+vars.getPaxVal(vars.getLogSize()-1).getMyProposalNum());
    		//System.out.println("Proposal: "+vars.getPaxVal(logIndex).getMyProposalNum());
        	// send message to all followers
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
				this.sendAccepts(vars, sites, logIndex);
			} else {
				System.err.println("failed to get majority response");
				
				// restarting so empty our Promises list
				vars.getPaxosValues().get(logIndex).setPromises(new CopyOnWriteArrayList<>());
				
				// create Prepare message with initial proposal number 1
				Prepare newMessage = new Prepare(vars.getMySite(), vars.getLogSize(), ((Prepare) m).getProposalNum()+1);

				SendMessageThread mt = new SendMessageThread(vars, newMessage, sites);
				mt.start();
			}

        } else if (m instanceof Promise) {
        	System.out.println("Received Promise message from " + m.getSender());
        	Promise pm = (Promise) m;
        	pm.onReceive(vars, sites);
        } else if (m instanceof Accept) {
        	System.out.println("Received Accept message from " + m.getSender());
        	Accept am = (Accept) m;
        	am.onReceive(vars);
        } else if (m instanceof Ack) {
        	System.out.println("Received Ack message from " + m.getSender());
        	Ack am = (Ack) m;
        	am.onReceive(vars, sites);
        } else if (m instanceof Commit) {
        	System.out.println("Received Commit message from " + m.getSender());
        	Commit cm = (Commit) m;
        	cm.onReceive(vars);
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
	public void sendAccepts(SiteVariables receiverSite, List<Site> sites, int logIndex) {
		SynodValues synodValues = receiverSite.getPaxVal(logIndex);

		int largestAccNum = 0;
		TwitterEvent bestValue = null;

		// find best value to send
		for (Promise promise : synodValues.getPromises()) {
			System.out.println("AccNum: "+promise.getAccNum()+"AccVal:"+promise.getAccVal());
			if (promise.getAccNum() > largestAccNum) {
				largestAccNum = promise.getAccNum();
				if (bestValue != null) {
					bestValue = promise.getAccVal();
				}
			}
		}

		if (bestValue == null) {
			bestValue = synodValues.getMyProposal();
			//					
			
		}

		// send Accept message to everyone
		System.out.println("Best Value: "+bestValue);
		Accept message = new Accept(receiverSite.getMySite(), logIndex, synodValues.getMyProposalNum(), bestValue);
		for (Site site : sites) {
			System.out.println("Sending Accept message to " + site.getName());
			TweetClient tc = new TweetClient(message, site);
			tc.start();
		}
	}

}
