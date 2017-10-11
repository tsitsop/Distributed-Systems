package main.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {
	public static List<String> parseInput(String filename) {
		List<String> ips = new ArrayList<String>();
		String ip;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			try {
				while ((ip= reader.readLine()) != null) {
				  ips.add(ip);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		    try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    return ips;
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		List<String> ipsandports = parseInput("src/main/resources/hosts.txt");
		List<String> ips = new ArrayList<String>();
		List<Integer> ports = new ArrayList<Integer>();
		
		for (int i=0; i<ips.size(); i++) {
			String[] ipsplit = ipsandports.get(i).split(" ");
			ips.add(ipsplit[0]);
			ports.add(Integer.valueOf(ipsplit[1]));
		}
		
		
		
		
	}
}
