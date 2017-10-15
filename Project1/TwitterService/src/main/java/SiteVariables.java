package main.java;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

import main.java.events.Block;
import main.java.events.Unblock;

/**
 * This class represents the general and Wuu-Bernsein
 * variables that are stored at a site.
 *
 * @author tsitsg
 *
 */
public class SiteVariables implements Serializable{
	private static final long serialVersionUID = 1L;

	private ConcurrentHashMap<Block, String> dictionary;
	private ConcurrentHashMap<LogEvent, String> partialLog;
	private int localClock;
	private AtomicIntegerArray matrixClock;
	private int numProcesses;
	private Site mySite;

	/**
	 * The public constructor
	 *
	 * @param n		The number of sites
	 */
	public SiteVariables(int n, Site site) {
		this.dictionary = new ConcurrentHashMap<>();
		this.partialLog = new ConcurrentHashMap<>();
		this.localClock = 0;
		this.matrixClock = new AtomicIntegerArray(n*n);
		// initialize matrixClock times to 0
		for (int i = 0; i < matrixClock.length(); i++) {
			matrixClock.set(i, 0);
		}
		this.numProcesses = n;
		this.mySite = site;
	}

	/* Getters */
	public ConcurrentHashMap<Block, String> getDictionary() {
		return dictionary;
	}
	public ConcurrentHashMap<LogEvent, String> getPartialLog() {
		return partialLog;
	}
	public int getLocalClock() {
		return localClock;
	}
	public int getNumProcesses() {
		return numProcesses;
	}
	public AtomicIntegerArray getMatrixClock() {
		return matrixClock;
	}
	public Site getMySite() {
		return mySite;
	}

	/* Setters */
	public void setDictionary(ConcurrentHashMap<Block, String> dictionary) {
		this.dictionary = dictionary;
	}
	public void setPartialLog(ConcurrentHashMap<LogEvent, String> partialLog) {
		this.partialLog = partialLog;
	}
	public void setLocalClock(int localClock) {
		this.localClock = localClock;
	}
	public void setNumProcesses(int numProcesses) {
		this.numProcesses = numProcesses;
	}
	public void setMatrixClock(AtomicIntegerArray matrixClock) {
		this.matrixClock = matrixClock;
	}
	public void setMySite(Site mySite) {
		this.mySite= mySite;
	}


	@Override
	public String toString() {
		return dictionary.toString()+"\n"+partialLog.toString()+"\n"+localClock+"\n"+matrixClock.toString();
	}


	/**
	 * Ticks the local and matrix clock.
	 *
	 * @param id	The ID of the site ticking its clock.
	 */
	public void tickClock(int id) {
		localClock += 1;
		matrixClock.getAndIncrement((numProcesses*id) + id);
	}


	/**
	 * Adds an event to the partial log
	 *
	 * @param e	Event to be added
	 */
	public void addToLog(LogEvent e) {
		partialLog.put(e, "true");
	}


	/**
	 * Adds an event to the dictionary
	 *
	 * @param e	Event to be added
	 */
	public boolean addToDictionary(Block e) {
		// Need to iterate over full dictionary to see if an earlier
		//  block event is somewhere. Can't simply use .put() because
		//   the objects are only equal if they have same timestamp
		for (Block b : dictionary.keySet()) {
			if ((b.getBlocker().equals(e.getBlocker())) && (b.getBlockee().equals(e.getBlockee()))) {
				return false;
			}
		}

		dictionary.put(e, "true");
		System.out.println("Blocked " + e.getBlockee());
		return true;
	}


	/**
	 * Removes an event from the dictionary
	 *
	 * @param e	Event to be removed
	 */
	public boolean removeFromDictionary(Unblock e) {
		// Need to iterate over full dictionary to see if an earlier
		//  block event is somewhere. Can't simply use .contains() because
		//   the objects are only equal if they have same timestamp
		for (Block b : dictionary.keySet()) {
			if ((b.getBlocker().equals(e.getUnblocker())) && (b.getBlockee().equals(e.getUnblockee()))) {
				dictionary.remove(b);
				System.out.println("Unblocked " + e.getUnblockee());
				return true;
			}
		}

		return false;
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
		System.out.println("Dictionary: " + dictionary.toString());

		for (Block b : dictionary.keySet()) {
			if (b.getBlockee().equals(s.getName())) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Returns a part of a log (NP for send, NE for receive)
	 *
	 * @param log 	 The log we want to reference
	 * @param mClock The matrix clock of the site that called this function
	 * @param id 	 The id of site that we want to know about
	 * @param n 	 The total number of sites
	 * @return The part of the log the requester doesn't know about
	 */
	public static ConcurrentHashMap<LogEvent,String> getNP(
			ConcurrentHashMap<LogEvent,String> log,
			AtomicIntegerArray mClock,
			int id,
			int n ) {

		ConcurrentHashMap<LogEvent, String> np = new ConcurrentHashMap<>();

		// Add an event to NP if site doesn't have a record of it
		for (LogEvent e : log.keySet()) {
			if (!hasRec(mClock, e, id, n)) {
				np.put(e, "true");
			}
		}

		return np;
	}


	/**
	 * Checks to see if matrix clock's site knows if site k knows about event e
	 *
	 * @param mClock	Matrix clock of site that wants information
	 * @param e 		Event of interest
	 * @param id		id of the site we want to know about
	 * @param n 		Total number of sites
	 *
	 * @return True if site who mClock belongs to knows that site k knows about event e
	 * 		   False otherwise
	 */
	public static boolean hasRec(AtomicIntegerArray mClock, LogEvent e, int id, int n) {
		boolean val = false;

		int clockValue = getMatrixClockValue(mClock, n, id , e.getId());

		if (clockValue >= e.getLocalTime()) {
			val = true;
		} else {
			val = false;
		}

		return val;
	}


	/**
	 * Gets the value of some matrix clock
	 *
	 * @param mc 		 Matrix clock of interest
	 * @param n 		 Number of sites
	 * @param rowSite	 Row site position
	 * @param columnSite Column site position
	 *
	 * @return the value at that position
	 */
	public static int getMatrixClockValue(AtomicIntegerArray mc, int n, int rowSite, int columnSite) {
		return mc.get((n*rowSite) + columnSite);
	}

	public void setMatrixClockValue(AtomicIntegerArray mc, int n, int rowSite, int columnSite,int v) {
		 mc.set(((n*rowSite) + columnSite),v);
	}

}
