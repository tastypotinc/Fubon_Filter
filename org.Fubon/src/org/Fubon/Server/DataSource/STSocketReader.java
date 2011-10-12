package org.Fubon.Server.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;



import org.Fubon.Server.AppConfig;
import org.apache.commons.net.SocketClient;


/**
 * @author YC Huang
 */
 
public class STSocketReader extends SocketClient  implements DataReader
{
    static final boolean debug =  /*true;*/ false;
 
    static final boolean debugoptions =  /*true;*/ false;
    static final int DEFAULT_PORT=777;
    static boolean forceClose=false;
	String remoteip;
	int remoteport;
    
    public STSocketReader()
    {
    	try {

    	AppConfig config = AppConfig.getInstance();
    	remoteip=config.getString("DataServer.Remote_IP");
    	remoteport=Integer.parseInt(config.getString("DataServer.Remote_Port"));
        setDefaultPort(DEFAULT_PORT);
		}

		catch (Exception e) {
			System.err.println("Exception while get STSocketReader config (config.xml) :"
					+ e.getMessage());
		}

    }
 
    public OutputStream getOutputStream()
    {
        return _output_;
    }
    
    
    /***
     * Returns the telnet connection input stream.  You should not close the
     * stream when you finish with it.  Rather, you should call
     * {@link #disconnect  disconnect }.
     * <p>
     * @return The telnet connection input stream.
     * @throws IOException 
     * @throws SocketException 
     ***/
    public InputStream getInputStream() 
    {
    	try {
    		connect(remoteip, remoteport);
        	return _input_;
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    public void Close()
    {
    	try {
			super.disconnect();
			forceClose=true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public int read(byte[] buff) throws IOException 
    {
    	if (forceClose) return -1;
    	if (!isConnected()) connect(remoteip, remoteport);
    	return _input_.read(buff);
    }
    
 
}