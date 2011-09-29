package org.Fubon.Server;


public class Rule2 implements Rule {
	static final int ID =1;
	static final String name="條件二";
	static int back_mins;	//  back_mins分鐘內
	static int percent;    //漲幅 以百分比呈現
	Stock checking_stock;
	//boolean hasgenerated=false;
	boolean valid;
	
	Rule2()
	{

	}
	
	public void SetStock(Stock pstock)
	{
		synchronized (this) 
		{
			checking_stock=pstock;
		}
	}
	
	public void reset(int bm, int per,boolean v)
	{
		synchronized (this) 
		{
			back_mins=bm;
			percent=per;
//			hasgenerated=false;
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
			if (!valid) return false;
			return RuleChecking();
/*			if (hasgenerated)
				return false;
			else
			{
				return RuleChecking();
			}
*/
		}
	}
	//             分鐘內漲幅>             %      
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
				tsignal=new Signal(checking_stock.Symbol+"=>"+name+" "+((Integer)back_mins).toString()+"分鐘內漲幅("+((Float)(checking_stock.getChangeRate(back_mins)*100)).toString()
				+"%)> "+((Integer)percent).toString()+"%  ");
//				hasgenerated=true;
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
				System.err.println("Rule2.RuleChecking: "+e.toString() );
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
			return name+" "+((Integer)back_mins).toString()+"分鐘內漲幅("+((Float)(checking_stock.getChangeRate(back_mins)*100)).toString()
			+"%)> "+((Integer)percent).toString()+"%  ";
		}
		catch(Exception e)
		{
			System.err.println("Rule2.RuleChecking: "+e.toString() );
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
				if (checking_stock.getChangeRate(back_mins)*100>(float)percent)
					return true;
				else
					return false;
			}
			catch(Exception e)
			{
				System.err.println("Rule2.RuleChecking: "+e.toString() );
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
				String Result="條件二";
				if (!valid) Result=Result+":沒作用"; 
				else	Result=Result+":使用中";
				
				
				Result=Result+((Integer)back_mins).toString()+"分鐘內漲幅"+((Float)checking_stock.getChangeRate(back_mins)).toString();
				Result=Result+":需要大於"
				+((Integer)percent).toString()
				+"% ";
				Result=Result+":比較價格"+((Float)checking_stock.getChangeRateReference(back_mins)).toString();

				return Result;
			}
			catch(Exception e)
			{
				System.err.println("Rule2.RuleChecking: "+e.toString() );
				return "條件二查詢錯誤: "+e.toString();
			}
		}
	}
	

	

	
}
