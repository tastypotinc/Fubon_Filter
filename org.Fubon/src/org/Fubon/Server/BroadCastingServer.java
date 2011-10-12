package org.Fubon.Server;
import java.net.ServerSocket;
import java.util.GregorianCalendar;
import java.net.Socket;

/*
public class BroadCastingServer extends java.lang.Thread {
 
    private boolean OutServer = false;
    private ServerSocket server;
    private final int ServerPort = 81;// 要監控的port
 
    public BroadCastingServer() {
        try {
            server = new ServerSocket(ServerPort);
 
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
 
    public void run() {
        Socket socket;
        java.io.BufferedInputStream in;
        java.io.BufferedOutputStream out;
 
        System.out.println("伺服器已啟動 !");
        while (!OutServer) {
            socket = null;
            try {
                synchronized (server) {
                    socket = server.accept();
                }
                System.out.println("取得連線 : InetAddress = "
                        + socket.getInetAddress());
                // TimeOut時間
                socket.setSoTimeout(15000);
 
                in = new java.io.BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                String data = "";
                int length;
                while ((length = in.read(b)) > 0)// <=0的話就是結束了
                {
                    data += new String(b, 0, length);
                }
 
                System.out.println("我取得的值:" + data);
                
                in.close();
                in = null;
                
                
                out = new java.io.BufferedOutputStream(socket.getOutputStream());
                
                String s="Send out data";
                System.out.println("送出訊息:" + s);
                out.write(s.getBytes());
                out.close();
                out = null;
                socket.close();
 
            } catch (java.io.IOException e) {
                System.out.println("Socket連線有問題 !");
                System.out.println("IOException :" + e.toString());
            }
 
        }
    }
 
 
}
*/

import java.io.*;
//import java.net.*;
//import java.security.*;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.Locale;
import java.util.Vector;
import java.util.Calendar;

//import com.ibm.icu.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Title:        Sample Server
 * Description:  This utility will accept input from a socket, posting back to the socket before closing the link.
 * It is intended as a template for coders to base servers on. Please report bugs to brad at kieser.net
 * Copyright:    Copyright (c) 2002
 * Company:      Kieser.net
 * @author B. Kieser
 * @version 1.0
 */

public class BroadCastingServer extends java.lang.Thread {

  private static int port=777, maxConnections=0;
  public  static int status=0;  // 0: normal -1: error
  public  static int user_numer=0;
  public  static String http_directory;
  public  static ServerSocket listener;
  // storeBroadCastingSignal_Queue keep the signal from market open to now
  //private static BroadCastingSignal_Queue storeBroadCastingSignal_Queue;
  // queueManager keep the seperate queue for each client
  private static Vector<BroadCastingSignal_Queue> queueManager = new Vector<BroadCastingSignal_Queue>();
  // Listen for incoming connections and handle them
  //private static JSONObject jsonobj;
  private static JSONArray	SignalArray;
  public BroadCastingServer()
  {
		try {

	    	AppConfig config = AppConfig.getInstance();
	    	port=Integer.parseInt(config.getString("BroadCasting.Port"));
	    	maxConnections=Integer.parseInt(config.getString("BroadCasting.MaxConnection"));
	    	//storeBroadCastingSignal_Queue=new BroadCastingSignal_Queue();
	    	status=0;
	    	user_numer=0;
	    	SignalArray= new JSONArray();
		}

		catch (Exception e) {
			System.err.println("Exception while get BroadCastingServer config (config.xml) :"
					+ e.getMessage());
		}
  }
  
