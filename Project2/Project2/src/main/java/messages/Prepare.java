package main.java.messages;

import java.util.Arrays;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;

public class Prepare extends PaxosMessage {
	private Integer proposalNum;

	public Prepare(Site sender, Integer logIndex, Integer proposalNum) {
		super(sender, logIndex);
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

		if (this.proposalNum > synodValues.getMaxPrepare()) {
			// update local maxPrepare
			synodValues.setMaxPrepare(proposalNum);
			receiverSite.modifyPaxosValues(logIndex, synodValues);

			System.out.println("(index " + logIndex + ") Sending Promise back to "+ sender.getName());

			// create Promise message
			Promise message = new Promise(receiverSite.getMySite(), logIndex, synodValues.getAccNum(), synodValues.getAccVal());

			// send Promise back to sender
			TweetClient tc = new TweetClient(message, sender);
			tc.start();
		} else {
			System.out.println("(index " + logIndex + ") Prepare message sent by " + sender.getName() + " had a proposal number that was too small");
		}
	}

	public Integer getProposalNum() {
		return proposalNum;
	}

	public void setProposalNum(Integer proposalNum) {
		this.proposalNum = proposalNum;
	}
}
