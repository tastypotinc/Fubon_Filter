package org.Fubon.Server;
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.net.SocketException;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.Map;
import java.lang.Thread;

import org.Fubon.Server.Analysis.RuleManager;
import org.Fubon.Server.DataSource.DataReader;
import org.Fubon.Server.DataSource.DataSource;
import org.Fubon.Server.DataSource.FrameParser;
import org.Fubon.Server.DataSource.STFileReader;
import org.Fubon.Server.DataSource.STSocketReader;
import org.Fubon.Server.DataStore.Stock;
import org.Fubon.Server.DataStore.StockManager;
//import org.Fubon.Server.Utility.DateConvertor;


/**
 * @author Daniel F. Savarese
 * @author Bruno D'Avanzo
 */
 
public class DataManager extends  Thread
{
	public DataReader dr = null;
    DataSource	m_DS;
    public StockManager m_SM;
    public RuleManager m_RM;
    final int buffer_size=1024;

    public String RecordFile;
    public String LogFile;
    
	//static String message="";
	public Signal_Queue	queue;
	//static BroadCastingSe	broadcasting_queue;
	//Current Receiving Status
	public int	  DataManager_Status=0;  // 0:waiting  1:receiving -1 error
	public Status currentStatus;
	public int Reading_Type;   // 0: reading from Socket  1:reading from file
	public String currentStatusMessage;
	public int handle_ticks_count=0;
    public DataManager()
    {
		try {
		    String CloseFile;
		    String SymbolFile;
		    String Exchange;
		    String OpenTime;
			Calendar cal = new GregorianCalendar();
			queue=new Signal_Queue();
			//broadcasting_queue=new BroadCastingSignal_Queue();
			currentStatus=new Status();
	    	AppConfig config = AppConfig.getInstance();
	    	CloseFile=config.getString("Stock.Close");
	    	SymbolFile=config.getString("Stock.Symbol");
	    	RecordFile=config.getString("LocalFile.Record")+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString();
	    	LogFile=config.getString("LocalFile.Log");
	    	OpenTime=config.getString("Stock.OpenTime");
	    	Exchange=config.getString("Stock.Exchange");
	    	BroadCastingServer.http_directory=config.getString("Http.Directory");
			m_DS=new FrameParser(Exchange);
			m_SM=new StockManager(CloseFile,SymbolFile,Exchange,OpenTime);
			m_RM=new RuleManager();
			m_SM.setDataSource(m_DS);
			m_DS.setStockManager(m_SM);
		}

		catch (Exception e) {
			System.err.println("Exception while get DataManager config (config.xml) :"
					+ e.getMessage());
		}
    }
    
    private boolean checkiffitDefaultMarket()
    {
    	return m_DS.checkiffitDefaultMarket();
		
    }
    
    public void StartGenerateCloseFile(String FilePath)
    {
    	m_SM.StartGenerateCloseFile(FilePath);
    }
    
    public String getMarketTradingDate()
    {
    	return m_DS.getMarketTradingDate();
    }
    
    public void setReadingMethod(int type)
    {
    	Reading_Type=type;
    }
    
    public void addSignal(Signal tsignal)
    {
    	queue.add(tsignal);
    	BroadCastingServer.add(tsignal);
    }
 
   
    /***
     * Returns the telnet connection input stream.  You should not close the
     * stream when you finish with it.  Rather, you should call
     * {@link #disconnect  disconnect }.
     * <p>
     * @return The telnet connection input stream.
     * @throws FileNotFoundException 
     ***/
    
