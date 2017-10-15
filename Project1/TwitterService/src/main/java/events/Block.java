package main.java.events;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import main.java.Site;

public class Block extends TwitterEvent {

	private static final long serialVersionUID = 1L;

	private Site blocker;
	private String blockee;
	//private DateTime time;

	public Block(Site blocker, String blockee, DateTime time) {
		this.eventType = "block";
		this.blocker = blocker;
		this.blockee = blockee;
		this.time = time.toDateTime(DateTimeZone.UTC);
	}
	public Block() {}

	/* Getters */
	public Site getBlocker() {
		return this.blocker;
	}
	public String getBlockee() {
		return this.blockee;
	}
	/*public DateTime getTime() {
		return this.time;
	}*/

	/* Setters */
	public void setBlocker(Site blocker) {
		this.blocker = blocker;
	}
	public void setBlockee(String blockee) {
		this.blockee = blockee;
	}
	/*public void setTime(DateTime time) {
		// make sure to store all times as UTC to avoid timezone issues
		this.time = time.toDateTime(DateTimeZone.UTC);
	}*/

}
