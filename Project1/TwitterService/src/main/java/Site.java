package main.java;

import java.io.Serializable;

public class Site implements Serializable{

	private static final long serialVersionUID = 1L;

	public final String ip;
	public final int port;
	
	public Site(String ip, int port) {
	   this.ip = ip;
	   this.port = port;
	 }
	
	public boolean equals(Site site) {
		return this.ip.equals(site.ip) && this.port == site.port;
	}
}