    // 清盤
    public void CleanMarket()
    {
    	System.out.println("=======>CleanMarket");
    	//將memory DB 的資料 Backup到File
    	m_SM.backupmemoryDB();
    	//將資料清除歸零
    	m_SM.clear();
    	m_SM.getStockCloseData();
    	m_SM.getStockSymbolData();
    	//將Signal清除歸零
    	queue.clear();
    	// 更新 Close Data Symbol
    }
    

    
    public void ParsingData() 
	{
    	FileOutputStream frecord = null;
    	FileWriter tFW = null;
    	//InputStream instr;
		int ret_read = 0;
		int packet_count=0;
		int err_packet_count=0;
		try {
			// Use Socket for reading data
			// dr = new SocketReader();
			// Use File for reading data
			
			if (Reading_Type==0)
				dr = new STSocketReader();
			else
				dr = new STFileReader();
			//instr = dr.getInputStream();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			DataManager_Status=-1;
			if (Reading_Type==0)
				currentStatusMessage="開啟Socket錯誤!";
			else
				currentStatusMessage="開啟資料檔案錯誤!";
			return;
		} 
	
		try
		{
			byte[] buff = new byte[buffer_size];
			byte[] last_buff = new byte[buffer_size];
			byte[] working_buff;
			int last_ret_read = 0;
			int startindex = 0, endindex = 0;   //The index in each buff
			String FrameType, FrameContent;
			Integer FrameLength;
			boolean Reset=false,crossbuff =false;
			
			if (RecordFile!="")
				frecord = new FileOutputStream(RecordFile, true);
			else 
				frecord = null;
			if (LogFile!="")
			{
		    	tFW=new FileWriter(LogFile);
		    	//BuffereWriter stockdata = new BufferedWriter(tFW);
				//flog = new FileOutputStream(LogFile, true);
			}
			else 
				tFW = null;
			do {
				try {
					DataManager_Status=1;
					//ret_read = instr.read(buff);
					ret_read = dr.read(buff);
					//if (Reading_Type==1) sleep(1000);
					if (frecord!=null) frecord.write(buff, 0, ret_read);
					if (ret_read > 0) {
						if (!crossbuff) {
							startindex = 0;
							endindex = 0;
						}
						do {
							if (!crossbuff) {
								startindex=-1;
								for (int i = endindex; i < ret_read; i++) {
									if (buff[i] != DataCommand.FrameStartByte)
										continue;
									else {
										startindex = i;
										break;
									}
	
								}
								if (startindex==-1)
								{
									// Scan all the packet Can't get the startindex ... Drop this packet
									Reset=true;  //Reset==1 will force to read the DataReader buffer
									
								}
								else
								{
									// This code is to prevent the some exception of socket ... The ret_read will be error
									// add FrameLength to prevent this error... 
									FrameLength=0;
									if ((startindex + (3+4))<buffer_size)
									{
										FrameLength = hex2int(new String(buff,
											startindex + 3, 4));
										if (((FrameLength+7) > ret_read) && ((FrameLength+7)<buffer_size))
											 ret_read=FrameLength+7;
									}
										
									for (int i = startindex; i < ret_read; i++) {
										if (buff[i] != DataCommand.FrameEndByte)
											continue;
										else {
											endindex = i;
											break;
										}
		
									}
									
									if (endindex <= startindex) {
										last_buff = buff.clone();
										last_ret_read = ret_read;
										crossbuff = true;
									} else {
										FrameType = new String(buff, startindex + 1, 2);
										// String hexstring;
										// byte[] len;
										// hexstring=new String(buff, startindex+3, 4);
										// len=hex2byte(hexstring);
										FrameLength = hex2int(new String(buff,
												startindex + 3, 4));
										// FrameLength=bytes2int(len);
										// FrameLength=bytes2int(hex2byte(new
										// String(buff, startindex+3, 4)));
										FrameContent = new String(buff, startindex + 7,
												FrameLength);
										// System.out.println("Frame: " +FrameType +
										// "Lenth:"+FrameLength
										// +"Content:"+FrameContent);
										if (FrameLength != ((endindex - startindex) - 7)) {
											System.err.println("Error(OnePacket) " + FrameLength
													+ "Begin:" + startindex + "End:"
													+ endindex+ " content:" + FrameContent);
											//Error Reconnect ...
											//return;
											err_packet_count++;
											currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";
	
										} 
										else
										{
											//System.out.println(FrameLength.toString()+" Frame->" + FrameContent);
											try
											{
												//Log Data
												if (tFW!=null)
													tFW.write(FrameLength.toString()+" Frame->" + FrameContent+"\n");
												// Process Data
												currentStatus.set(FrameContent);
												m_DS.Parser( FrameContent);
												marketOpenChecking();
												if (checkiffitDefaultMarket())
												{
													handle_ticks_count++;
													m_DS.SavetoDataStore(FrameType);
												}
												
												packet_count++;
												currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";
												
											}
											catch(Exception e)
											{
												System.err.println("FrameParser Exception :" + e.getMessage());
												err_packet_count++;
												currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";
	
											}
										}
									}
								}
							} else {
								endindex = 0;
								for (int i = 0; i < ret_read; i++) {
									if (buff[i] != DataCommand.FrameEndByte)
										continue;
									else {
										endindex = i;
										break;
									}
	
								}
								//Have to consider the case buff[0] fit the FrameEndByte
								if ((endindex == 0)&&(buff[0] != DataCommand.FrameEndByte))
								{
									// Add this buff to the end of last_buff
									
									byte[] tempBuff;
									tempBuff = last_buff;
									last_buff=new byte[last_ret_read+ret_read];
									
									for (int i=0;i<last_ret_read;i++)
									{
										last_buff[i]=tempBuff[i];
									}
									for (int i=last_ret_read;i<last_ret_read+ret_read;i++)
									{
										//last_buff[i]=buff[i-ret_read];
										last_buff[i]=buff[i-last_ret_read];
									}
									
									last_ret_read=last_ret_read+ret_read;
									
									System.err
											.println("Error Package more than 1024 Begin:"
													+ startindex
													+ "End:"
													+ endindex);
									
									FrameLength = hex2int(new String(last_buff,
											startindex+3, 4));  //For debugging
									System.err.println("Error " + FrameLength
											+ "ret_read:" + ret_read + "Begin:" + startindex + "End:"
											+ endindex + " FrameLength:" + FrameLength);
									err_packet_count++;
									currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";

								}
								else {
									working_buff = new byte[last_ret_read
											- startindex + endindex];
									for (int i = 0; i < last_ret_read - startindex; i++)
										working_buff[i] = last_buff[i + startindex];
									for (int i = 0; i < endindex; i++)
										working_buff[i + last_ret_read - startindex] = buff[i];
									FrameType = new String(working_buff, 1, 2);
									// String hexstring;
									// byte[] len;
									// hexstring=new String(buff, startindex+3, 4);
									// len=hex2byte(hexstring);
									FrameLength = hex2int(new String(working_buff,
											3, 4));
									// FrameLength=bytes2int(len);
									// FrameLength=bytes2int(hex2byte(new
									// String(buff, startindex+3, 4)));
									FrameContent = new String(working_buff, 7,
											FrameLength);
									// System.out.println("Frame: " +FrameType +
									// "Lenth:"+FrameLength
									// +"Content:"+FrameContent);
									crossbuff = false;
									if (FrameLength != ((last_ret_read - startindex + endindex) - 7)) {
										System.err.println("Error(cross packet) " + FrameLength
												+ "Begin:" + startindex + "End:"
												+ endindex + " content:" + FrameContent);
										err_packet_count++;
										currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";

									} 
									else
									{	try
										{
											//Log Data
											if (tFW!=null)
												tFW.write(FrameLength.toString()+" Frame->" + FrameContent+"\n");
											// Process Data
											currentStatus.set(FrameContent);
											m_DS.Parser( FrameContent);
											marketOpenChecking();
											if (checkiffitDefaultMarket())
											{
												handle_ticks_count++;
												m_DS.SavetoDataStore(FrameType);
											}
											packet_count++;
											currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";

										}
										catch(Exception e)
										{
											System.err.println("FrameParser Exception :" + e.getMessage());
											err_packet_count++;
											currentStatusMessage="(處理Tick:"+((Integer)handle_ticks_count).toString()+"收到Tick:"+((Integer)packet_count).toString()+" Errors:"+((Integer)err_packet_count).toString()+")";

										}
									}
								}
	
							}
	
						} while ((endindex < (ret_read - 1)) && (!crossbuff) && (!Reset));
					}
				} catch (Exception e) {
					System.err.println("Exception while parsing loop:" + e.getMessage());
					//exception reset the parsing 
					startindex = 0;
					endindex = 0;
					crossbuff = false;
					currentStatusMessage="讀取資料錯誤!("+e.getMessage()+")";
					DataManager_Status=-1;
				}
	
			} while (((ret_read > 0))&&!isInterrupted());
			
		} catch (Exception e) {
			System.err.println("Exception while parsing:" + e.getMessage());
			DataManager_Status=-1;
			currentStatusMessage="Parsing資料錯誤!("+e.getMessage()+")";
			 if (frecord!=null)
					try {
						frecord.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						currentStatusMessage="關閉紀錄檔案錯誤!("+e.getMessage()+")";
					}
		}

		try {
			dr.Close();
			DataManager_Status=0;
			// if (ret_read<0) System.exit(1);
		} catch (Exception e) {
			System.err.println("Exception while connecting:" + e.getMessage());
			DataManager_Status=-1;
			
			if (Reading_Type==0)
				currentStatusMessage="關閉Socket錯誤!("+e.getMessage()+")";
			else
				currentStatusMessage="關閉資料檔案錯誤!";
		}

	}
    
