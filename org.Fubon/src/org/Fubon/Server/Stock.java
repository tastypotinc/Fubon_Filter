package org.Fubon.Server;
//import java.util.Arrays;
import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.GregorianCalendar;
import java.util.Map;
//import java.util.Set;
import java.util.TreeMap;

public class Stock {
	Map<Integer, Tick> ts;
	Map<Date, DayLine> dl;
	
	boolean DayLineListSorted=false;
	String	Symbol;
	String	TradingDate;
	String	Exch;
	// The information for current day trading
	String	Name;
	String	Short_Name;
	String	CUR;
	int	Unit;
	float	Reference;
	String	EName;
	String	Short_EName;
	String  Settlement;  //‘S’表示已經收盤. ‘ ‘表示還在盤中.
	
	float	Open;
	float	High;
	float	Low;
	float	Close;
	float	PreClose;   //(昨收)參考價
	float	UpLimit;
	float	DownLimit;
	long	PreTotalVolume;
	int		lastTickIndex;
	//The filed for the rule checking
	Date	OpenTime;
	Date	OpenPeriod;
	long 	OpenTotal=(long)0;
	long	AverageVolume=(long)0;
	Stock(String S,String ex,String strOpenTime)
	{	
		// Need to modify later for more object oriented about rules

		Symbol=S;
		Exch=ex;
		ts=new TreeMap<Integer,Tick>();
		dl=new TreeMap<Date,DayLine>();
		/*
		Rule1 r1=new Rule1(this);
		Rule2 r2=new Rule2(this);
		Rule3 r3=new Rule3(this);
		lr=new ArrayList<Rule>();
		lr.add(r1);
		lr.add(r2);
		lr.add(r3);
		*/
		OpenTime=DateConvertor.getTradingTime(strOpenTime);
	}
/*	
	@SuppressWarnings("unchecked")
	public void  setDayLineSorted()
	{
		Map.sort(dl,new DayLineComparator());
	}
	
*/

	public void UpdateSymbol(String ID,String uName,String SName,String uCUR,int uUnit,float uReference,String uEName,String SEName)
	{
		Name=uName;
		Short_Name=SName;
		CUR=uCUR;
		Unit=uUnit;
		Reference=uReference;
		EName=uEName;
		Short_EName=SEName;
	}
	
	public void checkRule()
	{

    	Rule	r;
    	Boolean	pass=true;
    	for (int i=0;i<FrameParser.lr.size();i++)
		{
    		r=FrameParser.lr.get(i);
    		r.SetStock(this);
    		if (!r.RuleChecking())
    		{
    			pass=false;
    			break;
    		}
		}
    	if (pass)
    	{
    		for (int i=0;i<FrameParser.lr.size();i++)
    		{
        		r=FrameParser.lr.get(i);
        		if ((r.isValid())&&(r.generateSignal()))
        		{
	        		//System.out.print(Symbol+"=>"+r.Signal().getSignal()+"\n");
	        		//DataManager.message=Symbol+"=>"+r.Signal()+"\n"+DataManager.message;
	        		DataManager.addSignal(r.Signal());
        		}

    		}
    	}
	}
	
	private void setOpenPeriod(int begin_mins)
	{
		Calendar lcal;
		lcal= new GregorianCalendar();
		lcal.setTime(OpenTime);
		lcal.add(Calendar.MINUTE, begin_mins);
		OpenPeriod=lcal.getTime();
		
	}
	
	//開盤後begin_mins分鐘總量
	public long getOpenTotal(int begin_mins)
	{
		try
		{
			Tick	tick;
			setOpenPeriod(begin_mins);
			//開盤後begin_mins分鐘的時間
			
			if (ts.size()>0)
			{
			    Object[] arrayTick =  ts.values().toArray();
			     
				for (int i=0;i<arrayTick.length;i++)
				{
					tick=(Tick)arrayTick[i];
					if (tick!=null)
					{
						if ((tick.Time.after(OpenPeriod)) && (OpenTotal!=0))
							break;
						else
							OpenTotal=tick.TotalVolume;
							
					}
				}
			}
			if (OpenTotal!=(long)0) return OpenTotal;
/*			for (int i=0;i<ts.size();i++)
			{
				if (ts.get(i)!=null)
				{
					if (ts.get(i).Time.after(OpenPeriod))
						break;
					else
						OpenTotal=ts.get(i).TotalVolume;
						
				}
			}
*/
		}
		catch(Exception e)
		{
			System.err.println("Stock.getOpenTotal: "+e.toString() );
		}
		return OpenTotal;
	}
	
