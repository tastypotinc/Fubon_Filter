package org.Fubon.Server.Utility;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateConvertor {

	static public Date getDate(String TradingDate,String TradingTime)
	{
		Calendar cal = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
		try {
			
		
			return df.parse(TradingDate+TradingTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cal.set(0, 0, 0, 0, 0, 0);
			return cal.getTime();
		}
	}
	// Format YYYYMMDD
	static public boolean thesameTradingDate(String TD1,String TD2)
	{
//		Calendar cal = new GregorianCalendar();
		try
		{
			int YYYY1,MM1,DD1,YYYY2,MM2,DD2;
			TD1=TD1.replace(' ', '0');

			if (TD1.length()!=8)
			{
				System.err.printf("DateConvetor.thesameTradingDate TD1 error strTradingTime format"+TD1+"\n");
				return false;
			}

	
			YYYY1=Integer.parseInt(TD1.substring(0, 4));
			MM1=Integer.parseInt(TD1.substring(4, 6));
			DD1=Integer.parseInt(TD1.substring(6, 8));
			
			TD2=TD2.replace(' ', '0');

			if (TD2.length()!=8)
			{
				System.err.printf("DateConvetor.thesameTradingDate TD2 error strTradingTime format"+TD2+"\n");
				return false;
			}

	
			YYYY2=Integer.parseInt(TD2.substring(0, 4));
			MM2=Integer.parseInt(TD2.substring(4, 6));
			DD2=Integer.parseInt(TD2.substring(6, 8));

			if ((YYYY1==YYYY2)&&(MM1==MM2)&&(DD1==DD2))
				return true;
			else
				return false;
		}
		catch (Exception e)
		{
			System.err.print("DateConvertor.thesameTradingDate: "+e.toString());
			return false;
		}
	}
	
	// Format YYYYMMDD
	static public boolean isTodayTradingDate(String TD1)
	{
		Calendar cal = new GregorianCalendar();
		try
		{
			int YYYY1,MM1,DD1,YYYY2,MM2,DD2;
			TD1=TD1.replace(' ', '0');

			if (TD1.length()!=8)
			{
				System.err.printf("DateConvetor.thesameTradingDate TD1 error strTradingTime format"+TD1+"\n");
				return false;
			}

	
			YYYY1=Integer.parseInt(TD1.substring(0, 4));
			MM1=Integer.parseInt(TD1.substring(4, 6));
			DD1=Integer.parseInt(TD1.substring(6, 8));
			
			YYYY2=cal.get(Calendar.YEAR);
			MM2=cal.get(Calendar.MONTH);
			DD2=cal.get(Calendar.DAY_OF_MONTH);
			if ((YYYY1==YYYY2)&&(MM1==MM2)&&(DD1==DD2))
				return true;
			else
				return false;
		}
		catch (Exception e)
		{
			System.err.print("DateConvertor.thesameTradingDate: "+e.toString());
			return false;
		}
	}
	static public Date getTradingTime(String TradingTime)
	{
		Calendar cal = new GregorianCalendar();
		try
		{
			int YYYY,MM,DD,HH,mm,ss;
			TradingTime=TradingTime.replace(' ', '0');
			if (TradingTime.length()==5) TradingTime="0"+TradingTime;  
			if (TradingTime.length()!=6) 
			{
				System.err.printf("DateConvetor.getTradingTime error strTradingTime format"+TradingTime+"\n");
				return cal.getTime();
			}

			HH=Integer.parseInt(TradingTime.substring(0, 2));
			mm=Integer.parseInt(TradingTime.substring(2, 4));
			ss=Integer.parseInt(TradingTime.substring(4, 5));
			YYYY=cal.get(Calendar.YEAR);
			MM=cal.get(Calendar.MONTH);
			DD=cal.get(Calendar.DAY_OF_MONTH);
			cal.set(YYYY, MM, DD, HH, mm, ss);
			return cal.getTime();
		}
		catch (Exception e)
		{
			System.err.print("DateConvertor.getTradingTime: "+e.toString());
			return cal.getTime();
		}
	}
	
	static public String getTickTime(Date TradingTime)
	{
		Calendar cal = new GregorianCalendar();
		try
		{
			String HH,mm,ss;
			cal.setTime(TradingTime);
			HH=totwodigit(Integer.valueOf(cal.get(Calendar.HOUR_OF_DAY)).toString());
			mm=totwodigit(Integer.valueOf(cal.get(Calendar.MINUTE)).toString());
			ss=totwodigit(Integer.valueOf(cal.get(Calendar.SECOND)).toString());
			
			return HH+":"+mm+":"+ss;
		}
		catch (Exception e)
		{
			System.err.print("DateConvertor.getTickTime: "+e.toString());
			return "00:00:00";
		}
	}
	
	static String totwodigit(String item)
	{
		if (item.length()==2) return item;
		if (item.length()==0) return "00";
		if (item.length()==1) return "0"+item;
		if (item.length()>2)  return item.substring(0,2);
		return "00";
	}
	
	
}
