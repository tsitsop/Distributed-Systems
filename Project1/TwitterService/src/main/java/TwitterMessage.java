package main.java;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

import main.java.events.Tweet;

/**
 * This class is the object that gets sent when a user tweets. 
 * It contains the tweet itself, the matrix clock of the site
 * sending the tweet, and the subset of said site's partial log
 * that it doesn't know if the receiving site knows about.
 * 
 * @author tsitsg
 */
public class TwitterMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Tweet tweet;
	private AtomicIntegerArray matrixClock;
	private ConcurrentHashMap<LogEvent, String> np;
	
	/**
	 * The public constructor
	 * 
	 * @param tweet			The tweet the site is sending
	 * @param matrixClock	The matrix clock of the site sending the tweet
	 * @param np			The subset of the sender's partial log that it 
	 * 						 doesn't know if the receiver knows
	 */
	public TwitterMessage(Tweet t, AtomicIntegerArray matrixClock, ConcurrentHashMap<LogEvent, String> np) {
		this.tweet = t;
		this.matrixClock = matrixClock;
		this.np = np;
	}

	// Getters
	public Tweet getTweet() {
		return tweet;
	}
	public AtomicIntegerArray getMatrixClock() {
		return matrixClock;
	}
	public ConcurrentHashMap<LogEvent, String> getNp() {
		return np;
	}

	// Setters
	public void setTweet(Tweet t) {
		this.tweet = t;
	}
	public void setMatrixClock(AtomicIntegerArray matrixClock) {
		this.matrixClock = matrixClock;
	}
	public void setNp(ConcurrentHashMap<LogEvent, String> np) {
		this.np = np;
	}
	
}
