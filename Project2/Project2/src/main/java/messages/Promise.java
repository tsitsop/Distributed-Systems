package main.java.messages;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.Site;
import main.java.SiteVariables;
import main.java.SynodValues;
import main.java.TweetClient;
import main.java.ValContainer;
import main.java.events.TwitterEvent;

public class Promise extends PaxosMessage {
	private Integer accNum;
	private TwitterEvent accVal;

	public Promise(Site sender, Integer logIndex, Integer accNum, TwitterEvent accVal) {
		super(sender, logIndex);

		this.accNum = accNum;
		this.accVal = accVal;
	}

	public Integer getAccNum() {
		return accNum;
	}
	public TwitterEvent getAccVal() {
		return accVal;
	}

	public void setAccNum(Integer accNum) {
		this.accNum = accNum;
	}
	public void setAccVal(TwitterEvent accVal) {
		this.accVal = accVal;
	}

	public void onReceive(SiteVariables receiverSite, List<Site> sites) {
		receiverSite.getPaxVal(logIndex).addPromise(this);
	}

}
