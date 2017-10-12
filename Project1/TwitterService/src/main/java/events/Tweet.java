package main.java.events;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import main.java.Site;

public class Tweet extends TwitterEvent {

	private static final long serialVersionUID = 1L;

	private Site user;
	private String message;
	private DateTime time;
	
	public Tweet(Site user, String message, DateTime time) {
		this.eventType = "tweet";
		this.user = user;
		this.message = message;
		this.time = time.toDateTime(DateTimeZone.UTC);
	}
	public Tweet() {}
	
	/* Getters */
	public Site getUser() {
		return this.user;
	}
	public String getMessage() {
		return this.message;
	}
	public DateTime getTime() {
		return this.time;
	}
	
	/* Setters */
	public void setUser(Site user) {
		this.user = user;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setTime(DateTime time) {
		// make sure to store all times as UTC to avoid timezone issues
		this.time = time.toDateTime(DateTimeZone.UTC);
	}
	
}
