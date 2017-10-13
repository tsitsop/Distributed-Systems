package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import main.java.events.Block;
import main.java.events.Tweet;
import main.java.events.TwitterEvent;
import main.java.events.Unblock;

public class TwitterServer extends Thread {
	private Site mySite;
	private List<Site> sites;
	private ArrayList<LogEvent> log;
 	private UtilityVariables vars;

	public TwitterServer(Site site, List<Site> sites, UtilityVariables u) throws IOException {
		this.mySite = site;
		this.sites = sites;
		this.vars = u;
	}

	/**
	 * Listen for user input and react accordingly
	 */
	public void run() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String command;
		TwitterEvent e;
		Object o;

		while(true) {
			try {
				// listen for user input
				System.out.println("Waiting for you to enter a command: ");
				command = inFromUser.readLine();

				// determine command the user requested
				o = parseCommand(command);

				// if it was invalid command, ask for another command
				if (o == null) {
					continue;
				}

				// if it was a view command,
				if (o.getClass().equals(String.class)) {
					//  do nothing for now
				}

				e = (TwitterEvent) o;

				// if the command  is an event, do what you should do
				if (e.getEventType().compareTo("tweet") == 0) {

					vars.tickClock();
					log.add(new LogEvent(mySite.id,vars.getClock(),e));

					for (int i=0; i< sites.size(); i++) {
						// We don't want to send a tweet to ourselves
						if (mySite.equals(sites.get(i))) {
							continue;
						}
						// create a TweetClient thread to send our tweet to the destination
						TweetClient tc = new TweetClient((Tweet)e, sites.get(i));
						tc.start();
					}
				} else if (e.getEventType().compareTo("block") == 0) {
					// do block stuff
				} else {
					// do unblock stuff
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}



	/**
	 * Parse the command that was entered by the user
	 *
	 * @param command the string command
	 * @return a TwitterEvent representing that command
	 */
	private Object parseCommand(String command) {
		System.out.println("command was " + command);
		String[] splitCommand = command.split(" ", 2);
		TwitterEvent e;

		if (splitCommand[0].compareTo("view") == 0) {
			return "view";
		} else if (splitCommand[0].compareTo("tweet") == 0) {
			e = new Tweet(this.mySite, splitCommand[1], new DateTime());
		} else if (splitCommand[0].compareTo("block") == 0) {
			e = new Block(this.mySite, splitCommand[1], new DateTime());
		} else if (splitCommand[0].compareTo("unblock") == 0) {
			e = new Unblock(this.mySite, splitCommand[1], new DateTime());
		} else {
			System.out.println("Invalid command: try again");
			return null;
		}

		return e;
	}

}
