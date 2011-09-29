package org.Fubon.Server;
import java.util.Vector;

class BroadCastingSignal_Queue {
  private final static int SIZE = 5000;
  private Vector<Signal> queue = new Vector<Signal>();
  private int count = 0;
  
  synchronized void add(Signal ts) {
    while(count == SIZE) {
      try {
    	System.out.println("Wait from add ! count:"+((Integer)count).toString()+" ID:"+this.toString());  
        wait();
        System.out.println("Wake up from add !"+" ID:"+this.toString());  
      }
      catch(InterruptedException ie) {
        ie.printStackTrace();
        System.exit(0);
      }
    }
    queue.addElement(ts);
    ++count;
    notify();
    notifyAll();
    System.out.println("NotifyAll from add ! count:"+((Integer)count).toString()+" ID:"+this.toString());  
  }
  
  synchronized Signal remove() {
    while(count == 0) {
    	try {
    		System.out.println("Wait from remove ! count:"+((Integer)count).toString()+" ID:"+this.toString());  
            wait();
            System.out.println("Wake up from remove !"+" ID:"+this.toString());  
          }
          catch(InterruptedException ie) {
            ie.printStackTrace();
            System.exit(0);
          }
    	  //return null;
      
    }
    Signal iobj = queue.remove(0);
    --count;
    notifyAll();
    System.out.println("Notify from remove ! count:"+((Integer)count).toString()+" ID:"+this.toString());  
    return iobj;
  }
  
  int size()
  {
	  return queue.size();
  }
  
  Signal read(int index)
  {
	  return queue.elementAt(index);
  }
  
}