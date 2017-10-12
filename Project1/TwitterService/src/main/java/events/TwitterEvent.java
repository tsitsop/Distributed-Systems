package main.java.events;

import java.io.Serializable;

public abstract class TwitterEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String eventType;

	public TwitterEvent() {}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	
}
