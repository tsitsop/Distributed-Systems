package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import main.java.messages.Accept;
import main.java.messages.Ack;
import main.java.messages.Commit;
import main.java.messages.PaxosMessage;
import main.java.messages.Prepare;
import main.java.messages.Promise;

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
	private List<Site> sites;

	/**
	 * Constructor
	 * @param vars The SiteVariables object that has the log,
 	 * 				 dictionary, local clock, and matrix clock.
	 * @throws IOException	if an I/O error occurs when opening the socket
	 */
	public ListeningServer(SiteVariables vars, List<Site> sites) throws IOException {
		this.vars = vars;
		this.serverSocket = new ServerSocket(vars.getMySite().getPort());
		this.sites = sites;
	}

	public void run() {
		PaxosMessage m;
		ObjectInputStream inFromClient;

		while (true) {
			try {
				// wait for a message to come in
				Socket clientSocket = serverSocket.accept();

				// set up input streams
				inFromClient =  new ObjectInputStream(clientSocket.getInputStream());

				// read in the message
				m = (PaxosMessage) inFromClient.readObject();
		        inFromClient.close();

				System.out.println("got a message!!");

		        // determine message type and run corresponding function
		        if (m instanceof Prepare) {
		        	System.out.println("Received Prepare message from " + m.getSender());
		        	Prepare pm = (Prepare) m;
		        	pm.onReceive(vars);
		        } else if (m instanceof Promise) {
		        	System.out.println("Received Propose message from " + m.getSender());
		        	Promise pm = (Promise) m;
		        	pm.onReceive(vars, sites);
		        } else if (m instanceof Accept) {
		        	System.out.println("Received Accept message from " + m.getSender());
		        	Accept am = (Accept) m;
		        	am.onReceive(vars);
		        } else if (m instanceof Ack) {
		        	System.out.println("Received Ack message from " + m.getSender());
		        	Ack am = (Ack) m;
		        	am.onReceive(vars);
		        } else if (m instanceof Commit) {
		        	System.out.println("Received Commit message from " + m.getSender());
		        	Commit cm = (Commit) m;
		        	cm.onReceive(vars);
		        }
			} catch (IOException e) {
				e.printStackTrace();
			} catch(ClassNotFoundException c) {
		         System.out.println("Message class not found");
		         c.printStackTrace();
		         return;
		      }
		}
	}


}
