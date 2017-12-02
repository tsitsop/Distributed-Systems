package main.java;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.events.TwitterEvent;
import main.java.messages.Ack;
import main.java.messages.Promise;

public class SynodValues implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer accNum;
	private TwitterEvent accVal;
	private Integer maxPrepare;
	private Integer leader;
	private TwitterEvent myProposal;
	private Integer myProposalNum;
	private CopyOnWriteArrayList<Promise> promises;
	private CopyOnWriteArrayList<Ack> acks;

	public SynodValues() {
		accNum = 0;
		accVal = null;
		maxPrepare = 0;
		leader = 0;
		myProposal = null;
		myProposalNum = 0;
		promises = new CopyOnWriteArrayList<>();
		acks = new CopyOnWriteArrayList<>();
	}

	/* Getters */
	public Integer getAccNum() {
		return accNum;
	}
	public TwitterEvent getAccVal() {
		return accVal;
	}
	public Integer getMaxPrepare() {
		return maxPrepare;
	}
	public Integer getLeader() {
		return leader;
	}
	public TwitterEvent getMyProposal() {
		return myProposal;
	}
	public Integer getMyProposalNum() {
		return myProposalNum;
	}
	public CopyOnWriteArrayList<Promise> getPromises() {
		return promises;
	}
	public CopyOnWriteArrayList<Ack> getAcks() {
		return acks;
	}

	/* Setters */
	public void setAccNum(Integer accNum) {
		this.accNum = accNum;
	}
	public void setAccVal(TwitterEvent accVal) {
		this.accVal = accVal;
	}
	public void setMaxPrepare(Integer maxPrepare) {
		this.maxPrepare = maxPrepare;
	}
	public void setLeader(Integer leader) {
		this.leader = leader;
	}
	public void setMyProposal(TwitterEvent myProposal) {
		this.myProposal = myProposal;
	}
	public void setMyProposalNum(Integer num) {
		this.myProposalNum = num;
	}
	public void setPromises(CopyOnWriteArrayList<Promise> promises) {
		this.promises = promises;
	}
	public void setAcks(CopyOnWriteArrayList<Ack> acks) {
		this.acks = acks;
	}

	
	
	/**
	 * Add promise to list of received promises
	 * @param promise
	 * @return the number of promises before adding this element
	 */
	public int addPromise(Promise promise) {
		promises.add(promise);

		return promises.size()-1;
	}

	/**
	 * Add ack to list of received acks
	 * @param ack
	 */
	public int addAck(Ack ack) {
		acks.add(ack);

		return acks.size()-1;
	}

	/**
	 * Check if have received promises from majority of sites
	 * @param numSites
	 * @return true if majority sent promises
	 */
	public boolean majorityPromises(int numSites) {
		if (promises.size() > numSites/2) {
			return true;
		}

		return false;
	}

	/**
	 * Check if have received acks from majority of sites
	 * @param numSites
	 * @return true if majority sent acks
	 */
	public boolean majorityAcks(int numSites) {
		if (acks.size() > numSites/2) {
			return true;
		}

		return false;
	}



}
