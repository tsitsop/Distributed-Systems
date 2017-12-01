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

public class Promise extends PaxosMessage {
	private Integer logIndex;
	private Integer accNum;
	private TwitterEvent accVal;

	public Promise(Site sender, Integer logIndex, Integer accNum, TwitterEvent accVal) {
		super(sender);

		this.logIndex = logIndex;
		this.accNum = accNum;
		this.accVal = accVal;
	}

	public Integer getLogIndex() {
		return logIndex;
	}
	public Integer getAccNum() {
		return accNum;
	}
	public TwitterEvent getAccVal() {
		return accVal;
	}

	public void setLogIndex(Integer logIndex) {
		this.logIndex = logIndex;
	}
	public void setAccNum(Integer accNum) {
		this.accNum = accNum;
	}
	public void setAccVal(TwitterEvent accVal) {
		this.accVal = accVal;
	}

	public void onReceive(SiteVariables receiverSite, List<Site> sites) {
		// if this is the first promise received, set a timer to wait and see if have received from majority
		//   - will first run 1 second after the first promise is received and then every 1/2 second
		if (receiverSite.getPaxosValues().get(logIndex).addPromise(this) == 0) {
			long time = System.currentTimeMillis();
			Timer timer = new Timer();

			ValContainer<Boolean> success = new ValContainer<>(false);

			/*
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// if it has been 5 seconds and we still haven't gotten majority, then quit and restart paxos
					if (System.currentTimeMillis()-time > 5000) {
						this.cancel();
					}

					// if we have received a majority, we can continue with this execution
					if (receiverSite.getPaxosValues().get(logIndex).majorityPromises(sites.size())) {
						this.cancel();
						success.setVal(true);
					}
				}
			}, time + 1000, 500);
			*/

			while(true)
			{
				if (System.currentTimeMillis()-time > 5000) {
					break;
				}

				if (receiverSite.getPaxVal(logIndex).majorityPromises(sites.size())) {
					success.setVal(true);
					break;
				}
			}

			// if we successfully received a majority of responses, send accepts
			if (success.getVal()) {
				this.sendAccepts(receiverSite, sites);
			} else {
				// restarting so empty our Promises list
				receiverSite.getPaxosValues().get(logIndex).setPromises(new CopyOnWriteArrayList<>());

				// create Prepare message with initial proposal number 1
				Prepare message = new Prepare(receiverSite.getMySite(), receiverSite.getLogSize(), 1);

				// send message to all followers
				TweetClient tc;
				for (Site site : sites) {
					tc = new TweetClient(message, site);
					tc.start();
				}
			}
		}
	}

	/**
	 * Send accept message to all acceptors if have received majority promises
	 * @param receiverSite the site that received the promises - the current site
	 * @param sites
	 */
	public void sendAccepts(SiteVariables receiverSite, List<Site> sites) {
		SynodValues synodValues = receiverSite.getPaxosValues().get(logIndex);

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

		// send Accept message to everyone
		Accept message = new Accept(receiverSite.getMySite(), logIndex, synodValues.getMyProposalNum(), bestValue);
		for (Site site : sites) {
			TweetClient tc = new TweetClient(message, site);
			tc.start();
		}
	}
}
