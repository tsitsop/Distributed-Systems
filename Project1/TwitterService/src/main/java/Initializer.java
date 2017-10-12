package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Initializer {

	/**
	 * Parse the input file to get all sites.
	 * 
	 * @param in input file
	 * @return list of sites
	 */
	private static List<Site> parseInputFile(File in) {
		BufferedReader br;
	    String fLine;
	    String[] lParts;
	    List<Site> sites = new ArrayList<Site>();
	    try {
	        br = new BufferedReader(new FileReader(in));
	        // while there is stuff to read
	        while((fLine = br.readLine()) != null) {
	            // split into ip and port
	        	lParts = fLine.split(" ");
	        	
	        	// add a site to sites
	            sites.add(new Site(lParts[0], Integer.parseInt(lParts[1])));
	        }

		    br.close();
        } catch (Exception ex) {
        	System.err.println(ex);
        	return null;
        }
	    
	    return sites;
	}
	
	public static void main(String[] args) {
		String path = new File("src/main/resources/input.txt").getAbsolutePath();;
		File input = new File(path);
		
		// Make sure enough arguments
		if (args.length < 1) {
			System.err.println("Need a number as input");
			System.exit(1);
		} 
		
		// which line it is in the input file
		int id  = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.err.println("Argument" + args[0] + " must be an integer.");
			System.exit(1);
		}
		
		// get the list of sites
		List<Site> sites = parseInputFile(input);
		
		// get the site info for this server
		Site mySite = sites.get(id);
		
		
		
		
		
		
		// initialize or recover utility variables
		if (UtilityVariables.existVariables()) {
			//recover variables
		} else {
			// initialize variables
			// might be sites.size()-1?
			UtilityVariables utils = new UtilityVariables(sites.size());
		}
		// i haven't passed the variables anywhere - up to you.
		// i also haven't made it so they write to/from the files. also up to you.
		
		
		
		
		
		
		
		
		// now need to start 2 servers:
		//	1. a server to wait for user input and send tweets (TweetServer)
		//  2. a server to wait for tweets to come in (ListeningServer)
		try {
			Thread twitterServerThread = new TwitterServer(mySite, sites);
	        twitterServerThread.start();
	        Thread listeningServerThread = new ListeningServer(mySite);
	        listeningServerThread.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}	
		
		
}
