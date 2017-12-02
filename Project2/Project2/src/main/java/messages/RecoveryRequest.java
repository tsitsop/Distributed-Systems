package main.java.messages;

import java.util.Arrays;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;
import main.java.messages.RecoveryInfo;

import java.util.List;
import java.util.ArrayList;

public class RecoveryRequest extends PaxosMessage {
	private Integer proposalNum;

	public RecoveryRequest(Site sender, Integer logIndex) {
		super(sender, logIndex);
	}

	/**
	 * When a site receives a Recovery message, it replies with 
	 *  send a promise back to the sender. Else do nothing.
	 *
	 * @param receiverSite The site who received the Propose message
	 */
	public void onReceive(SiteVariables receiverSite) {
        List<SynodValues> paxosValues = receiverSite.getPaxosValues();
        
        List<Integer> accNums = new ArrayList<>();
        

        for (int i = 0; i < paxosValues.size(); i++) {
            accNums.add(paxosValues.get(i).getAccNum());
        }

        RecoveryInfo message = new RecoveryInfo(receiverSite.getMySite(), logIndex, accNums);

        TweetClient tc = new TweetClient(message, sender);
        tc.start();
	}

}
