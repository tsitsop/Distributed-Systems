package main.java;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import main.java.events.TwitterEvent;

public class LogEvent {
  private int id;
  private int localTime;
  TwitterEvent event;
  LogEvent(int i,int l, TwitterEvent e)
  {
    id = i;
    localTime = l;
    event = e;
  }
}
