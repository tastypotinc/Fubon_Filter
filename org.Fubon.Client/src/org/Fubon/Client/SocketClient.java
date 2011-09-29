package org.Fubon.Client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.net.URLConnection;
//import java.io.InputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
//import org.json.simple.parser.JSONParser;
 
public class SocketClient extends  Thread {

    Socket client;
    InetSocketAddress isa;
	static Signal_Queue	queue;
	static String statusMessage="未初始化";
	static int	  packet_receive=0;
	static int	  update_packet=0;
	static int	  err_packet_receive=0;
    public SocketClient(String address,int port) {
    	client = new Socket();
        isa = new InetSocketAddress(address, port);
        queue=new Signal_Queue();
        packet_receive=0;
    	update_packet=0;
    	err_packet_receive=0;
    }
    
    @Override
	public void run() 
	{


        try {
        	synchronized(statusMessage)	{statusMessage="連接伺服器中!";}
            client.connect(isa, 10000);
            System.out.println("Client connec to Server"+client.getInetAddress());
            synchronized(statusMessage)	{statusMessage="選股伺服器已連接.....";}
            BufferedInputStream in = new java.io.BufferedInputStream(client.getInputStream());
            byte[] b = new byte[Signal.Signal_Length];
            byte[] buff = new byte[Signal.Signal_Length];
            boolean isSplit=false;
            
            Signal tsignal;
            String data = "";
            int length;
            int lastIndex=0;
            
            Calendar cal = new GregorianCalendar();
            URL BroadCastingServer = new URL("http://"+isa.getHostName()+"/Fubon/"+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString()+".txt");
           // URLConnection yc = BroadCastingServer.openConnection();
           /* InputStream inHttp = BroadCastingServer.openStream();
            inHttp = new BufferedInputStream(inHttp);

           // BufferedReader inHttpBuffer = new BufferedReader(
           //                         new InputStreamReader(
           //                         yc.getInputStream(),"UTF-16")); 
            BufferedReader inHttpBuffer = new BufferedReader(
                    new InputStreamReader(
                    		inHttp,"UTF-16"));
            String inputLine="",jsonText="";
            
            while ((inputLine = inHttpBuffer.readLine()) != null) 
                jsonText=jsonText+inputLine;
            inHttpBuffer.close();
            */
        
            BufferedReader inHttp = new BufferedReader(new InputStreamReader(BroadCastingServer.openStream(),"UTF-16"));
            String inputLine="",jsonText="";
            while ((inputLine = inHttp.readLine()) != null) {
            	jsonText=jsonText+inputLine;
                
            }
            inHttp.close();
            
            try{
            	if (jsonText!=null)
            	{
            	//System.out.println(jsonText);
            	Object obj=JSONValue.parse(jsonText);
            	JSONArray array=(JSONArray)obj;
            	JSONObject signalJson;
            	for (int i=0;i<array.size();i++)
            	{
            		signalJson=(JSONObject) array.get(i);
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
            		tsignal=new Signal("");
            		//System.out.println("pass0");
            		tsignal.Rule 		= getString(signalJson,"Rule");
            		tsignal.Symbol 		= getString(signalJson,"Symbol");
            		tsignal.Time 		= getString(signalJson,"Time");
            		//System.out.println("pass1");
            		tsignal.Price 		= getfloat(signalJson,"Price");
            		tsignal.change 		= getfloat(signalJson,"change");
            		tsignal.volume 		= getlong(signalJson,"volume");
            		tsignal.totlavolume = getlong(signalJson,"totlvolume");
            		tsignal.avgvolume 	= getlong(signalJson,"avgvolume");
            		tsignal.message 	= getString(signalJson,"message");
            		//System.out.println("pass");
            		synchronized(queue)
    				{
            			queue.add(tsignal);
    				}
            		update_packet++;
            	}
            	}

            }
            catch(Exception pe){
              System.out.println("position: " + pe.toString());
              
            }
    		
            while (((length = in.read(b)) > 0)&&!isInterrupted())// <=0的話 or Interrupted 就是結束了
            {
            	packet_receive++;
            	
            	if (isSplit)
            	{
            		if ((Signal.Signal_Length-lastIndex)>length)
            		{// The packet Split into more than two
            			for (int i=0;i<length;i++)
	            			buff[i+lastIndex]=b[i];
	            		isSplit=true;
	            		lastIndex=lastIndex+length;
            		}
            		else
            		{
	            		for (int i=lastIndex;i<Signal.Signal_Length;i++)
	            		{
	            			if (length<i-lastIndex)
	                    		System.out.println("Error "+Integer.toString(length)+"--------------------------------------------------------------------------------------------------------");
	            			buff[i]=b[i-lastIndex];
	            		}
	            		data = String.format(Locale.TAIWAN , "%s", new String(buff, 0, length));
		                
		    			try
		    			{	
		    				   					
		    				tsignal=new Signal("");
		    				tsignal.setSignal(buff);
		
		    				synchronized(queue)
		    				{
		    					queue.add(tsignal);
		    				}
		    				if ((tsignal.Symbol.compareTo("14213")==1)||(tsignal.Symbol.compareTo("66404")==1))
			    				System.out.println("Break:" + data);
		    			}
		    			catch(Exception e)
		    			{
		    				System.err.println("Processing Signal: "+e.toString() );
		    				err_packet_receive++;
		    			}
		    			System.out.println("我取得的值(Split):" + data);
		    			
		    			if ((Signal.Signal_Length-lastIndex)<length)
		    			{
		    				for (int i=0;i<length-(Signal.Signal_Length-lastIndex);i++)
		            			buff[i]=b[i+(Signal.Signal_Length-lastIndex)];
		            		isSplit=true;
		            		lastIndex=length-(Signal.Signal_Length-lastIndex);
		    			}
		    			else
		    			{
		    				isSplit=false;
		    				lastIndex=0;
		    			}
            		}
            	}
            	else
            	{
	            	if ((Signal.Signal_Length>length)&&(!isSplit))
	            	{
	            		// The signal packet will send in two packets
	            		for (int i=0;i<length;i++)
	            			buff[i]=b[i];
	            		isSplit=true;
	            		lastIndex=length;
	            	}
	            	else
	            	{
		                data = String.format(Locale.TAIWAN , "%s", new String(b, 0, length));
		                //Signal tsignal;
		    			try
		    			{	
		    				   					
		    				tsignal=new Signal("");
		    				tsignal.setSignal(b);
		    				synchronized(queue)
		    				{
		    					queue.add(tsignal);
		    				}
		    				
		    			}
		    			catch(Exception e)
		    			{
		    				System.err.println("Processing Signal: "+e.toString() );
		    				err_packet_receive++;
		    			}
		                System.out.println("我取得的值:" + data);
	            	}
            	}
            }

            statusMessage="選股伺服器已斷線!";
            in.close();
            in = null;
            client.close();
 
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 !");
            System.out.println("IOException :" + e.toString());
            statusMessage="選股伺服器 Socket連線有問題 !"+ e.toString();
        }
    }
    
    String getString(JSONObject tjsonObject,String key)
    {
    	Object result;
    	result=tjsonObject.get(key);
    	if (result==null) return ""; else return result.toString();
    }
    
    float getfloat(JSONObject tjsonObject,String key)
    {
    	Object result;
    	result=tjsonObject.get(key);
    	if (result==null) return (float) 0.0; else return Float.parseFloat(result.toString());
    }
    
    long getlong(JSONObject tjsonObject,String key)
    {
    	Object result;
    	result=tjsonObject.get(key);
    	if (result==null) return (long) 0; else return Long.parseLong(result.toString());
    }
    

}