  public void run() {
    int i=0;

    try{
      listener = new ServerSocket(port);
      Socket server;

      while(((i++ < maxConnections) || (maxConnections == 0))&&!isInterrupted())
      {
        //doComms connection;

        server = listener.accept();
        BroadCastingSignal_Queue tBroadCastingSignal_Queue;
        tBroadCastingSignal_Queue=requestQueue();
        // send the signal from market open
        /* for (int index=0;index<storeBroadCastingSignal_Queue.size();index++)
        {
        	tBroadCastingSignal_Queue.add(storeBroadCastingSignal_Queue.read(index));
        }*/
        
        // generate the signal from market open
        synchronized (SignalArray) 
	  	  {
	  	  	if (SignalArray!=null) 
	  	  	{
	  	  		Calendar cal = new GregorianCalendar();
	  	  		FileOutputStream fileOutputStream = new FileOutputStream(http_directory+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString()+".txt",false); 
	  	  		//FileWriter fileWriter = new FileWriter(http_directory+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString()+".txt",true); 
	  	  		OutputStreamWriter fileStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-16"); 
	  	  		
	  	  		StringWriter outString = new StringWriter();
	  	  		//SignalArray.writeJSONString(fileWriter);
	  	  		SignalArray.writeJSONString(outString);
	  	  		fileStreamWriter.write(outString.toString());
	
	  	  		//SignalArray.toJSONString(fileStreamWriter);
	  	  		fileStreamWriter.flush();
	  	  		fileStreamWriter.close();
	  	  		fileOutputStream.close();
	  	  	}
	  	  }
        doComms conn_c= new doComms(server,tBroadCastingSignal_Queue);
        Thread t = new Thread(conn_c);
        user_numer++;
        t.start();
      }
    } catch (IOException ioe) {
      System.out.println("IOException on socket listen: " + ioe);
      ioe.printStackTrace();
      if (!isInterrupted()) status=-1;
    }
  }
  
  public void setup(int p,int mc) {
	  port=p;
	  maxConnections=mc;
  }
  
  static synchronized public BroadCastingSignal_Queue requestQueue()
  {
	  BroadCastingSignal_Queue new_broadcastingsignal_queue;
	  new_broadcastingsignal_queue=new BroadCastingSignal_Queue();
	  queueManager.addElement(new_broadcastingsignal_queue);
	  return new_broadcastingsignal_queue;
  }

  static synchronized public boolean removeQueue(BroadCastingSignal_Queue remove_broadcastingsignal_queue)
  {
	  try
	  {
		  return queueManager.removeElement(remove_broadcastingsignal_queue);
	  }
	  catch(Exception e)
	  {
		  System.err.println("Remove BroadCastingSignal_Queue Error :"+e.toString());
	  }
	return false;
  }
  
  @SuppressWarnings("unchecked")
static synchronized void add(Signal ts) {
	  BroadCastingSignal_Queue tBroadCastingSignal_Queue;
	  for (int i=0;i<queueManager.size();i++)
	  {
		  tBroadCastingSignal_Queue=queueManager.get(i);
		  tBroadCastingSignal_Queue.add(ts);
	  }
	  //storeBroadCastingSignal_Queue.add(ts);
	  
	  JSONObject jsonobj;
	//	Signal	  
	//  public	String 	Rule;  		// 6 Bytes
	//	public	String 	Symbol;		// 6 Bytes
	//	public	String	Time;		// 8 Bytes  HH:mm:ss;
	//	public	float	Price;		// 4 Bytes
	//	public	float	change;		// 4 Bytes
	//	public	long	volume;		// 8 Bytes
	//	public	long	totlavolume;// 8 Bytes
	//	public	long	avgvolume;	// 8 Bytes
	//	public	String 	message;	// 30 Bytes
  	  jsonobj=new JSONObject();
  	  
/*	  jsonobj.put("Rule",String.format(Locale.TAIWAN , "%s", ts.Rule));
	  jsonobj.put("Symbol",String.format(Locale.TAIWAN , "%s", ts.Symbol));
	  jsonobj.put("Time",String.format(Locale.TAIWAN , "%s", ts.Time));
	  jsonobj.put("Price",String.format(Locale.TAIWAN , "%s", ts.Price));
	  jsonobj.put("change",String.format(Locale.TAIWAN , "%s", ts.change));
	  jsonobj.put("volume",String.format(Locale.TAIWAN , "%s", ts.volume));
	  jsonobj.put("totlvolume",String.format(Locale.TAIWAN , "%s", ts.totlavolume));
	  jsonobj.put("avgvolume",String.format(Locale.TAIWAN , "%s", ts.avgvolume));
	  jsonobj.put("message",String.format(Locale.TAIWAN , "%s", ts.message));
	  jsonobj.put("Price",String.format(Locale.TAIWAN , "%s", ts.Price));
	  jsonobj.put("change",String.format(Locale.TAIWAN , "%s", ts.change)); */
	  try
	  {
	  jsonobj.put("Rule",new String(ts.Rule.getBytes("UTF-16"), "UTF-16"));
	  jsonobj.put("Symbol",new String(ts.Symbol.getBytes("UTF-16"), "UTF-16"));
	  jsonobj.put("Time",new String(ts.Time.getBytes("UTF-16"), "UTF-16")); 

/*	  jsonobj.put("Rule",String.format(Locale.TRADITIONAL_CHINESE, "%s", ts.Rule));
	  jsonobj.put("Symbol",String.format(Locale.TRADITIONAL_CHINESE , "%s", ts.Symbol));
	  jsonobj.put("Time",String.format(Locale.TRADITIONAL_CHINESE , "%s", ts.Time)); */
	  jsonobj.put("Price",ts.Price);
	  jsonobj.put("change",ts.change);
	  jsonobj.put("volume",ts.volume);
	  jsonobj.put("totlvolume",ts.totlavolume);
	  jsonobj.put("avgvolume",ts.avgvolume);
	  jsonobj.put("message",new String(ts.message.getBytes("UTF-16"), "UTF-16"));
//	  jsonobj.put("message",String.format(Locale.TRADITIONAL_CHINESE , "%s", ts.message));
	  }
	  catch(Exception e)
	  {
		  System.err.println("The Signal format transafer error ! "+e.toString());
	  }
	  synchronized (SignalArray) 
	  {
		  if (SignalArray==null) SignalArray=new JSONArray();
		  SignalArray.add(jsonobj);
		  //System.out.print(jsonobj);
	  }
  


  }
}

