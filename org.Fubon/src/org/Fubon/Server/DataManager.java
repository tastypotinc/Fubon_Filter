package org.Fubon.Server;
import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.net.SocketException;
import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.List;
import java.util.Map;
import java.lang.Thread;



/**
 * @author Daniel F. Savarese
 * @author Bruno D'Avanzo
 */
 
class DataManager extends  Thread
{
	DataReader dr = null;
    FrameParser	tFP;
    final int buffer_size=1024;
    static String CloseFile;
    static String SymbolFile;
    static String RecordFile;
    static String LogFile;
    static String Exchange;
    static String OpenTime;
    
	//static String message="";
	static Signal_Queue	queue;
	//static BroadCastingSe	broadcasting_queue;
	//Current Receiving Status
	static int	  DataManager_Status=0;  // 0:waiting  1:receiving -1 error
	static Status currentStatus;
	static int Reading_Type;   // 0: reading from Socket  1:reading from file
	static String currentStatusMessage;
	static int handle_ticks_count=0;
    DataManager()
    {
		try {
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
			tFP=new FrameParser(Exchange);
			
		}

		catch (Exception e) {
			System.err.println("Exception while get DataManager config (config.xml) :"
					+ e.getMessage());
		}
    }
    
    static String getMarketTradingDate()
    {
    	return FrameParser.MarketTradingDate;
    }
    
    static public void setReadingMethod(int type)
    {
    	Reading_Type=type;
    }
    
    static public void addSignal(Signal tsignal)
    {
    	queue.add(tsignal);
    	BroadCastingServer.add(tsignal);
    }
 
    public Stock getStockbySymbol(String Symbol)
    {
    	//return tFP.getStockbySymbol(Symbol,Exchange);
    	return FrameParser.stockHashtable.get(Symbol + "." + Exchange);
    }
    
    public Map<Integer, Tick> getStockTickListbySymbol(String Symbol)
    {
    	//return tFP.getStockTickListbySymbol(Symbol,Exchange);
    	return (Map<Integer, Tick>) FrameParser.stockHashtable.get(Symbol + "." + Exchange).ts;
    }
    
