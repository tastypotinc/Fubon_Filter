package org.Fubon.Server.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface DataReader {

	public OutputStream getOutputStream();
	public InputStream getInputStream();
	public void Close();
	public int read(byte[] buff)  throws IOException ;
}
