package org.Fubon.Server;
public class Status {
	String message="尚未連接";
	boolean Changed=true;
	synchronized void set(String ts) {
		message=ts;
		Changed=true;
	  }
	  
	public synchronized String read() {
	    Changed=false;
	    return message;
	  }
	
	public synchronized boolean Changed()
	{
		return Changed;
	}
}
