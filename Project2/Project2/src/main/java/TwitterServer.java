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
						for (Tweet t : vars.getTimeline()) {
							System.out.println(t.toString());
						}
					} else if (o.equals("log")) {
						vars.printWriteAheadLog();
					}

					System.out.println();
					continue;
				}

				// if it wasn't any of the above events, we know it was a TwitterEvent
				e = (TwitterEvent) o;

				if (e.getEventType().compareTo("block") == 0) {
					if (vars.isBlocked(((Block) e).getBlockee())) {
						System.out.println("This user is already blocked");
						continue;
					}
					if (((Block)e).getBlockee().equals(vars.getMySite().getName()))
					{
						System.out.println("Hey, can't block yourself!");
						continue;
					}
					String n = ((Block)e).getBlockee();
					boolean valid = false;
					for (Site s : sites)
					{
						if (s.getName().equals(n))
						{
							valid = true;
							break;
						}
					}
					if (!valid)
					{
						System.out.println("This user does not exist");
						continue;
					}
				} else if (e.getEventType().compareTo("unblock") == 0) {
					if (!vars.isBlocked(((Unblock) e).getUnblockee())) {
						System.out.println("This user is not blocked");
						continue;
					}
					if (((Unblock) e).getUnblockee().equals(vars.getMySite().getName()))
					{
						System.out.println("Hey, can't unblock yourself!");
						continue;
					}
				}
				
				PaxosMessage message;
				
				// declare initial synodValues. since it is first
				SynodValues synodValues = new SynodValues();
				
				
				// if you are leader, can skip prepare promise
				if (vars.getLogSize() != 0) {
					if (vars.getPaxVal(vars.getLogSize()-1).getLeader() == vars.getMySite().getId()) {
						// create Accept message with proposal number 
						message = new Accept(vars.getMySite(), vars.getLogSize(), 1, e);
						
						SendMessageThread mt = new SendMessageThread(vars, message, sites);
						mt.start();
						continue;
					}
				}

				// if you aren't the leader, do this
				// update Paxos log
				synodValues.setMyProposal(e);
				synodValues.setMyProposalNum(1);
				vars.modifyPaxosValues(vars.getLogSize(), synodValues);

				
				// create Prepare message with initial proposal number 1
				message = new Prepare(vars.getMySite(), vars.getLogSize()-1, 1);
				

				
				SendMessageThread dmt = new SendMessageThread(vars, message, sites);
				dmt.start();
			
				
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
