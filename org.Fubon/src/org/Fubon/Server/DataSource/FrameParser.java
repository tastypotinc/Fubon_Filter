package org.Fubon.Server.DataSource;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Map;
//import java.util.StringTokenizer;
//import java.util.TreeMap;

//import org.Fubon.Server.DataManager;
//import org.Fubon.Server.Analysis.*;

import org.Fubon.Server.DataStore.DayLine;
import org.Fubon.Server.DataStore.Stock;
import org.Fubon.Server.DataStore.StockManager;
import org.Fubon.Server.Utility.DateConvertor;

//import org.json.simple.JSONObject;
//import java.util.Locale;
public class FrameParser implements DataSource {

	StockManager m_SM;
	
	String Exchange;		// The config market
	static public String MarketTradingDate=""; 
	/* =======The field in ticker==================================== */
	
	static Stock tStock;
	static String ID = "";
	String TradingDate = "";
	String PreClose = "";
	String UpLimit = "";
	String DownLimit = "";
	String PreTotalVolume = "";
	String Time;
	String Settlement;
	String Price;
	String Bid;
	String Ask;
	String Open;
	String High;
	String Low;
	String Volume;
	String TotalVolume;
	String BestBid1;
	String BestBid2;
	String BestBid3;
	String BestBid4;
	String BestBid5;
	String BestAsk1;
	String BestAsk2;
	String BestAsk3;
	String BestAsk4;
	String BestAsk5;
	String BestBidSize1;
	String BestBidSize2;
	String BestBidSize3;
	String BestBidSize4;
	String BestBidSize5;
	String BestAskSize1;
	String BestAskSize2;
	String BestAskSize3;
	String BestAskSize4;
	String BestAskSize5;
	String TickIndex;
	String Exch = "";
	
	
	Calendar cal = new GregorianCalendar();
	static DateFormat df ;  //= new SimpleDateFormat("yyyyMMdd");

	/*
	 * FrameParser(Hashtable<String,Stock> pHT) { stockHashtable=pHT; }
	 */

	public FrameParser(String ex) {
		
		Exchange=ex;
	}
	
