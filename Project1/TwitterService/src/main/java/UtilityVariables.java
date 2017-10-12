package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.joda.time.DateTime;

import main.java.events.Tweet;
import main.java.events.TwitterEvent;

public class UtilityVariables {
	private ConcurrentHashMap<TwitterEvent, String> dictionary;
	private ConcurrentHashMap<TwitterEvent, String> partialLog;
	private int localClock;
	AtomicIntegerArray matrixClock;
	
	public UtilityVariables(int n) {
		this.dictionary = new ConcurrentHashMap<>();
		this.partialLog = new ConcurrentHashMap<>();
		this.localClock = 0;
		this.matrixClock = new AtomicIntegerArray(n*n);
		
		/*
		 * need to write each of these to its own file - serialize them to put them in
		 * will need a function that serializes and stores and another that returns the 
		 * deserialized form of them.
		 * 
		 * See the testSerialization below for how to do this
		 * 
		 * also need to write the existVariables function
		 */
	}
	public UtilityVariables() {	}
	
	/**
	 * tells us if this is fresh start or not.
	 * 
	 * @return true if files exist (recovering from failure), false otherwise
	 */
	public static boolean existVariables() {
		return false;
	}
	
	
	/**
	 * REMOVE EVENTUALLY
	 * test to make sure we can serialize and deserialize messages
	 */
	public static void testSerialization() {
		Tweet t = new Tweet(new Site("george", 10), "hello", new DateTime());
		
		String path = new File("src/main/resources/output.ser").getAbsolutePath();
		try {
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(t);
			out.close();
			fout.close();
		} catch(IOException i) {
			i.printStackTrace();
		}

		try {
	         FileInputStream fin = new FileInputStream(path);
	         ObjectInputStream in = new ObjectInputStream(fin);
	         t = (Tweet) in.readObject();
	         in.close();
	         fin.close();
	      } catch(IOException i) {
	         i.printStackTrace();
	         return;
	      } catch(ClassNotFoundException c) {
	         System.out.println("Employee class not found");
	         c.printStackTrace();
	         return;
	      }
		
		System.out.println(t.getEventType());
		System.out.println(t.getUser());
		System.out.println(t.getMessage());
		System.out.println(t.getTime());
	}
	
	
}
