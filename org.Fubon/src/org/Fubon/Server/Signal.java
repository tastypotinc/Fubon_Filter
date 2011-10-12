package org.Fubon.Server;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
//import java.util.Locale;


public class Signal {
	final   int     Signal_Length=118;
	final	String  charset="UTF-8";
	public	String 	Rule;  		// 6 Bytes
	public	String 	Symbol;		// 6 Bytes
	public	String	Time;		// 8 Bytes  HH:mm:ss;
	public	float	Price;		// 4 Bytes
	public	float	change;		// 4 Bytes
	public	long	volume;		// 8 Bytes
	public	long	totlavolume;// 8 Bytes
	public	long	avgvolume;	// 8 Bytes
	public	String 	message;	// 30 Bytes
	public Signal(String msg)
	{
		message=msg;
	}
	String getSignalMessage()
	{
		return message;
	}
	byte[] getSignal()
	{
		try
		{
		byte[] result;
		result=new byte[Signal_Length];
		copyBytes(Rule.getBytes(charset)			,result,	 0,12);
		copyBytes(Symbol.getBytes(charset)			,result,	12,	6);
		copyBytes(Time.getBytes(charset)			,result,	18, 8);
		copyBytes(floatTobytes(Price)		,result,	26, 4);
		copyBytes(floatTobytes(change)		,result,	30, 4);
		copyBytes(longTobytes(volume)		,result,	34, 8);
		copyBytes(longTobytes(totlavolume)	,result,	42, 8);
		copyBytes(longTobytes(avgvolume)	,result,	50, 8);
		copyBytes(message.getBytes(charset)			,result,	58,60);
		return result;
		}
		catch (Exception e)
		{
			System.err.println("getSignal error :"+e.toString());
			return null;
		}
	}
	
	void setSignal(byte[] data)
	{
		if (data.length!=Signal_Length) 
		{
			System.err.println("Error in setSignal Data length error");
			return;
		}
		try
		{
			Rule=new String(fetchbytes(data,0,12),charset);			// 6 Bytes
			Symbol=new String(fetchbytes(data,12,6),charset);		// 6 Bytes
			Time=new String(fetchbytes(data,18,8),charset);			// 8 Bytes  HH:mm:ss;
			Price=bytesToFloat(fetchbytes(data,26,4));				// 4 Bytes
			change=bytesToFloat(fetchbytes(data,30,4));				// 4 Bytes
			volume=bytesToLong(fetchbytes(data,34,8));				// 8 Bytes
			totlavolume=bytesToLong(fetchbytes(data,42,8));			// 8 Bytes
			avgvolume=bytesToLong(fetchbytes(data,50,8));			// 8 Bytes
			message=new String(fetchbytes(data,58,60),charset);		// 30 Bytes
		}
		catch (Exception e)
		{
			System.err.println("Signal.setSignal Error:"+e.toString());
		}
	}
	// Fetch a length from S to D
	// Fetch startat~atartat+len
	byte[]	fetchbytes(byte[] S,int startat, int len)
	{
		if ((startat+len)>S.length) 
		{
			System.err.println("Error in fetchBytes startat:"+((Integer)startat).toString()+" len:"+((Integer)len).toString());
			return null;
		}
		byte[] result;
		result=new byte[len];
		for (int i=0;i<len;i++)
		{
			result[i]=S[startat+i];
		}
		return result;
	}
	//S source always begin at 0
	//D destination begin at startat
	void	copyBytes(byte[] S,byte[] D,int startat, int len)
	{
		int i;
		if ((startat+len)>D.length) 
		{
			System.err.println("Error in copyBytes S:"+S.toString()+" D:"+D.toString()+" startat:"+((Integer)startat).toString()+" len:"+((Integer)len).toString());
			return;
		}
		if (S.length < len)
		{
			for (i=0;i<S.length;i++)
			{
				D[startat+i]=S[i];
			}
			for (i=S.length;i<len;i++)
				D[startat+i]=0;
		}
		else
		{
			for (i=0;i<len;i++)
				D[startat+i]=S[i];
		}
	}
	
	//float to byte[]
	public static byte[] floatTobytes(float v) {
	        ByteBuffer bb = ByteBuffer.allocate(4);
	        byte[] ret = new byte [4];
	        FloatBuffer fb = bb.asFloatBuffer();
	        fb.put(v);
	        bb.get(ret);
	        return ret;
	}

	//byte[]转float
	public static float bytesToFloat(byte[] v){
	        ByteBuffer bb = ByteBuffer.wrap(v);
	        FloatBuffer fb = bb.asFloatBuffer();
	        return fb.get();
	}
	
	public static byte[] longTobytes(long v) {
	    byte[] writeBuffer = new byte[ 8 ];

	    writeBuffer[0] = (byte)(v >>> 56);
	    writeBuffer[1] = (byte)(v >>> 48);
	    writeBuffer[2] = (byte)(v >>> 40);
	    writeBuffer[3] = (byte)(v >>> 32);
	    writeBuffer[4] = (byte)(v >>> 24);
	    writeBuffer[5] = (byte)(v >>> 16);
	    writeBuffer[6] = (byte)(v >>>  8);
	    writeBuffer[7] = (byte)(v >>>  0);

	    return writeBuffer;
	}
	
	public static long bytesToLong(byte[] bb) {
        return ((((long) bb[0] & 0xff) << 56)
                | (((long) bb[1] & 0xff) << 48)
                | (((long) bb[2] & 0xff) << 40)
                | (((long) bb[3] & 0xff) << 32)
                | (((long) bb[4] & 0xff) << 24)
                | (((long) bb[5] & 0xff) << 16)
                | (((long) bb[6] & 0xff) << 8) | (((long) bb[7] & 0xff) << 0));
    }
	
	public static long bytesToLong(byte[] bb, int index) {
        return ((((long) bb[index + 0] & 0xff) << 56)
                | (((long) bb[index + 1] & 0xff) << 48)
                | (((long) bb[index + 2] & 0xff) << 40)
                | (((long) bb[index + 3] & 0xff) << 32)
                | (((long) bb[index + 4] & 0xff) << 24)
                | (((long) bb[index + 5] & 0xff) << 16)
                | (((long) bb[index + 6] & 0xff) << 8) | (((long) bb[index + 7] & 0xff) << 0));
    }
}