	//前back_days日成交均量
	public long getCloseAverage()
	{
		try
		{
			
	
			//if (!DayLineListSorted) setDayLineSorted();
			if (AverageVolume!=(long)0) return AverageVolume;
			DayLine dayline;
			int     bd;
			
			if (dl.size()>0)
			{
				if (dl.size()<Rule1.back_days) bd=dl.size(); else bd=Rule1.back_days;
				Object[] arrayDayLine =  dl.values().toArray();
				for (int i=0;i<bd;i++)
				{
					dayline=(DayLine)arrayDayLine[dl.size()-i-1];
					AverageVolume=AverageVolume+dayline.Volume;
				}
				
				//for debug
				if (bd==0) 
				{
					System.err.println("Stock.getCloseAverage:"+Symbol+"沒有歷史收盤 新股上市 ?"+Long.toString(AverageVolume));
					AverageVolume=(long)0;
				}
				else
					AverageVolume=(long)(AverageVolume/bd);
			}

			
		}
		catch(Exception e)
		{
			System.err.println("Stock.getCloseAverage: "+e.toString() );
		}
		return AverageVolume;
	}
	
	//存有多少 ClosePrice Data  
	public int getCloseNumber()
	{
		return dl.size();
	}
	
//  back_mins分鐘內漲幅 的參考 價
	public float getChangeRateReference(int back_mins)
	{
		try
		{
			Tick lastTick,back_mins_Tick=null;
			Calendar lastTime,back_mins_Time;
			lastTick=getLastTick();
			if (lastTick==null) return 0;
			lastTime= new GregorianCalendar();
			back_mins_Time=  new GregorianCalendar();
			lastTime.setTime(lastTick.Time);
			lastTime.add(Calendar.MINUTE, -1*back_mins);
/*			for (int i=ts.size();i>0;i--)
			{
				back_mins_Tick=ts.get(i-1);
*/
			Object[] arrayTicks =  ts.values().toArray();
			for (int i=ts.size();i>0;i--)
			{
				back_mins_Tick=(Tick)arrayTicks[i-1];
				if (back_mins_Tick!=null) 
				{
					back_mins_Time.setTime(back_mins_Tick.Time);
					if (lastTime.compareTo(back_mins_Time)>=0)
					break;
				}
			}
			
			if ((back_mins_Tick==null)||(back_mins_Tick.Price==0)) return 0;
			
			return back_mins_Tick.Price;
		}
		catch(Exception e)
		{
			System.err.println("Stock.getChangeRate: "+e.toString() );
			return 0;
		}
		
	}
	//  back_mins分鐘內漲幅 以百分比呈現
	public float getChangeRate(int back_mins)
	{
		try
		{
			Tick lastTick,back_mins_Tick=null;
			Calendar lastTime,back_mins_Time;
			lastTick=getLastTick();
			if (lastTick==null) return 0;
			lastTime= new GregorianCalendar();
			back_mins_Time=  new GregorianCalendar();
			lastTime.setTime(lastTick.Time);
			lastTime.add(Calendar.MINUTE, -1*back_mins);
			//for (int i=lastTickIndex;i>0;i--)
			Object[] arrayTicks =  ts.values().toArray();
			for (int i=ts.size();i>0;i--)
			{
				back_mins_Tick=(Tick)arrayTicks[i-1];
				if (back_mins_Tick!=null) 
				{
					back_mins_Time.setTime(back_mins_Tick.Time);
					if (lastTime.compareTo(back_mins_Time)>=0)
					break;
				}
			}
			
			if ((back_mins_Tick==null)||(back_mins_Tick.Price==0)) return 0;
			
			return (lastTick.Price-back_mins_Tick.Price)/back_mins_Tick.Price;
		}
		catch(Exception e)
		{
			System.err.println("Stock.getChangeRate: "+e.toString() );
			return 0;
		}
		
	}
	
