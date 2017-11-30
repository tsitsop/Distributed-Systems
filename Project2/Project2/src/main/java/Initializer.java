package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will initialize the site, creating the TwitterServer
 * and ListeningServer. It will also initialize the site variables.
 *
 * @author tsitsg
 *
 */
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
		int id = 0;
	    List<Site> sites = new ArrayList<Site>();

	    try {
	        br = new BufferedReader(new FileReader(in));
	        // while there is stuff to read
	        while((fLine = br.readLine()) != null) {
	            // split into ip and port
	        	lParts = fLine.split(" ");

	        	// add a site to sites
	            sites.add(new Site(lParts[0], Integer.parseInt(lParts[1]), id, lParts[2]));
				id += 1;
	        }

		    br.close();
        } catch (Exception ex) {
        	System.err.println(ex);
        	return null;
        }

	    return sites;
	}


	public static void main(String[] args) {
		
		// The path to the configuration file
		String path = new File("src/main/resources/input.txt").getAbsolutePath();;
		File input = new File(path);

		// Make sure enough arguments (need the id of this site)
		if (args.length < 1) {
			System.err.println("Need a number as input");
			System.exit(1);
		}

		// Get the site id
		int id  = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.err.println("Argument" + args[0] + " must be an integer.");
			System.exit(1);
		}

		// Get the list of sites
		List<Site> sites = parseInputFile(input);

		// Get the site info for this server
		Site mySite = sites.get(id);

		// initialize or recover site variables
		SiteVariables vars = null;
		if (UtilityFunctions.freshStart(mySite.getName())) {
			vars = new SiteVariables(sites.size(), mySite);
		} else {
			System.out.println("Recovering from a failure!");
			// if kill program before entering input, file is empty
			if (UtilityFunctions.isEmpty(mySite.getName())) { 
				vars = new SiteVariables(sites.size(), mySite);
			} else {
				vars = UtilityFunctions.readVars(mySite);
			}
			
			// run full Paxos for each log entry
		}

		// Start 2 servers:
		// 1. a server to wait for user input and send tweets (TwitterServer)
		// 2. a server to wait for tweets to come in (ListeningServer)
		try {
			Thread twitterServerThread = new TwitterServer(vars, sites);
	        twitterServerThread.start();

	        Thread listeningServerThread = new ListeningServer(vars, sites);
	        listeningServerThread.start();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}


}
