package org.Fubon.Server;


public class Rule3 implements Rule {
	static final int ID =1;
	final String name="條件三";
	static long	referVolume;	//單量是否大於referVolume 張
	Stock checking_stock;
	//boolean hasgenerated=false;
	boolean valid;
	
	Rule3()
	{

	}
	
	public void SetStock(Stock pstock)
	{
		synchronized (this) 
		{
			checking_stock=pstock;
		}
	}
	
	public void reset(long rv,boolean v)
	{
		synchronized (this) 
		{
			referVolume=rv;
			valid=v;
		}
	}
	
	public void init(Stock pstock)
	{
		synchronized (this) 
		{
			checking_stock=pstock;
		}
	}
	
	public boolean isValid()
	{
		synchronized (this) 
		{
			return valid;
		}
	}
	
	public boolean generateSignal()
	{
		synchronized (this) 
		{
			return RuleChecking();
		}
	}
	
	//單量大於                  張
	public Signal Signal()
	{
		synchronized (this) 
		{
			Signal tsignal;
			try
			{
				Tick tick;
				long	avgvolume;
				avgvolume = checking_stock.getCloseAverage();
				tick = checking_stock.getLastTick();
				tsignal=new Signal(checking_stock.Symbol+"=>"+name+"單量大於("+((Long)checking_stock.getLastTickVolume()).toString()+")"+((Long)referVolume).toString()+"張");
				tsignal.Rule = name;
				tsignal.Symbol = checking_stock.Symbol;
				tsignal.Time = DateConvertor.getTickTime(tick.Time);
				tsignal.Price = tick.Price;
				tsignal.change = (checking_stock.PreClose==0) ? 0 : (tick.Price - checking_stock.PreClose)/ checking_stock.PreClose;
				tsignal.volume = tick.Volume;
				tsignal.totlavolume = tick.TotalVolume;
				tsignal.avgvolume = avgvolume;
				return tsignal;
			}
			catch(Exception e)
			{
				System.err.println("Rule3.RuleChecking: "+e.toString() );
				tsignal=new Signal("");
				return tsignal;
			}
		}
	}
/*	
	public String Signal()
	{
		try
		{
			return name+"單量大於("+((Long)checking_stock.getLastTickVolume()).toString()+")"+((Long)referVolume).toString()+"張";
		}
		catch(Exception e)
		{
			System.err.println("Rule3.RuleChecking: "+e.toString() );
			return "";
		}
	}
*/
	public boolean RuleChecking()
	{
		synchronized (this) 
		{
			try
			{
				if (!valid) return true;
				if (checking_stock.getVolumeExceed(referVolume))
					return true;
				else
					return false;
			}
			catch(Exception e)
			{
				System.err.println("Rule3.RuleChecking: "+e.toString() );
				return false;
			}
		}
	}
	
	public String RuleDumping()
	{
		synchronized (this) 
		{
			try
			{
				String Result="條件三";
				if (!valid) Result=Result+":沒作用"; 
				else	Result=Result+":使用中";
				
				
				Result=Result+"最近一筆交易量"+((Long)checking_stock.getLastTickVolume()).toString();
				Result=Result+":需要大於"
				+((Long)referVolume).toString()
				+"張";

				return Result;
			}
			catch(Exception e)
			{
				System.err.println("Rule3.RuleChecking: "+e.toString() );
				return "條件三查詢錯誤: "+e.toString();
			}
		}
	}
	
	

	

	
}
