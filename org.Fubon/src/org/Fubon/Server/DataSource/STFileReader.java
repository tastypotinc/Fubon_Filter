package org.Fubon.Server.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.Fubon.Server.AppConfig;



//import org.apache.commons.net.SocketClient;


/**
 * @author YC Huang
 */
 
public class STFileReader implements DataReader
{
    static final boolean debug =  /*true;*/ false;
 
    static final boolean debugoptions =  /*true;*/ false;
    static OutputStream  _output_;
    static InputStream  _input_;
    static boolean forceClose=false;
    public STFileReader() 
    {
    	try{
    		AppConfig config = AppConfig.getInstance();
    		String  testFile=config.getString("LocalFile.Data");
    		String  logFile=config.getString("LocalFile.Log");
    	        	_input_=new FileInputStream(testFile);
    	        	_output_=new FileOutputStream(logFile);
    	    forceClose=false;
        }
        catch (FileNotFoundException e)
        {
        	System.err.println("Exception while get STSocketReader config (config.xml) :"
    				+ e.getMessage());
        	
        }
    	
        
    }
 
    public OutputStream getOutputStream()
    {
        return _output_;
    }
    
    public void Close()
    {
       try {
    	   if (_output_!=null)		
			_output_.close();
		   if (_input_!=null) _input_.close();
		   forceClose=true;
       } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    /***
     * Returns the telnet connection input stream.  You should not close the
     * stream when you finish with it.  Rather, you should call
     * {@link #disconnect  disconnect }.
     * <p>
     * @return The telnet connection input stream.
     ***/
    public InputStream getInputStream()
    {
        return _input_;
    }

    public int read(byte[] buff) throws IOException 
    {
    	//System.err.println("File Bytes Left:" + Integer.valueOf(_input_.available()).toString());
    	/* if (_input_.available()<=5531302)
    	{
    		System.err.println("Enter infinite loop !");
    	} */
    	if (forceClose) return -1;
    	return _input_.read(buff);
    }
    
 
}