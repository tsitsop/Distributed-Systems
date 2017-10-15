package main.java;

import java.io.IOException;
import java.net.ConnectException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * TweetClient is the client that connects to all unblocked followers and
 * sends them tweets.
 *
 * @author tsitsg
 *
 */
public class TweetClient implements Runnable{
	private Thread thread;
	private TwitterMessage message;
	private Site dest;

	/**
	 * Public constructor
	 *
	 * @param message 	The tweet to send, along with the matrix clock and partial log
	 * @param dest 	The site the tweet is being sent to
	 */
	TweetClient(TwitterMessage message, Site dest) {
		this.message = message;
		this.dest = dest;
		//System.out.println("\n\nCreating thread to send tweet \"" + this.message.getTweet().getMessage() + "\" to " + dest.getIp() + ":" + dest.getPort());
	}

	/**
	 * Try to send our tweet to its destination
	 */
	public void run() {
		//System.out.println(message.getNp());

		try {
			// try to connect to destination
			//System.out.println("Trying to connect to " + dest.getIp() + ":" + dest.getPort());
			Socket client = new Socket(dest.getIp(), dest.getPort());
			//System.out.println("Successfully connected to " + dest.getIp() + ":" + dest.getPort());

			// create a stream to send tweet to the destination server
			OutputStream outToServer = client.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);

			// try to send our tweet
			//System.out.println("Trying to send tweet to " + dest.getIp() + ":" + dest.getPort());
			out.writeObject(message);
			//System.out.println("Successfully sent tweet to " + dest.getIp() + ":" + dest.getPort());

			client.close();
		} catch (ConnectException ce){
			return;
		} catch (IOException ie) {
			ie.printStackTrace();
		}

		//System.out.println("Thread that sent tweet \"" + this.message.getTweet().getMessage() + "\" to " +dest.getIp() + ":" + dest.getPort() + " exiting");
	}

	/**
	 * Start a thread if it hasn't been started
	 */
	public void start() {
		//System.out.println("Starting thread to send tweet \"" + this.message.getTweet().getMessage() + "\" to " + dest.getIp() + ":" + dest.getPort() + "\n\n");
		if (this.thread == null) {
			this.thread = new Thread(this);
			thread.start();
		}
	}

}
