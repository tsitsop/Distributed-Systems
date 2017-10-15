package main.java.events;

import java.io.Serializable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public abstract class TwitterEvent implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected String eventType;

	protected DateTime time;

	public TwitterEvent() {}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public DateTime getTime() {
		// return the local time
		return this.time.withZone(DateTimeZone.getDefault());
	}

	public void setTime(DateTime time) {
		// make sure to store all times as UTC to avoid timezone issues
		this.time = time.toDateTime(DateTimeZone.UTC);
	}

}
