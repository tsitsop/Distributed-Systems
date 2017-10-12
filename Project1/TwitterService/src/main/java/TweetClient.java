package main.java;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import main.java.events.Tweet;

public class TweetClient implements Runnable{
	private Thread thread;
	private Tweet tweet;
	private Site dest;
	
	TweetClient(Tweet tweet, Site dest) {
		this.tweet = tweet;
		this.dest = dest;
		System.out.println("Creating thread to send tweet \"" + this.tweet.getMessage() + "\" to " + dest.ip + ":" + dest.port);
	}
	
	/**
	 * Try to send our tweet to its destination
	 */
	public void run() {
		try {
			// try to connect to destination
			System.out.println("Trying to connect to " + dest.ip + ":" + dest.port);
			Socket client = new Socket(dest.ip, dest.port);
			System.out.println("Successfully connected to " + dest.ip + ":" + dest.port);
			
			// create a stream to send tweet to the destination server
			OutputStream outToServer = client.getOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outToServer);
			
			// try to send our tweet
			System.out.println("Trying to send tweet to " + dest.ip + ":" + dest.port);
			out.writeObject(tweet);
			System.out.println("Successfully sent tweet to " + dest.ip + ":" + dest.port);
			
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Thread that sent tweet \"" + tweet.getMessage() + "\" to " + dest.ip + ":" + dest.port + " exiting");
	}
	
	/**
	 * Start a thread if it hasn't been started
	 */
	public void start() {
		System.out.println("Starting thread to send tweet \"" + tweet.getMessage() + "\" to " + dest.ip + ":" + dest.port);
		if (this.thread == null) {
			this.thread = new Thread(this);
			thread.start();
		}
	}
	
}