	public boolean checkifMarketOpen()
	{
		/* 檢查是否為剛開盤 */
		if ((TradingDate!=null)&&(TradingDate.length()!=0))
		{
			if ((MarketTradingDate!=null)&&(MarketTradingDate.length()!=0))
			{
				if (!DateConvertor.thesameTradingDate(TradingDate, MarketTradingDate))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void setStockManager(StockManager SM)
	{
		m_SM=SM;
	}
	
	/*
	public void setRule1(int bm,int bd, int per,boolean f,boolean v)
    {
		Object[] symbol= stockHashtable.keySet().toArray();
    	Stock	tstock;
    	Rule1	r1;
    	rule1_bm=bm;
    	rule1_bd=bd;
    	rule1_per=per;
    	rule1_filter=f;
    	rule1_v=v;
    	for (int i=0;i<symbol.length;i++)
		{
    		tstock=stockHashtable.get(symbol[i]);
//    		if (tstock.Symbol.compareTo("3522")==0)
//    		{
//    			System.out.println("3522 is here ");
//    		}
    		r1=(Rule1)tstock.lr.get(0);
    		r1.reset(bm, bd, per, f,v);
		}
    }
	
	public void setRule2(int bm, int per,boolean v)
    {
		Object[] symbol= stockHashtable.keySet().toArray();
    	Stock	tstock;
    	Rule2	r2;
    	rule2_bm=bm;
    	rule2_per=per;
    	rule2_v=v;
    	for (int i=0;i<symbol.length;i++)
		{
    		tstock=stockHashtable.get(symbol[i]);
    		r2=(Rule2)tstock.lr.get(1);
    		r2.reset(bm, per, v);
		}
    }
	
	public void setRule3(long rv,boolean v)
    {
		Object[] symbol= stockHashtable.keySet().toArray();
    	Stock	tstock;
    	Rule3	r3;
    	rule3_rv=rv;
    	rule3_v=v;
    	for (int i=0;i<symbol.length;i++)
		{
    		tstock=stockHashtable.get(symbol[i]);
    		r3=(Rule3)tstock.lr.get(2);
    		r3.reset(rv,v);
		}
    }
	*/

	
	
/*	static public Stock getStockbySymbol(String Symbol, String Exchange) {
		return (Stock) stockHashtable.get(Symbol + "." + Exchange);
	}

	static public Map<Integer, Tick> getStockTickListbySymbol(String Symbol, String Exchange) {
		return (Map<Integer, Tick>) stockHashtable.get(Symbol + "." + Exchange).ts;
	}

*/
/*	public void CleanMarket()
	{
		if (m_SM!=null)
			m_SM.clear();

	}
*/
	/*
	 * void setAllStockDayLineSorted() { //After the insert of CloseParser
	 * parsing all the close file.. Call this method stockHashtable. }
	 */

	public void CloseParser(String CloseData, String Exchange, String strOpenTime) {
		try {
			String[] item;
			DayLine tDayLine;
//			int New = 0;
//			Calendar ical;
			tDayLine = new DayLine();
			item = CloseData.split(",");
			ID = item[1];
//			ical = new GregorianCalendar();
			
			//df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//tDayLine.tDate = df.parse(item[0]);
			tDayLine.tDate = item[0];
			tDayLine.Close = Float.parseFloat(item[2]);
			tDayLine.Volume= Long.parseLong(item[3]);
			tDayLine.Open= Float.parseFloat(item[4]);
			tDayLine.High = Float.parseFloat(item[5]);
			tDayLine.Low = Float.parseFloat(item[6]);
			/* if (ID.compareTo("0001")==0)
			{
				System.out.println("Sotck:"+ID+"-"+item[0]+"-"+item[2]);
			} */
			
			/* 將收盤價 更新到 StockManager */
			if (m_SM!=null) 		m_SM.insert_Close(item[1],tDayLine);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("CloseParser Error:" + e.toString());
			// e.printStackTrace();
		}

	}
	
	/*
	 * void setAllStockDayLineSorted() { //After the insert of CloseParser
	 * parsing all the close file.. Call this method stockHashtable. }
	 */

	public void SymbolParser(String SymbolData) {
		try {
			String[] item;
			
//			int New = 0;
//			Calendar ical;
			
			item = SymbolData.split(",");
/*
 * 			SymbolHK.txt 的格式
//			item[0] Symbol
//			item[1] Name
//			item[2] Short_Name
//			item[3] CUR
//			item[4] Unit
//			item[5] Reference
//			item[6] EName
//			item[7] Short_EName
 * 
 */
			ID = item[0];
//			ical = new GregorianCalendar();
			
			
			if (item.length<8)
			{

				item = expand(item, 8);

				
			}
			/* 股票Symbol 資料更新到 DB */
			if (m_SM!=null)
			m_SM.insert_Symbol(item[0], getString(item[1]), getString(item[2]), getString(item[3]), getint(item[4]), getfloat(item[5]), getString(item[6]), getString(item[7]));
			/*
			symbolDataStore.Insert(item[0], getString(item[1]), getString(item[2]), getString(item[3]), getint(item[4]), getfloat(item[5]), getString(item[6]), getString(item[7]));
			if (!stockHashtable.containsKey(ID + "." + Exchange)) {
				tStock = new Stock(ID, Exchange, strOpenTime);
				tStock.UpdateSymbol(item[0], getString(item[1]), getString(item[2]), getString(item[3]), getint(item[4]), getfloat(item[5]), getString(item[6]), getString(item[7]));
				stockHashtable.put(ID + "." + Exchange, tStock);
			} else {
				tStock = (Stock) stockHashtable.get(ID + "." + Exchange);
				tStock.UpdateSymbol(item[0], getString(item[1]), getString(item[2]), getString(item[3]), getint(item[4]), getfloat(item[5]), getString(item[6]), getString(item[7]));
			}
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("CloseParser Error:" + e.toString());
			// e.printStackTrace();
		}

	}
	
	private static String[] expand(String[] array, int size) {
	    String[] temp = new String[size];
	    System.arraycopy(array, 0, temp, 0, array.length);
	    for(int j = array.length; j < size; j++)
	        temp[j] = "";
	    return temp;
	}
	
	static String getString(String data)
    {
		try
		{
    	if ((data==null)||(data.length()==0)||(data=="")) return "";
    	return data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Frame Parser getfloat:"+e.toString());
			return  "";
		}
    }
	    
	static float getfloat(String data)
	    {
			try
			{
	    	if ((data==null)||(data.length()==0)||(data=="")) return (float)0.0;
	    	return Float.parseFloat(data);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.err.println("Frame Parser getfloat:"+e.toString());
				return  (float)0.0;
			}
	    }
	    
	static int getint(String data)
    {
		try
		{
    	if ((data==null)||(data.length()==0)||(data=="")) return 0;
    	return Integer.parseInt(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Frame Parser getint:"+e.toString());
			return  0;
		}
    }
	public void Parser(String FrameContent) {

		//String[] TokenResult = FrameContent.split("|");
		String TokenString;
		String[] item;
		int FieldID;
//		String Field;
		java.util.StringTokenizer vElement = new java.util.StringTokenizer(FrameContent, "|");

		//String Exch = "";
		
		//for (int x = 0; x < TokenResult.length; x++) {
		while( vElement.hasMoreTokens() )
		{
			TokenString =vElement.nextToken();
			item = TokenString.split("=");
			try{
				FieldID = Integer.parseInt(item[0]);
			}
			catch(Exception e)
			{
				System.err.println(" FiledID parsing error !"+item[0]+" FrameContent:"+FrameContent);
				return;
			}
			switch (FieldID) {
			case 1:
				ID = item[1];
				break;
			case 2:
				TradingDate = item[1];
				break;
			case 3:
				Settlement = item[1];
				break;
			case 4:
				PreClose = item[1];
				break;
			case 5:
				Open = item[1];
				break;
			case 6:
				High = item[1];
				break;
			case 7:
				Low = item[1];
				break;
			case 8:
				UpLimit = item[1];
				break;
			case 9:
				DownLimit = item[1];
				break;
			case 10:
				Time = item[1];
				break;
			case 11:
				Price = item[1];
				break;
			case 12:
				Bid = item[1];
				break;
			case 13:
				Ask = item[1];
				break;
			case 14:
				Volume = item[1];
				break;
			case 15:
				TotalVolume = item[1];
				break;
			case 16:
				PreTotalVolume = item[1];
				break;
			case 17:
				BestBid1 = item[1];
				break;
			case 18:
				BestBid2 = item[1];
				break;
			case 19:
				BestBid3 = item[1];
				break;
			case 20:
				BestBid4 = item[1];
				break;
			case 21:
				BestBid5 = item[1];
				break;
			case 22:
				BestAsk1 = item[1];
				break;
			case 23:
				BestAsk2 = item[1];
				break;
			case 24:
				BestAsk3 = item[1];
				break;
			case 25:
				BestAsk4 = item[1];
				break;
			case 26:
				BestAsk5 = item[1];
				break;
			case 27:
				BestBidSize1 = item[1];
				break;
			case 28:
				BestBidSize2 = item[1];
				break;
			case 29:
				BestBidSize3 = item[1];
				break;
			case 30:
				BestBidSize4 = item[1];
				break;
			case 31:
				BestBidSize5 = item[1];
				break;
			case 32:
				BestAskSize1 = item[1];
				break;
			case 33:
				BestAskSize2 = item[1];
				break;
			case 34:
				BestAskSize3 = item[1];
				break;
			case 35:
				BestAskSize4 = item[1];
				break;
			case 36:
				BestAskSize5 = item[1];
				break;
			case 37:
				TickIndex = item[1];
				break;
			case 38:
				Exch = item[1];
				break;
			default:
			}

		}
		//過濾不是預先設定市場的股票
		//Neglect the tick not in target market
		//if (Exchange.compareTo(Exch)!=0) return;
		//DataManager.handle_ticks_count++;
		/* System.out.println("Sotck:"+ID+"-"+TradingDate+"-"+Price.toString());
		if (ID.compareTo("0001")==0)
		{
			System.out.println("Sotck:"+ID+"-"+TradingDate+"-"+Price.toString());
		}*/
		
		/* 檢查是否要清盤 */
		/*if ((TradingDate!=null)&&(TradingDate.length()!=0))
		{
			if (!DateConvertor.thesameTradingDate(TradingDate, MarketTradingDate))
			{
				DataManager.CleanMarket();
				MarketTradingDate=TradingDate;
			}
		}
		*/
		
		
	}
	
	public void setMarketTradingDate(String tMT)
	{
		if ((tMT==null)||(tMT.length()==0))
			MarketTradingDate=TradingDate;
		else
			MarketTradingDate=tMT;
	}
	
	public String getMarketTradingDate()
	{
		return MarketTradingDate;
	}
	
	public boolean checkiffitDefaultMarket()
	{
		if (Exchange.compareTo(Exch)!=0) return false;
		return true;
	}
	
	public void SavetoDataStore(String FrameType)
	{
		int type=0;
		type = Integer.parseInt(FrameType);

//		int New = 0;
		tStock=m_SM.getStock(ID);
/*		if (!m_SM.containsKey(ID + "." + Exch)) {
			tStock = new Stock(ID, Exch, strOpenTime);
			
			//((Rule1)tStock.lr.get(0)).reset(rule1_bm, rule1_bd, rule1_per, rule1_filter, rule1_v);
			//((Rule2)tStock.lr.get(1)).reset(rule2_bm, rule2_per, rule2_v);
			//((Rule3)tStock.lr.get(2)).reset(rule3_rv,rule3_v);
			
			New = 1;
		} else {
			tStock = (Stock) m_SM.get(ID + "." + Exch);
		}
*/

		switch (type) {
		case 1:
			tStock.Sunrise(TradingDate, PreClose, UpLimit, DownLimit,
					PreTotalVolume, Exch);
			break;
		case 2:
			tStock.Tick(TradingDate, Time, Price, Bid, Ask, Volume,
					TotalVolume, BestBid1, BestBid2, BestBid3, BestBid4,
					BestBid5, BestAsk1, BestAsk2, BestAsk3, BestAsk4, BestAsk5,
					BestBidSize1, BestBidSize2, BestBidSize3, BestBidSize4,
					BestBidSize5, BestAskSize1, BestAskSize2, BestAskSize3,
					BestAskSize4, BestAskSize5, TickIndex, Exch);

			break;
		case 3:
			
			tStock.Refresh(TradingDate, Settlement, PreClose, Open, High, Low,
					UpLimit, DownLimit, Time, Price, Bid, Ask, Volume,
					TotalVolume, PreTotalVolume, BestBid1, BestBid2, BestBid3,
					BestBid4, BestBid5, BestAsk1, BestAsk2, BestAsk3, BestAsk4,
					BestAsk5, BestBidSize1, BestBidSize2, BestBidSize3,
					BestBidSize4, BestBidSize5, BestAskSize1, BestAskSize2,
					BestAskSize3, BestAskSize4, BestAskSize5, TickIndex, Exch);


			break;
		case 4:
			tStock.TickRefresh(TradingDate, Time, Price, Bid, Ask, Volume,
					TotalVolume, TickIndex, Exch);
		default:
			break;		}
//		if (New == 1)
//			m_SM.insert_Stock(ID + "." + Exch, tStock);
		return;
	}

}
