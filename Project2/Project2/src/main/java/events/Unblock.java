package main.java.events;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import main.java.Site;

public class Unblock extends TwitterEvent {

	private static final long serialVersionUID = 1L;

	private Site unblocker;
	private String unblockee;
	//private DateTime time;

	public Unblock(Site unblocker, String unblockee, DateTime time) {
		this.eventType = "unblock";
		this.unblocker = unblocker;
		this.unblockee = unblockee;
		this.time = time.toDateTime(DateTimeZone.UTC);
	}
	public Unblock() {}

	/* Getters */
	public Site getUnblocker() {
		return this.unblocker;
	}
	public String getUnblockee() {
		return this.unblockee;
	}

	/* Setters */
	public void setUnblocker(Site unblocker) {
		this.unblocker = unblocker;
	}
	public void setUnblockee(String unblockee) {
		this.unblockee = unblockee;
	}

	@Override
	public String toString() {
		return "(" + time.toString(DateTimeFormat.shortDateTime()) + ") " + unblocker.getName() + " unblocked " + unblockee;
	}

}
