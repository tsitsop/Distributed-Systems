package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.joda.time.DateTime;

import main.java.events.Block;
import main.java.events.Tweet;
import main.java.events.TwitterEvent;
import main.java.events.Unblock;
import main.java.messages.Accept;
import main.java.messages.PaxosMessage;
import main.java.messages.Prepare;

/**
 * This is the server that listens for user input. Once it receives input,
 * it will follow the command or declare it invalid and ask for another.
 *
 * @author tsitsg, zrmaurer
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

				//The case of a view or log command
				if (o.getClass().equals(String.class)) {
					if (o.equals("view")) {
						 /*
						   for each log entry:
							 if log entry is tweet
								if tweet from user who blocked you
									continue
								else
									print user, tweet, time
							once printed everything: continue
						*/
//						 for (LogEvent le: sortedList) {
//						    	//gets TwitterEvent out of Log Event
//						    	TwitterEvent te = le.getEvent();
//
//						    	//if event is tweet
//						    	if (te.getEventType().compareTo("tweet") == 0) {
//						    		Tweet t = (Tweet)te;
//						    		// add blocked case, Log command prints regardless of blocked status
//						    		boolean canView = false;
//						    		if (!vars.hasBlocked(t.getUser().getName(), vars.getMySite().getName()) || o.equals("log")) {
//						    			canView = true;
//						    		}
//		  
//						    		if (canView) {
//						    			System.out.println("("+t.getTime()+") "+"Tweet: "+t.getUser().getName()+" - "+t.getMessage());
//						    		}
//						    	}
//
//						    	if (o.equals("log")) {
//						    		//log prints block and unblock events in
//						    		if (te.getEventType().compareTo("block") == 0) {
//						    			Block b = (Block)te;
//						    			System.out.println("("+b.getTime()+") "+"Block: "+b.getBlocker().getName()+" - "+b.getBlockee());
//						    		} else if (te.getEventType().compareTo("unblock") == 0) {
//						    			Unblock u = (Unblock)te;
//						    			System.out.println("("+u.getTime()+") "+"Unblock: "+u.getUnblocker().getName()+" - "+u.getUnblockee());
//						    		}
//						    	}
//						    }
					} else if (o.equals("log")) {
						// print each log entry
					}
					
					continue;
				}

				// if it wasn't any of the above events, we know it was a TwitterEvent
				e = (TwitterEvent) o;

				if (e.getEventType().compareTo("block") == 0) {
					// check if block is valid
				} else if (e.getEventType().compareTo("unblock") == 0) {
					// check if unblock is valid
				}
				
				PaxosMessage message;
				TweetClient tc;
				
				// declare initial synodValues. since it is first
				SynodValues synodValues = new SynodValues();
				
				
				// if you are leader, can skip prepare promise
				if (vars.getLogSize() != 0) {
					if (vars.getPaxosValues().get(vars.getLogSize()-1).getLeader() == vars.getMySite().getId()) {
						System.out.println("yo");
						// create Accept message with proposal number 1
						message = new Accept(vars.getMySite(), vars.getLogSize(), 1, e);
						
						// send message to all followers
						for (Site site : sites) {
							tc = new TweetClient(message, site);
							tc.start();
						}
					}
				}  else {
					synodValues.setMyProposal(e);
					// create Prepare message with initial proposal number 1
					message = new Prepare(vars.getMySite(), vars.getLogSize(), 1);
					
					// send message to all followers
					for (Site site : sites) {
						System.out.println("sending prepare to" + site.toString() + " sites");

						tc = new TweetClient(message, site);
						tc.start();
					}
				}
				
				// store our site variables to disk to maintain memory (includes blocking/unblocking)
				UtilityFunctions.writeVars(vars);

			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}


	/**
	 * Checks to makes sure the user is valid
	 * 
	 * @param n user name
	 * @return true if user exists, else false
	 */
	private boolean userExists(String n) {
		for (Site s : sites) {
	    	if (s.getName().equals(n)) {
	    		return true;
	    	}
    	}
		
		return false;
	}
	
	/**
	 * Parse the command that was entered by the user
	 *
	 * @param command the string command
	 * @return a TwitterEvent representing that command
	 */
	private Object parseCommand(String command) {
		// System.out.println("command was " + command);
		String[] splitCommand = command.split(" ", 2);
		TwitterEvent e;

		if (splitCommand[0].compareTo("view") == 0) {
			return "view";
		} else if (splitCommand[0].compareTo("viewlog") == 0){
			return "log";
		} else if (splitCommand[0].compareTo("viewdict")==0){
			return "dict";
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
