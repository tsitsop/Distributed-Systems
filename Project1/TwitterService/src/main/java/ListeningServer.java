package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collections;
import main.java.events.Block;
import main.java.events.Tweet;
import main.java.events.TwitterEvent;
import main.java.events.Unblock;

/**
 * This class represents the server that listens for incoming tweets.
 * When it receives a tweet it updates its dictionary, log, and
 * matrix clock as necessary.
 * @author tsitsg
 *
 */
public class ListeningServer extends Thread{
	private SiteVariables vars;
	private ServerSocket serverSocket;

	/**
	 * Constructor
	 * @param vars The SiteVariables object that has the log,
 	 * 				 dictionary, local clock, and matrix clock.
	 * @throws IOException	if an I/O error occurs when opening the socket
	 */
	public ListeningServer(SiteVariables vars) throws IOException {
		this.vars = vars;
		this.serverSocket = new ServerSocket(vars.getMySite().getPort());
	}

	public void run() {
		TwitterMessage m;
		ObjectInputStream inFromClient;

		while (true) {
			try {
				// wait for a tweet to come in
				//System.out.println("We are waiting for tweet to be received");
				Socket clientSocket = serverSocket.accept();
				//System.out.println("We have received a connection");

				// set up input streams
				inFromClient =  new ObjectInputStream(clientSocket.getInputStream());

				// read in the tweet
				//System.out.println("We are waiting for the tweet to come through");
		        m = (TwitterMessage) inFromClient.readObject();
		        inFromClient.close();

		        // print the contents of the tweet
				//System.out.println("We have received the tweet! it came from " + m.getTweet().getUser().getIp() + ":" + m.getTweet().getUser().getPort() + "and says the following:" );
				System.out.println("Recieved: "+m.getTweet().getMessage());
				//System.out.println("It was sent at " + m.getTweet().getTime());
				//System.out.println("\n\nThe partial log looks like the following:\n" + m.getNp().toString());

				//System.out.println(m.getNp());

				// create newEvents log
				ConcurrentHashMap<LogEvent, String> np = m.getNp();
				ConcurrentHashMap<LogEvent, String> newEvents = SiteVariables.getNP(np, vars.getMatrixClock(), vars.getMySite().getId(), vars.getNumProcesses());
				// truncate log




				// update the dictionary with block/unblock events
				ArrayList<LogEvent> sortedList = new ArrayList<LogEvent>(newEvents.keySet()) ;
				Collections.sort(sortedList, new Comparator<LogEvent>()
				{
					@Override
					public int compare(LogEvent e1, LogEvent e2){
						return Long.compare(e1.getEvent().getTime().getMillis(),e2.getEvent().getTime().getMillis());
					}
				});

					for(LogEvent le : sortedList)
					{
						if (le.getEvent().getEventType().equals("block"))
						{
							vars.addToDictionary((Block)le.getEvent());
						}
						else if (le.getEvent().getEventType().equals("unblock"))
						{
							vars.removeFromDictionary((Unblock)le.getEvent());
						}
					}
				// check block/unblocks in NP and adjust accordingly


				// update our entire matrix clockmySite
				int numP = vars.getNumProcesses();
				int myID = vars.getMySite().getId();
				int messID = m.getTweet().getUser().getId();
				//
				//System.out.println("Clock ------- ");
				//vars.printMatrixClock(vars.getMatrixClock(),numP);
				//System.out.println("--------------");


				//System.out.println("Message Clock-");
				//vars.printMatrixClock(m.getMatrixClock(),numP);
				//System.out.println("--------------");

					for(int k = 0;k<vars.getNumProcesses();k++)
					{
						vars.setMatrixClockValue(vars.getMatrixClock(),numP, myID, k,
								(Math.max(vars.getMatrixClockValue(vars.getMatrixClock(),numP, myID,k),
							vars.getMatrixClockValue(m.getMatrixClock(),numP,messID,k))));


					}
					for(int k = 0;k<vars.getNumProcesses();k++)
					{
						for (int l=0;l<numP;l++)
						{
							vars.setMatrixClockValue(vars.getMatrixClock(),numP,k,l,(Math.max(
								vars.getMatrixClockValue(vars.getMatrixClock(),numP,k,l),
								vars.getMatrixClockValue(m.getMatrixClock(),numP,k,l))));
						}
					}
					//System.out.println("New Clock ----");
					//vars.printMatrixClock(vars.getMatrixClock(),numP);
					//System.out.println("--------------");

					/*	for	k=1…N
										Tj (j,k)	=	max	(	Tj(j,k)	,	Ti(i,k)	)
							for	k=1…N,	l	=	1…N
										Tj (k,l)	=	max	(	Tj (k,l)	,	Ti(k,l)	)*/

			// add newEvents to log
			vars.getPartialLog().putAll(newEvents);
											//

			//Truncate log
			//for each in log
			boolean to_remove;
			Iterator<LogEvent> it = vars.getPartialLog().keySet().iterator();
			while (it.hasNext())
			{
				LogEvent le = it.next();
				//if it's id is in each row, delete it
				to_remove = true;
				if (le.getEvent().getEventType().equals("tweet"))
				{
					continue;
				}
				for (int i =0;i<vars.getNumProcesses();i++)
				{
					if (!SiteVariables.hasRec(vars.getMatrixClock(),le,i,vars.getNumProcesses()))
					{
						//System.out.println("not remove "+le);
						to_remove = false;
						break;
					}
				}

				if (to_remove)
				{
					//System.out.println("Will remove "+le);
				  //vars.removeFromLog(le);
					vars.getPartialLog().remove(le);
				}

			}

			//Update recovery file
			UtilityFunctions.writeVars(vars);

			} catch (IOException e) {
				e.printStackTrace();
			} catch(ClassNotFoundException c) {
		         System.out.println("Tweet class not found");
		         c.printStackTrace();
		         return;
		      }
		}
	}


}