	//最後一筆單量
	long getLastTickVolume()
	{
		return ts.get(lastTickIndex).Volume;
	}
	
	
	//單量是否大於referVolume 張
	boolean getVolumeExceed(long referVolume)
	{
		try
		{
		if (ts.get(lastTickIndex).Volume > referVolume)
			return true;
		else
			return false;
		}
		catch(Exception e)
		{
			System.err.println("Stock.getVolumeExceed"+e.toString());
			return false;
		}
	}
	
	
	void AddTick(Tick tTick)
	{
		try
		{
			if (ts.containsKey(tTick.TickIndex))
			{
				ts.remove(tTick.TickIndex);
			}
			ts.put(tTick.TickIndex,tTick);
		}
		catch(Exception e)
		{
			System.err.println("Stock.AddTick: "+e.toString() );
		}
		try
		{
			//檢查是否符合條件
			checkRule();
		}
		catch(Exception e)
		{
			System.err.println("Stock.AddTick.checkRule: "+e.toString() );
		}
	}
	
	void AdddayLine(DayLine tDayLine)
	{
		if (dl.containsKey(tDayLine.tDate))
		{
			dl.remove(tDayLine.tDate);
		}
		dl.put(tDayLine.tDate,tDayLine);
	}
	
	public void Sunrise(String TD,String PC,String UL,String DL,String PTV,String Ex)
	{
		TradingDate=TD;
		PreClose=String2Number(PC,Ex);
		UpLimit=String2Number(UL,Ex);
		DownLimit=String2Number(DL,Ex);
		PreTotalVolume=Long.parseLong(PTV);
		Exch=Ex;
	}
	
