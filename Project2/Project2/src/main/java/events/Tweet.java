package main.java.events;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import main.java.Site;

public class Tweet extends TwitterEvent {

	private static final long serialVersionUID = 1L;

	private Site user;
	private String message;

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

	/* Setters */
	public void setUser(Site user) {
		this.user = user;
	}
	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public String toString() {
		return "(" + time.toString(DateTimeFormat.shortDateTime()) + ") " + user.getName() + ": " + message;
	}
}
