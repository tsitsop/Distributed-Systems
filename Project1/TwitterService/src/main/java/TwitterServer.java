package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
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
		ConcurrentHashMap<LogEvent, String> np = new ConcurrentHashMap<>();

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

				//The case of a view, log, or dict command
				if (o.getClass().equals(String.class)) {

          //if dict, print blocks in dictionary
          if(o.equals("dict"))
          {
            for (Block b : vars.getDictionary().keySet())
            {
              System.out.println("("+b.getTime()+") "+"Block: "+b.getBlocker().getName()+" - "+b.getBlockee());
            }
            continue;
          }

          //List needs to be sorted to diplay tweets in order
          ArrayList<LogEvent> sortedList = new ArrayList<LogEvent>(vars.getPartialLog().keySet()) ;
          Collections.sort(sortedList, new Comparator<LogEvent>()
          {
            @Override
            public int compare(LogEvent e1, LogEvent e2){
              return Long.compare(e1.getEvent().getTime().getMillis(),e2.getEvent().getTime().getMillis());
            }
          });

          for (LogEvent le: sortedList)
          {
            //gets TwitterEvent out of Log Event
            TwitterEvent te = le.getEvent();

            //if event is tweet
            if (te.getEventType().compareTo("tweet") == 0)
            {
              Tweet t = (Tweet)te;
            	// add blocked case, Log command prints regardless of blocked status
              boolean canView = false;
              if (!vars.hasBlocked(t.getUser().getName(),vars.getMySite().getName())||o.equals("log"))
              {
                canView = true;
              }
              if(canView)
              {
                System.out.println("("+t.getTime()+") "+"Tweet: "+t.getUser().getName()+" - "+t.getMessage());
              }
            }

            if (o.equals("log"))
            {
              //log prints block and unblock events in
              if (te.getEventType().compareTo("block") == 0)
              {
                Block b = (Block)te;
                System.out.println("("+b.getTime()+") "+"Block: "+b.getBlocker().getName()+" - "+b.getBlockee());
              }
              else if (te.getEventType().compareTo("unblock") == 0)
              {
                Unblock u = (Unblock)te;
                System.out.println("("+u.getTime()+") "+"Unblock: "+u.getUnblocker().getName()+" - "+u.getUnblockee());
              }
            }

          }
          continue;
				}

				// if it wasn't any of the above events, we know it was a TwitterEvent
				e = (TwitterEvent) o;


				if (e.getEventType().compareTo("tweet") == 0) {

  				// tick clocks, add the event to the partial log
  				vars.tickClock(vars.getMySite().getId());
  				vars.addToLog(new LogEvent(vars.getMySite().getId(), vars.getLocalClock(),e));

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
						np = SiteVariables.getNP(vars.getPartialLog(), vars.getMatrixClock(), i, sites.size());
						// create a TweetClient thread to send TwitterMessage(tweet,matrixClock,NP)
						TwitterMessage message = new TwitterMessage((Tweet) e, vars.getMatrixClock(), np);
						TweetClient tc = new TweetClient(message, sites.get(i));
						tc.start();
					}
				} else { // this means we are either blocking or unblocking.
		          if (e.getEventType().compareTo("block") == 0) {
						      Block be = (Block) e;
						      // try adding to dictionary. If it exists or users invalid, function returns false and error message printed
                  if (!userExists(be.getBlockee())||!userExists(be.getBlocker().getName()))
                  {
                    System.err.println("One or more users does not exist");
                    continue;
                  }
						      if(!vars.addToDictionary(be)) {
							       System.err.println("You already blocked " + be.getBlockee());
                     continue;
						      }

         				// tick clocks, add the event to the partial log (doesnt execute if command is invalid)
        				vars.tickClock(vars.getMySite().getId());
        				vars.addToLog(new LogEvent(vars.getMySite().getId(), vars.getLocalClock(),e));

					     } else {
						      Unblock ue = (Unblock) e;
						      // try removing from dictionary. If block doens't exist or users invalid, function returns false and error printed
                  if (!userExists(ue.getUnblocker().getName())||!userExists(ue.getUnblockee()))
                  {
                    System.err.println("One or more users does not exist");
                    continue;
                  }
						      if (!vars.removeFromDictionary(ue)) {
							       System.err.println("You never blocked " + ue.getUnblockee());
                     continue;
						      }

          				// tick clocks, add the event to the partial log (doesnt execute if command is invalid)
          				vars.tickClock(vars.getMySite().getId());
          				vars.addToLog(new LogEvent(vars.getMySite().getId(), vars.getLocalClock(),e));

					     }
					// store our site variables to disk to maintain memory (includes blocking/unblocking)
					UtilityFunctions.writeVars(vars);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}


  private boolean userExists(String n)
  {
    for (Site s : sites)
    {
      if (s.getName().equals(n))
      {
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