	public void Tick(String TD,String Time,String Price,String Bid,String Ask,String Volume,
			String TotalVolume,String BestBid1,String BestBid2,String BestBid3,String BestBid4,
			String BestBid5,String BestAsk1,String BestAsk2,String BestAsk3,String BestAsk4,
			String BestAsk5,String BestBidSize1,String BestBidSize2,String BestBidSize3,
			String BestBidSize4,String BestBidSize5,String BestAskSize1,String BestAskSize2,
			String BestAskSize3,String BestAskSize4,String BestAskSize5,String TickIndex,String Ex)
	{
		Tick newTick;
		try
		{
			
			
			TradingDate=TD;
			newTick=new Tick();
			//System.err.print("Tick Insert ");
			newTick.setTime(Time);
			//System.err.print(" Pass0");
			newTick.Price =String2Number(Price,Ex);
			newTick.Bid=String2Number(Bid,Ex);
			newTick.Ask=String2Number(Ask,Ex);
			newTick.Volume=((Unit!=0)? Long.parseLong(Volume)*Unit : Long.parseLong(Volume));
			newTick.TotalVolume=((Unit!=0)? Long.parseLong(TotalVolume)*Unit : Long.parseLong(TotalVolume));
			//System.err.print(" Pass1");
			newTick.BestBid1=String2Number(BestBid1,Ex);
			newTick.BestBid2=String2Number(BestBid2,Ex);
			newTick.BestBid3=String2Number(BestBid3,Ex);
			newTick.BestBid4=String2Number(BestBid4,Ex);
			newTick.BestBid5=String2Number(BestAsk5,Ex);
			newTick.BestAsk1=String2Number(BestAsk1,Ex);
			newTick.BestAsk2=String2Number(BestAsk2,Ex);
			newTick.BestAsk3=String2Number(BestAsk3,Ex);
			newTick.BestAsk4=String2Number(BestAsk4,Ex);
			newTick.BestAsk5=String2Number(BestAsk5,Ex);
			//System.err.print(" Pass2");
			newTick.BestBidSize1=Integer.parseInt(BestBidSize1);
			newTick.BestBidSize2=Integer.parseInt(BestBidSize2);
			newTick.BestBidSize3=Integer.parseInt(BestBidSize3);
			newTick.BestBidSize4=Integer.parseInt(BestBidSize4);
			newTick.BestBidSize5=Integer.parseInt(BestBidSize5);
			newTick.BestAskSize1=Integer.parseInt(BestAskSize1);
			newTick.BestAskSize2=Integer.parseInt(BestAskSize2);
			newTick.BestAskSize3=Integer.parseInt(BestAskSize3);
			newTick.BestAskSize4=Integer.parseInt(BestAskSize4);
			newTick.BestAskSize5=Integer.parseInt(BestAskSize5);
			//System.err.print(" Pass3");
			newTick.TickIndex=Integer.parseInt(TickIndex);
	
			if (newTick.TickIndex>lastTickIndex) lastTickIndex=newTick.TickIndex;
			Exch=Ex;
			//System.err.print(" Pass4");
			/* 將Tick資料Insert到DB */
			/*FrameParser.tickDataStore.Insert(Symbol,TD, Time, Price, Bid, Ask, Volume,
						TotalVolume, BestBid1, BestBid2, BestBid3, BestBid4,
						BestBid5, BestAsk1, BestAsk2, BestAsk3, BestAsk4, BestAsk5,
						BestBidSize1, BestBidSize2, BestBidSize3, BestBidSize4,
						BestBidSize5, BestAskSize1, BestAskSize2, BestAskSize3,
						BestAskSize4, BestAskSize5, TickIndex, Ex);
			*/
			FrameParser.tickDataStore.Update(Symbol,TradingDate,Time,newTick.Price,newTick.Bid,newTick.Ask,newTick.Volume,
					newTick.TotalVolume,PreTotalVolume,newTick.BestBid1,newTick.BestBid2,newTick.BestBid3,newTick.BestBid4,
					newTick.BestBid5,newTick.BestAsk1,newTick.BestAsk2,newTick.BestAsk3,newTick.BestAsk4,
					newTick.BestAsk5,newTick.BestBidSize1,newTick.BestBidSize2,newTick.BestBidSize3,
					newTick.BestBidSize4,newTick.BestBidSize5,newTick.BestAskSize1,newTick.BestAskSize2,
					newTick.BestAskSize3,newTick.BestAskSize4,newTick.BestAskSize5,newTick.TickIndex,Ex);
			AddTick(newTick);
			//System.err.print(" Pass5");
		}
		catch(Exception e)
		{
			System.err.println("Tick Insert "+e.toString());
		}
	}
	
	public void memoryTick(String TD,String Time,float Price,float Bid,float Ask,long Volume,
			long TotalVolume,float BestBid1,float BestBid2,float BestBid3,float BestBid4,
			float BestBid5,float BestAsk1,float BestAsk2,float BestAsk3,float BestAsk4,
			float BestAsk5,int BestBidSize1,int BestBidSize2,int BestBidSize3,
			int BestBidSize4,int BestBidSize5,int BestAskSize1,int BestAskSize2,
			int BestAskSize3,int BestAskSize4,int BestAskSize5,int TickIndex,String Ex)
	{
		Tick newTick;
		try
		{
			/* 將Tick資料Insert到memory for 取得file cache 至memory 之用 */
			/* 因為是從 Database 中取得的 所以價格的 位數轉換就不需要了 */
			
			TradingDate=TD;
			newTick=new Tick();
			//System.err.print("Tick Insert ");
			newTick.setTime(Time);
			//System.err.print(" Pass0");
			newTick.Price =Price;
			newTick.Bid=Bid;
			newTick.Ask=Ask;
			newTick.Volume=Volume;
			newTick.TotalVolume=TotalVolume;
			//System.err.print(" Pass1");
			newTick.BestBid1=BestBid1;
			newTick.BestBid2=BestBid2;
			newTick.BestBid3=BestBid3;
			newTick.BestBid4=BestBid4;
			newTick.BestBid5=BestAsk5;
			newTick.BestAsk1=BestAsk1;
			newTick.BestAsk2=BestAsk2;
			newTick.BestAsk3=BestAsk3;
			newTick.BestAsk4=BestAsk4;
			newTick.BestAsk5=BestAsk5;
			//System.err.print(" Pass2");
			newTick.BestBidSize1=BestBidSize1;
			newTick.BestBidSize2=BestBidSize2;
			newTick.BestBidSize3=BestBidSize3;
			newTick.BestBidSize4=BestBidSize4;
			newTick.BestBidSize5=BestBidSize5;
			newTick.BestAskSize1=BestAskSize1;
			newTick.BestAskSize2=BestAskSize2;
			newTick.BestAskSize3=BestAskSize3;
			newTick.BestAskSize4=BestAskSize4;
			newTick.BestAskSize5=BestAskSize5;
			//System.err.print(" Pass3");
			newTick.TickIndex=TickIndex;
	
			if (newTick.TickIndex>lastTickIndex) lastTickIndex=newTick.TickIndex;
			Exch=Ex;
			//System.err.print(" Pass4");
			AddTick(newTick);
			//System.err.print(" Pass5");
		}
		catch(Exception e)
		{
			System.err.println("Tick Insert "+e.toString());
		}
	}
	public Tick getLastTick()
	{
		return ts.get(lastTickIndex);
	}
	
