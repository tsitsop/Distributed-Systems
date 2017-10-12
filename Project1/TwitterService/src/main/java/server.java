//package main.java;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.joda.time.DateTime;
//
//import main.java.events.Tweet;
//
//public class server {
//	/**
//	 * Parse the input file to get all sites.
//	 * 
//	 * @param in input file
//	 * @return list of sites
//	 */
//	private static List<Site> parseInputFile(File in) {
//		BufferedReader br;
//	    String fLine;
//	    String[] lParts;
//	    List<Site> sites = new ArrayList<Site>();
//	    try {
//	        br = new BufferedReader(new FileReader(in));
//	        // while there is stuff to read
//	        while((fLine = br.readLine()) != null) {
//	            // split into ip and port
//	        	lParts = fLine.split(" ");
//	        	
//	        	// add a site to sites
//	            sites.add(new Site(lParts[0], Integer.parseInt(lParts[1])));
//	        }
//
//		    br.close();
//        } catch (Exception ex) {
//        	System.err.println(ex);
//        	return null;
//        }
//	    
//	    return sites;
//	}
//	
//	public static void main(String[] args) {
//		File input = null;
//		
//		// Make sure filename argument is valid
//		if (args.length < 2) {
//			System.err.println("Need a file name as input");
//			System.exit(1);
//		} else {
//			String path = new File(args[1]).getAbsolutePath();
//			input = new File(path);
//			if (!input.exists()) {
//				System.err.println("The file doesn't exist!");
//				System.exit(1);
//			}
//		}
//		
//		// get the list of sites
//		List<Site> sites = parseInputFile(input);
//		
//		
//		
//		
//		Tweet t = new Tweet("george", "hello", new DateTime());
//		
//		
//		String path = new File("src/main/resources/output.ser").getAbsolutePath();
//		try {
//			FileOutputStream fout = new FileOutputStream(path);
//			ObjectOutputStream out = new ObjectOutputStream(fout);
//			out.writeObject(t);
//			out.close();
//			fout.close();
//		} catch(IOException i) {
//			i.printStackTrace();
//		}
//
//		try {
//	         FileInputStream fin = new FileInputStream(path);
//	         ObjectInputStream in = new ObjectInputStream(fin);
//	         t = (Tweet) in.readObject();
//	         in.close();
//	         fin.close();
//	      } catch(IOException i) {
//	         i.printStackTrace();
//	         return;
//	      } catch(ClassNotFoundException c) {
//	         System.out.println("Employee class not found");
//	         c.printStackTrace();
//	         return;
//	      }
//		
//		System.out.println(t.getEventType());
//		System.out.println(t.getUser());
//		System.out.println(t.getMessage());
//		System.out.println(t.getTime());
//	}
//}