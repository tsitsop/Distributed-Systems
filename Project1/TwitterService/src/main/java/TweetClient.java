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
	}

	/**
	 * Try to send our tweet to its destination
	 */
	public void run() {


		try {
			// try to connect to destination

			Socket client = new Socket(dest.getIp(), dest.getPort());


			// create a stream to send tweet to the destination server
			OutputStream outToServer = client.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);

			// try to send our tweet

			out.writeObject(message);


			client.close();
		} catch (ConnectException ce){
			//Silently fail if other side is down, will send next time;
			return;
		} catch (IOException ie) {
			ie.printStackTrace();
		}


	}

	/**
	 * Start a thread if it hasn't been started
	 */
	public void start() {
		if (this.thread == null) {
			this.thread = new Thread(this);
			thread.start();
		}
	}

}