    public Map<Date, DayLine> getStockDayLinebySymbol(String Symbol)
    {
    	//return tFP.getStockDayLinebySymbol(Symbol,Exchange);
    	return (Map<Date, DayLine>) FrameParser.stockHashtable.get(Symbol + "." + Exchange).dl;
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
    public static void CleanMarket()
    {
    	FrameParser.CleanMarket();
    	getStockCloseData();
    	getStockSymbolData();
    	
    }
    
    private static void getStockCloseData() 
    {
    	try {
	    	String CloseData;
//	    	String Symbol;
	    	//byte[] buff = new byte[1024];
	    	
	    	//int ret_read = 0;

	    	FileReader CF=new FileReader(CloseFile);
	    	BufferedReader stockdata = new BufferedReader(CF);
	    	while((CloseData = stockdata.readLine()) != null)
	    	{
	    		//try {
	    		
	    			//CloseData = stockdata.readLine();
					//CloseData = new String(buff, 0,ret_read);
					FrameParser.CloseParser(CloseData,Exchange,OpenTime);
					
				/*} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
    		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void getStockSymbolData() 
    {
    	try {
	    	String SymbolData;
////	    	String Symbol;
//	    	//byte[] buff = new byte[1024];
//	    	
//	    	//int ret_read = 0;
//	    	
//	    	InputStreamReader CF= new InputStreamReader(new InputStream(SymbolFile),"UTF-16");
//	    	BufferedReader stockdata = new BufferedReader(CF);
	    	
//	    	BufferedReader stockdata = new BufferedReader(new InputStreamReader(new FileInputStream(SymbolFile),
//	        "UTF-16"));
	    	BufferedReader stockdata = new BufferedReader(new InputStreamReader(new FileInputStream(SymbolFile),
	        "UTF-16"));
	    	while((SymbolData = stockdata.readLine()) != null)
	    	{
	    		//try {
	    		
	    			//SymbolData = stockdata.readLine();
					//SymbolData = new String(buff, 0,ret_read);
					FrameParser.SymbolParser(SymbolData,Exchange,OpenTime);
					
				/*} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
    		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
												tFP.Parser(FrameType, FrameContent, OpenTime);
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
											tFP.Parser(FrameType, FrameContent, OpenTime);
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
		tFP.setRule1(bm, bd, per,f, v);
	}
	
	public void setRule2(int bm, int per,boolean v)
	{
		tFP.setRule2(bm, per, v);
	}
	
	public void setRule3(long rv,boolean v)
	{
		tFP.setRule3(rv, v);
	}
	
	public String Dump(String symbol)
	{
		Stock tstock;
		tstock=FrameParser.stockHashtable.get(symbol + "." + Exchange);
		//tstock=tFP.getStockbySymbol(symbol, Exchange);
		if (tstock==null) return "沒有這檔股票 ! ";
		else
		return tstock.Dump();
	}
	
	void getFileDB2Memory() 
	{
		try
		{
			// 1. 重新從  Symbol.txt and Close.txt 更新股票基本資料
			// 2. 從sqlite檔案中更新至記憶體sqlite DB
			// 3. 將已經發生過的 Tick 重新 Parse 為解決 盤中重新啟動的資料保存
			// 4. 盤中時refresh tick 會更新 uplimit,downlimit,open,close,high,low 特別是收盤處理所送出的Tick ... 目前還沒implement recovery 機制
			
			/* =======The field in ticker==================================== */
			String ID = "";
			String TradingDate = "";
//			String PreClose = "";
//			String UpLimit = "";
//			String DownLimit = "";
//			String PreTotalVolume = "";
			String Time;
//			String Settlement;
			float Price;
			float Bid;
			float Ask;
//			String Open;
//			String High;
//			String Low;
			Long Volume;
			Long TotalVolume;
			float BestBid1;
			float BestBid2;
			float BestBid3;
			float BestBid4;
			float BestBid5;
			float BestAsk1;
			float BestAsk2;
			float BestAsk3;
			float BestAsk4;
			float BestAsk5;
			int BestBidSize1;
			int BestBidSize2;
			int BestBidSize3;
			int BestBidSize4;
			int BestBidSize5;
			int BestAskSize1;
			int BestAskSize2;
			int BestAskSize3;
			int BestAskSize4;
			int BestAskSize5;
			int TickIndex;
			String Exch = "";
			Stock tStock;

			FrameParser.CleanMarket();
	    	
	    	//return;
	    	
			FrameParser.tickDataStore.openConnectionAndCopyDbInMemory();
			
			ResultSet rs=FrameParser.tickDataStore.getDataSet();
			int New;
			//int type=0;
			while (rs.next()) {
//				tickDataStore.Insert(ID,TradingDate, Time, Price, Bid, Ask, Volume,
//						TotalVolume, BestBid1, BestBid2, BestBid3, BestBid4,
//						BestBid5, BestAsk1, BestAsk2, BestAsk3, BestAsk4, BestAsk5,
//						BestBidSize1, BestBidSize2, BestBidSize3, BestBidSize4,
//						BestBidSize5, BestAskSize1, BestAskSize2, BestAskSize3,
//						BestAskSize4, BestAskSize5, TickIndex, Exch,FrameType);
				New = 0;
				ID=rs.getString("ID");
				TradingDate=rs.getString("TradingDate");
				Time=rs.getString("Time");
				Price=rs.getFloat("Price");
				Bid=rs.getFloat("Bid");
				Ask=rs.getFloat("Ask");
				Volume=rs.getLong("Volume");
				TotalVolume=rs.getLong("TotalVolume");
				BestBid1=rs.getFloat("BestBid1");
				BestBid2=rs.getFloat("BestBid2");
				BestBid3=rs.getFloat("BestBid3");
				BestBid4=rs.getFloat("BestBid4");
				BestBid5=rs.getFloat("BestBid5");
				BestAsk1=rs.getFloat("BestAsk1");
				BestAsk2=rs.getFloat("BestAsk2");
				BestAsk3=rs.getFloat("BestAsk3");
				BestAsk4=rs.getFloat("BestAsk4");
				BestAsk5=rs.getFloat("BestAsk5");
				BestBidSize1=rs.getInt("BestBidSize1");
				BestBidSize2=rs.getInt("BestBidSize2");
				BestBidSize3=rs.getInt("BestBidSize3");
				BestBidSize4=rs.getInt("BestBidSize4");
				BestBidSize5=rs.getInt("BestBidSize5");
				BestAskSize1=rs.getInt("BestAskSize1");
				BestAskSize2=rs.getInt("BestAskSize2");
				BestAskSize3=rs.getInt("BestAskSize3");
				BestAskSize4=rs.getInt("BestAskSize4");
				BestAskSize5=rs.getInt("BestAskSize5");
				TickIndex =rs.getInt("TickIndex");
				Exch=tFP.Exchange;
				//type=Integer.parseInt(rs.getString("FrameType"));
				if (!FrameParser.stockHashtable.containsKey(ID + "." + Exch)) {
					tStock = new Stock(ID, Exch, OpenTime);
					
					//((Rule1)tStock.lr.get(0)).reset(rule1_bm, rule1_bd, rule1_per, rule1_filter, rule1_v);
					//((Rule2)tStock.lr.get(1)).reset(rule2_bm, rule2_per, rule2_v);
					//((Rule3)tStock.lr.get(2)).reset(rule3_rv,rule3_v);
					
					New = 1;
				} else {
					tStock = (Stock) FrameParser.stockHashtable.get(ID + "." + Exch);
				}


				tStock.memoryTick(TradingDate, Time, Price, Bid, Ask, Volume,
							TotalVolume, BestBid1, BestBid2, BestBid3, BestBid4,
							BestBid5, BestAsk1, BestAsk2, BestAsk3, BestAsk4, BestAsk5,
							BestBidSize1, BestBidSize2, BestBidSize3, BestBidSize4,
							BestBidSize5, BestAskSize1, BestAskSize2, BestAskSize3,
							BestAskSize4, BestAskSize5, TickIndex, Exch);
					if (New == 1)
						FrameParser.stockHashtable.put(ID + "." + Exch, tStock);
					

				}

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("getFileDB2Memory Error:"+e.toString());

		}
		getStockCloseData();
    	getStockSymbolData();

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
            	getFileDB2Memory();  
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