	public Tick getTick(int index)
	{
		return ts.get(index);
	}
	
	public void Refresh(String TD,String St,String PC,String O,String H,
			String L,String UL,String DL,String Time,String Price,String Bid,String Ask,String Volume,
			String TotalVolume,String PTV,String BestBid1,String BestBid2,String BestBid3,String BestBid4,
			String BestBid5,String BestAsk1,String BestAsk2,String BestAsk3,String BestAsk4,
			String BestAsk5,String BestBidSize1,String BestBidSize2,String BestBidSize3,
			String BestBidSize4,String BestBidSize5,String BestAskSize1,String BestAskSize2,
			String BestAskSize3,String BestAskSize4,String BestAskSize5,String TickIndex,String Ex)
	{

			Tick tempTick;
			boolean  NeedAdd;
			TradingDate=TD;
			Settlement=St;
			Open=String2Number(O,Ex);
			High=String2Number(H,Ex);
			Low=String2Number(L,Ex);
			PreTotalVolume=Long.parseLong(PTV);
			PreClose=String2Number(PC,Ex);
			UpLimit=String2Number(UL,Ex);
			DownLimit=String2Number(DL,Ex);
			//PreTotalVolume=Integer.parseInt(PTV);
			Exch=Ex;
			TradingDate=TD;
			if ((Time==null)||(Time.length()==0)||(Time.compareTo("0")==0))
				return;

			try
			{
				tempTick=ts.get(Integer.parseInt(TickIndex));
				if (tempTick!=null)
					NeedAdd=false;
				else
				{
					NeedAdd=true;
					tempTick=new Tick();
				}
			}
			catch (Exception e)
			{
				NeedAdd=true;
				tempTick=new Tick();
			}
		

		

			if (NeedAdd)
			{
				try
				{
				tempTick.setTime(Time);
				tempTick.Price =String2Number(Price,Ex);
				tempTick.Bid=String2Number(Bid,Ex);
				tempTick.Ask=String2Number(Ask,Ex);
				tempTick.Volume=Long.parseLong(Volume);
				tempTick.TotalVolume=Long.parseLong(TotalVolume);
				tempTick.BestBid1=String2Number(BestBid1,Ex);
				tempTick.BestBid2=String2Number(BestBid2,Ex);
				tempTick.BestBid3=String2Number(BestBid3,Ex);
				tempTick.BestBid4=String2Number(BestBid4,Ex);
				tempTick.BestAsk5=String2Number(BestAsk5,Ex);
				tempTick.BestAsk1=String2Number(BestAsk1,Ex);
				tempTick.BestAsk2=String2Number(BestAsk2,Ex);
				tempTick.BestAsk3=String2Number(BestAsk3,Ex);
				tempTick.BestAsk4=String2Number(BestAsk4,Ex);
				tempTick.BestAsk5=String2Number(BestAsk5,Ex);
				tempTick.BestBidSize1=Integer.parseInt(BestBidSize1);
				tempTick.BestBidSize2=Integer.parseInt(BestBidSize2);
				tempTick.BestBidSize3=Integer.parseInt(BestBidSize3);
				tempTick.BestBidSize4=Integer.parseInt(BestBidSize4);
				tempTick.BestBidSize5=Integer.parseInt(BestBidSize5);
				tempTick.BestAskSize1=Integer.parseInt(BestAskSize1);
				tempTick.BestAskSize2=Integer.parseInt(BestAskSize2);
				tempTick.BestAskSize3=Integer.parseInt(BestAskSize3);
				tempTick.BestAskSize4=Integer.parseInt(BestAskSize4);
				tempTick.BestAskSize5=Integer.parseInt(BestAskSize5);
				tempTick.TickIndex=Integer.parseInt(TickIndex);
		
				
				if (tempTick.TickIndex>lastTickIndex) lastTickIndex=tempTick.TickIndex;
				Exch=Ex;
				AddTick(tempTick);
				}
				catch(Exception e)
				{
					System.err.println("Tick Reresh"+e.toString());
					return;
				}
			}
			else
			{
				try
				{
				if ((Time!=null)&&(Time.compareTo("0")!=0))					tempTick.setTime(Time);
				if ((Price!=null)&&(Price.compareTo("0")!=0))				tempTick.Price =String2Number(Price,Ex);
				if ((Bid!=null)&&(Bid.compareTo("0")!=0))					tempTick.Bid=String2Number(Bid,Ex);
				if ((Ask!=null)&&(Ask.compareTo("0")!=0))					tempTick.Ask=String2Number(Ask,Ex);
				if ((Volume!=null)&&(Volume.compareTo("0")!=0))				tempTick.Volume=Long.parseLong(Volume);
				if ((TotalVolume!=null)&&(TotalVolume.compareTo("0")!=0))	tempTick.TotalVolume=Long.parseLong(TotalVolume);
				if ((BestBid1!=null)&&(BestBid1.compareTo("0")!=0))			tempTick.BestBid1=String2Number(BestBid1,Ex);
				if ((BestBid2!=null)&&(BestBid2.compareTo("0")!=0))			tempTick.BestBid2=String2Number(BestBid2,Ex);
				if ((BestBid3!=null)&&(BestBid3.compareTo("0")!=0))			tempTick.BestBid3=String2Number(BestBid3,Ex);
				if ((BestBid4!=null)&&(BestBid4.compareTo("0")!=0))			tempTick.BestBid4=String2Number(BestBid4,Ex);
				if ((BestBid5!=null)&&(BestBid5.compareTo("0")!=0))			tempTick.BestAsk5=String2Number(BestAsk5,Ex);
				if ((BestAsk1!=null)&&(BestAsk1.compareTo("0")!=0))			tempTick.BestAsk1=String2Number(BestAsk1,Ex);
				if ((BestAsk2!=null)&&(BestAsk2.compareTo("0")!=0))			tempTick.BestAsk2=String2Number(BestAsk2,Ex);
				if ((BestAsk3!=null)&&(BestAsk3.compareTo("0")!=0))			tempTick.BestAsk3=String2Number(BestAsk3,Ex);
				if ((BestAsk4!=null)&&(BestAsk4.compareTo("0")!=0))			tempTick.BestAsk4=String2Number(BestAsk4,Ex);
				if ((BestAsk5!=null)&&(BestAsk5.compareTo("0")!=0))			tempTick.BestAsk5=String2Number(BestAsk5,Ex);
				if ((BestBidSize1!=null)&&(BestBidSize1.compareTo("0")!=0))	tempTick.BestBidSize1=Integer.parseInt(BestBidSize1);
				if ((BestBidSize1!=null)&&(BestBidSize2.compareTo("0")!=0))	tempTick.BestBidSize2=Integer.parseInt(BestBidSize2);
				if ((BestBidSize1!=null)&&(BestBidSize3.compareTo("0")!=0))	tempTick.BestBidSize3=Integer.parseInt(BestBidSize3);
				if ((BestBidSize1!=null)&&(BestBidSize4.compareTo("0")!=0))	tempTick.BestBidSize4=Integer.parseInt(BestBidSize4);
				if ((BestBidSize1!=null)&&(BestBidSize5.compareTo("0")!=0))	tempTick.BestBidSize5=Integer.parseInt(BestBidSize5);
				if ((BestBidSize1!=null)&&(BestAskSize1.compareTo("0")!=0))	tempTick.BestAskSize1=Integer.parseInt(BestAskSize1);
				if ((BestBidSize1!=null)&&(BestAskSize2.compareTo("0")!=0))	tempTick.BestAskSize2=Integer.parseInt(BestAskSize2);
				if ((BestBidSize1!=null)&&(BestAskSize3.compareTo("0")!=0))	tempTick.BestAskSize3=Integer.parseInt(BestAskSize3);
				if ((BestBidSize1!=null)&&(BestAskSize4.compareTo("0")!=0))	tempTick.BestAskSize4=Integer.parseInt(BestAskSize4);
				if ((BestBidSize1!=null)&&(BestAskSize5.compareTo("0")!=0))	tempTick.BestAskSize5=Integer.parseInt(BestAskSize5);
				if ((TickIndex!=null)&&(TickIndex.compareTo("0")!=0))		tempTick.TickIndex=Integer.parseInt(TickIndex);		
				}
				catch(Exception e)
				{
					System.err.println("Tick Reresh"+e.toString());
					return;
				}
			}
			
			/* 將Tick資料Update到DB */
			if (Symbol.compareTo("FMCHN")==0)
			{
				System.err.println("Break !");
			}
			FrameParser.tickDataStore.Update(Symbol,TradingDate,Time,tempTick.Price,tempTick.Bid,tempTick.Ask,tempTick.Volume,
					tempTick.TotalVolume,PreTotalVolume,tempTick.BestBid1,tempTick.BestBid2,tempTick.BestBid3,tempTick.BestBid4,
					tempTick.BestBid5,tempTick.BestAsk1,tempTick.BestAsk2,tempTick.BestAsk3,tempTick.BestAsk4,
					tempTick.BestAsk5,tempTick.BestBidSize1,tempTick.BestBidSize2,tempTick.BestBidSize3,
					tempTick.BestBidSize4,tempTick.BestBidSize5,tempTick.BestAskSize1,tempTick.BestAskSize2,
					tempTick.BestAskSize3,tempTick.BestAskSize4,tempTick.BestAskSize5,tempTick.TickIndex,Ex);
					
			FrameParser.closeDataStore.Update(TradingDate,Symbol,Close,tempTick.TotalVolume,Open,High,Low);
			FrameParser.symbolDataStore.Update(UpLimit,DownLimit,Symbol);
			//
			//Refresh Don't need to insert Tick
			// System.out.println("Tick Reresh("+Symbol+")=>"+Time+"Pre_Close:"+PC);
		

	}
	
