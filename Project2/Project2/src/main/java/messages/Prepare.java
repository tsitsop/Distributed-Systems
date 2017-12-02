package main.java.messages;

import java.util.Arrays;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;

public class Prepare extends PaxosMessage {
	private Integer proposalNum;
	private Integer logIndex;

	public Prepare(Site sender, Integer logIndex, Integer proposalNum) {
		super(sender);
		this.logIndex = logIndex;
		this.proposalNum = proposalNum;
	}

	/**
	 * When a site receives a Propose message, if the proposal number is big enough
	 *  send a promise back to the sender. Else do nothing.
	 *
	 * @param receiverSite The site who received the Propose message
	 */
	public void onReceive(SiteVariables receiverSite) {
		SynodValues synodValues = receiverSite.getPaxVal(logIndex);

//		System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()));
//		System.out.println(" entered onReceive of Prepare");
		if (this.proposalNum > synodValues.getMaxPrepare()) {
			// update local maxPrepare
			synodValues.setMaxPrepare(proposalNum);
			receiverSite.modifyPaxosValues(logIndex, synodValues);


			System.out.println("Sending Promise back to "+ sender.getName());
			// create Promise message
			Promise message = new Promise(receiverSite.getMySite(), logIndex, synodValues.getAccNum(), synodValues.getAccVal());

			// send Promise back to sender
			TweetClient tc = new TweetClient(message, sender);
			tc.start();
		} else {
			System.out.println("Prepare sent by " + sender.getName() + " had a number that was too small");
		}
	}

	public Integer getProposalNum() {
		return proposalNum;
	}
	public Integer getLogIndex() {
		return logIndex;
	}

	public void setProposalNum(Integer proposalNum) {
		this.proposalNum = proposalNum;
	}
	public void setLogIndex(Integer logIndex) {
		this.logIndex = logIndex;
	}

}
