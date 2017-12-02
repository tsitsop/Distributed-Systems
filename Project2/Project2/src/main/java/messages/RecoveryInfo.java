package main.java.messages;

import java.util.Arrays;

import main.java.SendMessageThread;
import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;

import java.util.List;
import java.util.ArrayList;

public class RecoveryInfo extends PaxosMessage {
	private Integer proposalNum;
    private List<Integer> accNums;

	public RecoveryInfo(Site sender, Integer logIndex, List<Integer> accNums) {
        super(sender, logIndex);
        this.accNums = accNums;
	}

	/**
	 * When a site receives a Recovery message, it replies with 
	 *  send a promise back to the sender. Else do nothing.
	 *
	 * @param receiverSite The site who received the Propose message
	 */
	public void onReceive(SiteVariables receiverSite) {
        // run full paxos on each missing log entry 
        for (int i = 0; i < accNums.size(); i++) {
            // if we aren't up to date in terms of 
            if (receiverSite.getPaxVal(i).getAccNum() < accNums.get(i)) {
                Prepare message = new Prepare(receiverSite.getMySite(), i, accNums.get(i));
                SynodValues synodValues = receiverSite.getPaxVal(i);
                synodValues.setMyProposal(null);
				synodValues.setMyProposalNum(accNums.get(i));
                receiverSite.modifyPaxosValues(i, synodValues);
                
                SendMessageThread smt = new SendMessageThread(receiverSite, message, receiverSite.getSites());
                smt.start();
            }
        }
    }

}
