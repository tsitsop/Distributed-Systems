package main.java;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

import main.java.messages.PaxosMessage;

/**
 * TweetClient is the client that connects to all unblocked followers and
 * sends them tweets.
 *
 * @author tsitsg
 *
 */
public class TweetClient implements Runnable{
	private Thread thread;
	private PaxosMessage message;
	private Site dest;

	/**
	 * Public constructor
	 *
	 * @param message 	The message to send
	 * @param dest 	The site the message is being sent to
	 */
	public TweetClient(PaxosMessage message, Site dest) {
		this.message = message;
		this.dest = dest;
	}

	/**
	 * Try to send our message to its destination
	 */
	public void run() {
		try {
			// try to connect to destination

			Socket client = new Socket(dest.getIp(), dest.getPort());


			// create a stream to send tweet to the destination server
			OutputStream outToServer = client.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);

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