class doComms implements Runnable {
    private Socket server;
    private BroadCastingSignal_Queue userBroadCastingSignal_Queue;
//    private java.io.BufferedInputStream in;
    private java.io.BufferedOutputStream out;

    doComms(Socket server,BroadCastingSignal_Queue tBQ) {
      this.userBroadCastingSignal_Queue=tBQ;	
      this.server=server;
    }

    public void run () {

  /*    input="";

      try {
        // Get input from the client
        DataInputStream in = new DataInputStream (server.getInputStream());
        PrintStream out = new PrintStream(server.getOutputStream());

        while((line = in.readLine()) != null && !line.equals(".")) {
          input=input + line;
          out.println("I got:" + line);
        }

        // Now write to the client

        System.out.println("Overall message is:" + input);
        out.println("Overall message is:" + input);

        server.close();
      } catch (IOException ioe) {
        System.out.println("IOException on socket listen: " + ioe);
        ioe.printStackTrace();
      }
*/


 
        System.out.println("伺服器已啟動 !");
       
 
            try {
                System.out.println("取得連線 : InetAddress = "
                        + server.getInetAddress());
                // TimeOut時間                
                server.setSoTimeout(1500);
                out = new java.io.BufferedOutputStream(server.getOutputStream());
                
/*                in = new java.io.BufferedInputStream(server.getInputStream());              
                byte[] b = new byte[1024];
                String data = "";
                int length;
                while ((length = in.read(b)) > 0)// <=0的話就是結束了
                {
                    data += new String(b, 0, length);
                }
 
                System.out.println("我取得的值:" + data);
                
                in.close();
                in = null;
*/                
                Signal			tsignal;
                System.out.println("開始處理出訊息!\n" );               
                //while (userBroadCastingSignal_Queue.remove())
                while ((tsignal=userBroadCastingSignal_Queue.remove())!=null)
                {
                	out.write(tsignal.getSignal());
                	// Testing 
                	//byte[] test;
                	//test=tsignal.getSignal();
                	//tsignal.setSignal(test);
                	//out.write(String.format(Locale.TAIWAN, "%s\n", tsignal.getSignal()).getBytes());
                	out.flush();
                }

            } catch (java.io.IOException e) {
                System.out.println("Socket連線有問題 !");
                System.out.println("IOException :" + e.toString());
                try {
                	out.close();
                    out = null;
					server.close();
					BroadCastingServer.removeQueue(userBroadCastingSignal_Queue);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				BroadCastingServer.user_numer--;
				//Thread.stop();
            }
 /*           out.close();
            out = null;
            server.close();

 */
        
    }
}
