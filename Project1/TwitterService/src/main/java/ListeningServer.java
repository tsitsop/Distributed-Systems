package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Math.*;

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
				System.out.println("We are waiting for tweet to be received");
				Socket clientSocket = serverSocket.accept();
				System.out.println("We have received a connection");

				// set up input streams
				inFromClient =  new ObjectInputStream(clientSocket.getInputStream());

				// read in the tweet
				System.out.println("We are waiting for the tweet to come through");
		        m = (TwitterMessage) inFromClient.readObject();
		        inFromClient.close();

		        // print the contents of the tweet
				System.out.println("We have received the tweet! it came from " + m.getTweet().getUser().getIp() + ":" + m.getTweet().getUser().getPort() + "and says the following:" );
				System.out.println(m.getTweet().getMessage());
				System.out.println("It was sent at " + m.getTweet().getTime());
				System.out.println("\n\nThe partial log looks like the following:\n" + m.getNp().toString());

				// create newEvents log
				ConcurrentHashMap<LogEvent, String> np = m.getNp();
				ConcurrentHashMap<LogEvent, String> newEvents = SiteVariables.getNP(np, vars.getMatrixClock(), vars.getMySite().getId(), vars.getNumProcesses());
				// truncate log
				// add newEvents to log
				
				// update the dictionary with block/unblock events
				// check block/unblocks in NP and adjust accordingly
				
				// update our entire matrix clockmySite
					for(int k = 0,l=0;k<vars.getNumProcesses() && l<vars.getNumProcesses();k++,l++)
					{
						vars.setMatrixClockValue(vars.getMatrixClock(), vars.getNumProcesses(), vars.getMySite().getId(), k, 
								(Math.max(vars.getMatrixClockValue(vars.getMatrixClock(),vars.getNumProcesses(),vars.getMySite().getId(),k),
							vars.getMatrixClockValue(m.getMatrixClock(),vars.getNumProcesses(),m.getTweet().getUser().getId(),k))));

						vars.setMatrixClockValue(vars.getMatrixClock(),vars.getNumProcesses(),k,l,(Math.max(
							vars.getMatrixClockValue(vars.getMatrixClock(),vars.getNumProcesses(),k,l),
							vars.getMatrixClockValue(m.getMatrixClock(),vars.getNumProcesses(),k,l))));
					}
					/*	for	k=1…N
										Tj (j,k)	=	max	(	Tj(j,k)	,	Ti(i,k)	)
							for	k=1…N,	l	=	1…N
										Tj (k,l)	=	max	(	Tj (k,l)	,	Ti(k,l)	)*/
				

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