    private void marketOpenChecking()
    {
    	/* 檢查是否要清盤 */
    	if (m_DS.checkifMarketOpen())
		{
				CleanMarket();
				m_DS.setMarketTradingDate("");
		}
    }

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

/*	private static byte[] hex2byte(String hexStr) {
		if (hexStr == null || hexStr.isEmpty() || (hexStr.length() % 2 > 1)) {
			return null;
		}
		String hexStrUp = hexStr.toUpperCase();
		int length = hexStrUp.length() / 2;
		char[] hexChars = hexStrUp.toCharArray();
		byte[] resultByte = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			resultByte[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return resultByte;
	}
*/

	private static int hex2int(String hexStr) {
		if (hexStr == null || hexStr.isEmpty() || (hexStr.length() % 2 > 1)) {
			return 0;
		}
		String hexStrUp = hexStr.toUpperCase();
		int length = hexStrUp.length();
		char[] hexChars = hexStrUp.toCharArray();
		int result = 0;
		for (int i = 0; i < length; i++) {
			// int pos = i*2;
			// result = (byte) (charToByte(hexChars[pos]) << 4 |
			// charToByte(hexChars[pos+1]))+pos*result;
			result = result * 16;
			result = result + charToByte(hexChars[i]);
		}
		return result;
	}
/*
	private static int bytes2int(byte[] b) {
		// byte[] b=new byte[]{1,2,3,4};
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	private static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		//int mask = 0xff;
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}
*/
/*	public String DumpAll()
	{
	
	 Enumeration e = h.keys();
	    while (e.hasMoreElements())
	      System.out.println(e.nextElement());

	    e = h.elements();
	    while (e.hasMoreElements())
	      System.out.println(e.nextElement());
	} */
	public void setRule1(int bm,int bd, int per,boolean f,boolean v)
	{
		m_RM.setRule1(bm, bd, per,f, v);
	}
	
	public void setRule2(int bm, int per,boolean v)
	{
		m_RM.setRule2(bm, per, v);
	}
	
	public void setRule3(long rv,boolean v)
	{
		m_RM.setRule3(rv, v);
	}
	
	public String Dump(String symbol)
	{
		Stock tstock;
		tstock=m_SM.getStock(symbol);
		//tstock=m_DS.getStockbySymbol(symbol, Exchange);
		if (tstock==null) return "沒有這檔股票 ! ";
		else
		return tstock.Dump();
	}
	
	public void getFileDB2Memoryn() 
	{
		m_SM.getFileDB2Memory();  
	}
	
	@Override
	public void run() 
	{
		//getStockCloseData();
		
		//while (true)
		//{
//          boolean end_loop = false;
          try
          {
              
              try
              {
            	//m_SM.getFileDB2Memory();  
              	ParsingData();
              }
              catch (Exception e)
              {
                  System.err.println("Exception while connecting:" + e.getMessage());
                  DataManager_Status=-1;
                  //System.exit(1);
              }
          }
          catch (Exception e)
          {
                  System.err.println("Exception while connecting:" + e.getMessage());
                  DataManager_Status=-1;	
                  //System.exit(1);
          }
		//}
        
	}

 
}