	public void TickRefresh(String TD,String Time,String Price,String Bid,String Ask,String Volume,String TotalVolume,String TickIndex,String Ex)
	{
		try
		{
			Tick tempTick;
			boolean  NeedAdd;
			TradingDate=TD;
			try
			{
				tempTick=ts.get(Integer.parseInt(TickIndex));
				if (tempTick!=null)
					NeedAdd=false;
				else
				{
					NeedAdd=true;
					tempTick=new Tick();
				}
			}
			catch (Exception e)
			{
				NeedAdd=true;
				tempTick=new Tick();
			}
			tempTick.setTime(Time);
			tempTick.Price =String2Number(Price,Ex);
			tempTick.Bid=String2Number(Bid,Ex);
			tempTick.Ask=String2Number(Ask,Ex);
			tempTick.Volume=Long.parseLong(Volume);
			tempTick.TotalVolume=Long.parseLong(TotalVolume);
			tempTick.TickIndex=Integer.parseInt(TickIndex);
		
			if (NeedAdd)
				AddTick(tempTick);
		}
		catch(Exception e)
		{
			System.err.println("Tick Reresh"+e.toString());
		}
	}
	
	float String2Number(String field,String Ex)
	{
		try
		{
		if (field==null) return 0;
		if (Ex.compareTo("TW")==0) return ((float)Integer.parseInt(field))/100;
		if (Ex.compareTo("TE")==0) return ((float)Integer.parseInt(field))/100;
		if (Ex.compareTo("TF")==0) return ((float)Integer.parseInt(field))/100;
		if (Ex.compareTo("FS")==0) return ((float)Integer.parseInt(field))/100;
		if (Ex.compareTo("HK")==0) return ((float)Integer.parseInt(field))/1000;
		if (Ex.compareTo("SH")==0) return ((float)Integer.parseInt(field))/1000;
		if (Ex.compareTo("SZ")==0) return ((float)Integer.parseInt(field))/1000;
		if (Ex.compareTo("KS")==0) return ((float)Integer.parseInt(field));
		if (Ex.compareTo("JP")==0) return ((float)Integer.parseInt(field));
		if (Ex.compareTo("US")==0) return ((float)Integer.parseInt(field))/100;
		return 0;
		}
		catch(Exception e)
		{
			System.err.println("Stock.String2Number: "+e.toString()+"\n");
			return 0;
		}
	}
	
