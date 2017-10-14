package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;

import main.java.events.Block;
import main.java.events.Tweet;
import main.java.events.TwitterEvent;
import main.java.events.Unblock;

/**
 * This is the server that listens for user input. Once it receives input,
 * it will follow the command or declare it invalid and ask for another.
 * 
 * @author tsitsg
 *
 */
public class TwitterServer extends Thread {
 	private SiteVariables vars;
	private List<Site> sites;

 	/**
 	 * The public constructor.
 	 * 
 	 * @param sites	The A list of all other sites.
 	 * @param u		The SiteVariables object that has the log,
 	 * 				 dictionary, local clock, and matrix clock.
 	 */
	public TwitterServer(SiteVariables u, List<Site> sites) {
		this.vars = u;
		this.sites = sites;
	}

	/**
	 * Listen for user input and react accordingly
	 */
	public void run() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String command;
		TwitterEvent e;
		Object o;
		ConcurrentHashMap<LogEvent, String> np = new ConcurrentHashMap<>();

		while(true) {
			try {
				// listen for user input
				System.out.println("Waiting for you to enter a command: ");
				command = inFromUser.readLine();

				
				
				//////////////////////////////////////////////////////////////////////////////////////////////
				// Need to add functionality for view, view log, view dictionary
				//////////////////////////////////////////////////////////////////////////////////////////////
				
				// determine command the user requested
				o = parseCommand(command);

				// if it was invalid command, ask for another command
				if (o == null) {
					continue;
				}
				
				// if it was a view command, print the tweets
				if (o.getClass().equals(String.class)) {
					//  do nothing for now
				}
				
				// if it wasn't any of the above events, we know it was a TwitterEvent
				e = (TwitterEvent) o;
				
				// tick clocks, add the event to the partial log
				vars.tickClock(vars.getMySite().getId());
				vars.addToLog(new LogEvent(vars.getMySite().getId(), vars.getLocalClock(),e));
				
				
				if (e.getEventType().compareTo("tweet") == 0) {
					// store our site variables to disk to maintain memory
					UtilityFunctions.writeVars(vars);
					
					// Send tweet to other users
					for (int i=0; i< sites.size(); i++) {
						// Don't send tweet to self or to blocked followers
						if (vars.getMySite().equals(sites.get(i))) {
							continue;
						} else if (vars.isBlocked(sites.get(i))) {
							continue;
						}
						
						// create NP
						np.clear();
						np = SiteVariables.getNP(vars.getPartialLog(), vars.getMatrixClock(), i, sites.size());
						
						// create a TweetClient thread to send TwitterMessage(tweet,matrixClock,NP)
						TwitterMessage message = new TwitterMessage((Tweet) e, vars.getMatrixClock(), np);
						TweetClient tc = new TweetClient(message, sites.get(i));
						tc.start();
					}
				} else { // this means we are either blocking or unblocking. 
					// Add check to make sure user exists
					
					if (e.getEventType().compareTo("block") == 0) {
						Block be = (Block) e;
						// try adding to dictionary. If it exists, function returns false and error message printed
						if (!vars.addToDictionary(be)) {
							System.err.println("You already blocked " + be.getBlockee());
						} 
					} else {
						Unblock ue = (Unblock) e;
						// try removing from dictionary. If block doens't exist, function returns false and error printed
						if (!vars.removeFromDictionary(ue)) {
							System.err.println("You never blocked " + ue.getUnblockee());
						}
					}
					
					// store our site variables to disk to maintain memory (includes blocking/unblocking)
					UtilityFunctions.writeVars(vars);
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
			e = new Tweet(this.vars.getMySite(), splitCommand[1], new DateTime());
		} else if (splitCommand[0].compareTo("block") == 0) {
			e = new Block(this.vars.getMySite(), splitCommand[1], new DateTime());
		} else if (splitCommand[0].compareTo("unblock") == 0) {
			e = new Unblock(this.vars.getMySite(), splitCommand[1], new DateTime());
		} else {
			System.out.println("Invalid command: try again");
			return null;
		}

		return e;
	}

}
