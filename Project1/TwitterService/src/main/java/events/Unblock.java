package main.java.events;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import main.java.Site;

public class Unblock extends TwitterEvent {

	private static final long serialVersionUID = 1L;

	private Site unblocker;
	private String unblockee;
	private DateTime time;
	
	
	public Unblock(Site unblocker, String unblockee, DateTime time) {
		this.eventType = "unblock";
		this.unblocker = unblocker;
		this.unblockee = unblockee;
		this.time = time.toDateTime(DateTimeZone.UTC);
	}
	public Unblock() {}
	
	/* Getters */
	public Site getunblocker() {
		return this.unblocker;
	}
	public String getunblockee() {
		return this.unblockee;
	}
	public DateTime getTime() {
		return this.time;
	}
	
	/* Setters */
	public void setunblocker(Site unblocker) {
		this.unblocker = unblocker;
	}
	public void setunblockee(String unblockee) {
		this.unblockee = unblockee;
	}
	public void setTime(DateTime time) {
		// make sure to store all times as UTC to avoid timezone issues
		this.time = time.toDateTime(DateTimeZone.UTC);
	}
	
}