	String simpleDate(Date date)
	{
		Calendar cal=new  GregorianCalendar();
		cal.setTime(date);
		//Calendar.MONTH is 0~11 therefore add 1
		return ((Integer)cal.get(Calendar.YEAR)).toString()+"-"+((Integer)(cal.get(Calendar.MONTH)+1)).toString()+"-"+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString();
	}
	
	String simpleDateTime(Date date)
	{
		Calendar cal=new  GregorianCalendar();
		cal.setTime(date);
		//Calendar.MONTH is 0~11 therefore add 1
		return ((Integer)cal.get(Calendar.YEAR)).toString()+"-"+((Integer)(cal.get(Calendar.MONTH)+1)).toString()+"-"+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString()+" "+((Integer)cal.get(Calendar.HOUR_OF_DAY)).toString()+":"+((Integer)cal.get(Calendar.MINUTE)).toString()+":"+((Integer)cal.get(Calendar.SECOND)).toString();
	}
	
	public String Dump()
	{
		String result="";
		Tick	tick;
		DayLine	dayline;
		result=result+"交易資訊:\n";
		result=result+"股票名稱 :"+Name+"\n";
		result=result+"Name :"+EName+"\n";
		result=result+"交易幣別 :"+CUR+"\n";
		result=result+"交易單位 :"+((Integer)Unit).toString()+"\n";
		result=result+"參考價 :"+((Float)Reference).toString()+"\n";
		result=result+"當日Tick:\n";
		if (ts.size()>0)
		{
			Object[] arrayTick = ts.values().toArray();
		     
			for (int i=0;i<arrayTick.length;i++)
			{
				tick=(Tick)arrayTick[i];
				result=result+" >"+simpleDateTime(tick.Time)+" :"+tick.Price+" :"+tick.Volume+" :"+tick.TotalVolume+"\n";
			}
		}
		
		result=result+"前幾日收盤價:\n";
		if (dl.size()>0)
		{
			Object[] arrayDayLine =  dl.values().toArray();
			for (int i=0;i<arrayDayLine.length;i++)
			{
				dayline=(DayLine)arrayDayLine[i];
				result=result+" >"+simpleDate(dayline.tDate)+" :"+dayline.Close+" :"+dayline.Volume+"\n";
			}
		}
		
		result=result+"條件資料:\n";
		Rule r;
		for (int i=0;i<FrameParser.lr.size();i++)
		{
    		r=FrameParser.lr.get(i);
    		r.SetStock(this);
    		result=result+r.RuleDumping()+"\n";
		}
		return result;
	}

}
