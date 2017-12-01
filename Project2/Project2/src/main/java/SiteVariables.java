package main.java;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.events.Block;
import main.java.events.TwitterEvent;
/**
 * This class represents the variables that are stored
 * at a site.
 *
 * @author tsitsg
 *
 */
public class SiteVariables implements Serializable{
	private static final long serialVersionUID = 1L;

	private int numProcesses;
	private Site mySite;

	private CopyOnWriteArrayList<TwitterEvent> writeAheadLog;
	// array to mirror writeAheadLog, storing only accNum, accVal, maxPrepare, leader - maybe change to list<hashmap>
	private CopyOnWriteArrayList<SynodValues> paxosValues;
	private int logSize;

	/**
	 * The public constructor
	 *
	 * @param n		The number of sites
	 */
	public SiteVariables(int n, Site site) {
		this.numProcesses = n;
		this.mySite = site;

		this.writeAheadLog= new CopyOnWriteArrayList<>();
		this.paxosValues = new CopyOnWriteArrayList<>();
		this.logSize = 0;
	}

	/* Getters */
	public int getNumProcesses() {
		return numProcesses;
	}
	public Site getMySite() {
		return mySite;
	}
	public CopyOnWriteArrayList<TwitterEvent> getWriteAheadLog() {
		return writeAheadLog;
	}
	public CopyOnWriteArrayList<SynodValues> getPaxosValues() {
		return paxosValues;
	}
	public int getLogSize() {
		return logSize;
	}

	/* Setters */
	public void setNumProcesses(int numProcesses) {
		this.numProcesses = numProcesses;
	}
	public void setMySite(Site mySite) {
		this.mySite= mySite;
	}
	public void setWriteAheadLog(CopyOnWriteArrayList<TwitterEvent> writeAheadLog) {
		this.writeAheadLog = writeAheadLog;
	}
	public void setPaxosValues(CopyOnWriteArrayList<SynodValues> paxosValues) {
		this.paxosValues = paxosValues;
	}
	public void setLogSize(int logSize) {
		this.logSize = logSize;
	}

	@Override
	public String toString() {
		return writeAheadLog.toString()+"\n"+paxosValues.toString();
	}

	public void expandLog(int size)
	{
		while (getLogSize()<size)
		{
			writeAheadLog.add(null);
			paxosValues.add(new SynodValues());
			setLogSize(getLogSize()+1);
		}
	}

	public SynodValues getPaxVal(int index)
	{
		expandLog(index+1);
		return paxosValues.get(index);
	}

	/**
	 * Add a new element to writeAheadLog
	 * @param event
	 * @return
	 */
	public void addToLog(TwitterEvent event,int index) {
		expandLog(index+1);
		writeAheadLog.set(index,event);
	}

	/**
	 * Modify log entry at index
	 * @param index
	 * @param accNum
	 * @return
	 */
	public boolean modifyWriteAheadLog(int index, int accNum) {
		// do we need more than just an accNum? do we ever change value written in log entry

		return true;
	}

	/**
	 * modify PaxosValues
	 * @param index
	 * @param newVals
	 */
	public void modifyPaxosValues(int index, SynodValues newVals) {
		expandLog(index+1);
		paxosValues.set(index, newVals);
	}

	/**
	 * Determines if a user is blocked or not
	 *
	 * @param s	The user we are interested in
	 *
	 * @return True if user blocked
	 * 		   False otherwise
	 */
	public boolean isBlocked(Site s) {
		// could simply search through writeAheadLog to see if blocked
		// or could store local copy of blocks and search that



//		for (Block b : dictionary.keySet()) {
//			if (b.getBlocker().getName().equals(mySite.getName()) && b.getBlockee().equals(s.getName())) {
//				return true;
//			}
//		}

		return false;
	}


	public boolean hasBlocked(String n1,String n2) {
//		for (Block b : dictionary.keySet()) {
//			if (b.getBlocker().getName().equals(n1) && b.getBlockee().equals(n2)) {
//				return true;
//			}
//		}

		return false;
	}




}
