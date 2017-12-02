package main.java;

import java.util.List;

import main.java.messages.Accept;
import main.java.messages.Ack;
import main.java.messages.Commit;
import main.java.messages.PaxosMessage;
import main.java.messages.Prepare;
import main.java.messages.Promise;


public class OnReceiveThread implements Runnable{
	private SiteVariables vars;
	private PaxosMessage m;
	private Thread thread;
	private List<Site> sites;

	
	public OnReceiveThread(SiteVariables vars, PaxosMessage message, List<Site> sites) {
		this.vars = vars;
		this.m = message;
		this.sites = sites;
	}

	public void run() {
		// determine message type and run corresponding function
        if (m instanceof Prepare) {
        	System.out.println("Received Prepare message from " + m.getSender());
        	Prepare pm = (Prepare) m;
        	pm.onReceive(vars);
        } else if (m instanceof Promise) {
        	System.out.println("Received Promise message from " + m.getSender());
        	Promise pm = (Promise) m;
        	pm.onReceive(vars, sites);
        } else if (m instanceof Accept) {
        	System.out.println("Received Accept message from " + m.getSender());
        	Accept am = (Accept) m;
        	am.onReceive(vars);
        } else if (m instanceof Ack) {
        	System.out.println("Received Ack message from " + m.getSender());
        	Ack am = (Ack) m;
        	am.onReceive(vars, sites);
        } else if (m instanceof Commit) {
        	System.out.println("Received Commit message from " + m.getSender());
        	Commit cm = (Commit) m;
        	cm.onReceive(vars);
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
