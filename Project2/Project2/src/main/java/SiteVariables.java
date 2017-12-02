package main.java;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import main.java.events.Block;
import main.java.events.Unblock;
import main.java.events.TwitterEvent;
import main.java.events.Tweet;
import java.util.List;
import java.util.ArrayList;

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
	private List<Site> sites;

	private CopyOnWriteArrayList<TwitterEvent> writeAheadLog;
	// array to mirror writeAheadLog, storing only accNum, accVal, maxPrepare, leader - maybe change to list<hashmap>
	private CopyOnWriteArrayList<SynodValues> paxosValues;
	private CopyOnWriteArrayList<Tweet> timeline;
	private int logSize;

	/**
	 * The public constructor
	 *
	 * @param n		The number of sites
	 */
	public SiteVariables(int n, Site site, List<Site> sites) {
		this.numProcesses = n;
		this.mySite = site;
		this.sites = sites;

		this.writeAheadLog= new CopyOnWriteArrayList<>();
		this.paxosValues = new CopyOnWriteArrayList<>();
		this.timeline = new CopyOnWriteArrayList<>();
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
	public List<Site> getSites() {
		return sites;
	}

	public ArrayList<Tweet> getTimeline()
	{
		ArrayList<Tweet> l = new ArrayList<Tweet>();
		l.addAll(timeline);
		return l;
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
	 * Determines if a this site has blocked site s already
	 *
	 * @param s	The user we are interested in
	 *
	 * @return True if user blocked
	 * 		   False otherwise
	 */
	public boolean isBlocked(String s) {
		boolean blocked = false;

		for (TwitterEvent te : writeAheadLog) {
			if (te instanceof Block) {
				Block b = (Block) te;
				if (b.getBlocker().getName().equals(mySite.getName()) && b.getBlockee().equals(s)) {
					blocked = true;
				}
			} else if (te instanceof Unblock) {
				Unblock u = (Unblock) te;
				if (u.getUnblocker().getName().equals(mySite.getName()) && u.getUnblockee().equals(s)) {
					blocked = false;
				}
			}
		}

		return blocked;
	}

	public boolean hasBlocked(String n1,String n2) {
//		for (Block b : dictionary.keySet()) {
//			if (b.getBlocker().getName().equals(n1) && b.getBlockee().equals(n2)) {
//				return true;
//			}
//		}

		return false;
	}



	/**
	 * Recreate timeline if needed
	 */
	public void recreateTimeline() {
		// determine type of most recent thing added to log
		CopyOnWriteArrayList<Tweet> tweets = new CopyOnWriteArrayList<>();
		List<String> blocks = new ArrayList<String>();

		for (TwitterEvent event : writeAheadLog) {
			if (event instanceof Tweet) {
				Tweet t = (Tweet) event;
				tweets.add(t);					
				
			}
			else if (event instanceof Block)
			{
				Block b = (Block) event;
				// System.out.println("found block " + b.toString());
	
				if (b.getBlockee().equals(mySite.getName()))
				{
					// System.out.println("adding block");
					blocks.add(b.getBlocker().getName());
				}
			}
			else if (event instanceof Unblock)
			{
				Unblock b = (Unblock) event;
				
				if (b.getUnblockee().equals(mySite.getName()))
				{
					blocks.remove(b.getUnblocker().getName());
				}
			}
		}
		// System.out.println("people blocking me: " + blocks);


		List<Tweet> toRemove = new ArrayList<>();
		
		for (Tweet t : tweets)
		{
			if (blocks.contains(t.getUser().getName()))
			{
				toRemove.add(t);
			}
		}
		
		tweets.removeAll(toRemove);

		timeline = tweets;
	}

	public void printWriteAheadLog() {
		for (TwitterEvent t: writeAheadLog) {
			if (t instanceof Tweet) {
				System.out.println(((Tweet) t).toString());
			} else if (t instanceof Block) {
				System.out.println(((Block) t).toString());
			} else {
				System.out.println(((Unblock) t).toString());				
			}
		}
	}
}