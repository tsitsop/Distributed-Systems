package main.java;

import java.io.Serializable;

/**
 * This class represents a Site object. It has the basic information about
 * a site - its ip, port, numerical id, and real life name.
 * 
 * @author tsitsg
 *
 */
public class Site implements Serializable{
	private static final long serialVersionUID = 1L;

	private final String ip;
	private final int port;
	private final int id;
	private final String name;
	
	public Site(String ip, int port, int id, String name) {
	   this.ip = ip;
	   this.port = port;
	   this.id = id;
	   this.name = name;
	}

	
	/* Getters */
	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	
	/**
	 * Determine if two sites are the same
	 * 
	 * @param site	The site we want to compare to
	 * @return  True if the sites are equal, 
	 * 			False otherwise
	 */
	public boolean equals(Site site) {
		return this.id == site.getId();
	}
}
