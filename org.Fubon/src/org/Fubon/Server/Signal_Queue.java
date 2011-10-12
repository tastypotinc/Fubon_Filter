package org.Fubon.Server;
import java.util.Vector;
// This Signal_Queue Keep all the signal data
public class Signal_Queue {
  
  static private Vector<Signal> queue = new Vector<Signal>();
    
  synchronized void add(Signal ts) {
      queue.addElement(ts);
  }
  
  public synchronized Signal get(int index) {
    
    Signal iobj = queue.elementAt(index);
    
    return iobj;
  }
  
  public synchronized int currentIndex()
  {
	  return queue.size();
  }
}