package main.java.messages;

import java.io.Serializable;

import main.java.Site;

public class PaxosMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Site sender;
	protected Integer logIndex;
	
	public PaxosMessage(Site sender, Integer index) {
		this.sender = sender;
		this.logIndex = index;
	}
	
	public Integer getLogIndex() {
		return this.logIndex;
	}

	public void onReceive() {
	}
	
	public String getSender() {
		return this.sender.getName();
	}
}
