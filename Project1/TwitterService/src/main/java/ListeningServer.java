package main.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import main.java.events.Tweet;

public class ListeningServer extends Thread{
	private Site mySite;
	private ServerSocket serverSocket;

	public ListeningServer(Site site) throws IOException {
		this.mySite = site;
		this.serverSocket = new ServerSocket(site.port);
	}

	public void run() {
		Tweet tweet;

		while (true) {
			try {
				// wait for a tweet to come in
				System.out.println("We are waiting for tweet to be received");
				Socket clientSocket = serverSocket.accept();
				System.out.println("We have received a connection");

				// set up input streams
				ObjectInputStream inFromClient =  new ObjectInputStream(clientSocket.getInputStream());

				// read in the tweet
				System.out.println("We are waiting for the tweet to come through");
		        tweet = (Tweet) inFromClient.readObject();
		        inFromClient.close();

		        // print the contents of the tweet
				System.out.println("We have received the tweet! it came from " + tweet.getUser().ip + ":" + tweet.getUser().port + "and says the following:" );
				System.out.println(tweet.getMessage());
				System.out.println("It was sent at " + tweet.getTime() + "UTC");

				

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
