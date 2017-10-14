package main.java;

import java.io.Serializable;

import main.java.events.TwitterEvent;

/**
 * This class represents the object that is stored in 
 * a log entry. It has the creation site's ID, local 
 * time, and the event being stored.
 * 
 * @author tsitsg
 *
 */
public class LogEvent implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int localTime;
	private TwitterEvent event;
	
	/**
	 * Public constructor
	 * 
	 * @param i 	The id of the site that created the event
	 * @param l		The local clock time at which the event was created
	 * @param e		The TwitterEvent that is being stored in the log
	 */
	LogEvent(int i,int l, TwitterEvent e) {
		id = i;
		localTime = l;
		event = e;
	}

	/* Getters */
	public int getId() {
		return id;
	}
	public int getLocalTime() {
		return localTime;
	}
	public TwitterEvent getEvent() {
		return event;
	}

	/* Setters */
	public void setId(int id) {
		this.id = id;
	}
	public void setLocalTime(int localTime) {
		this.localTime = localTime;
	}
	public void setEvent(TwitterEvent event) {
		this.event = event;
	}
}
