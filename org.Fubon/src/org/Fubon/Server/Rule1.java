package org.Fubon.Server;




public class Rule1 implements Rule {
	static final int ID =1;
	static final String name="條件一";
	static int	begin_mins;	//the totoal volume after [begin_min]
	static int back_days;  //the [back_days] avarage volume
	static int percent;    //the percent of the [back_days] avarage volume
	Stock checking_stock;
//	boolean hasgenerated=false;
	boolean filter=false;
	boolean valid;
	
	Rule1()
	{
		
	}
	
	public void SetStock(Stock pstock)
	{
		synchronized (this) 
		{
			checking_stock=pstock;
		}
	}
	
	public void reset(int bm,int bd, int per,boolean f,boolean v)
	{
		synchronized (this) 
		{
			begin_mins=bm;
			back_days=bd;
			percent=per;
//			hasgenerated=false;
			filter=f;
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
	
	// 開盤後             分鐘總量>前              日成交均量             %         
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
				tsignal=new Signal("開盤後"+((Integer)begin_mins).toString()+"分鐘總量("+((Long)checking_stock.getOpenTotal(begin_mins)).toString()
				+")>前"+((Integer)back_days).toString()+"日成交均量 ("+Long.toString(avgvolume)+")"+((Integer)percent).toString()+"%  ");
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
				System.err.println("Rule1.Signal: "+e.toString() );
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
			return name+"開盤後"+((Integer)begin_mins).toString()+"分鐘總量("+((Long)checking_stock.getOpenTotal(begin_mins)).toString()
			+")>前"+((Integer)back_days).toString()+"日成交均量 ("+((Long)checking_stock.getCloseAverage(back_days)).toString()+")"+((Integer)percent).toString()+"%  ";
		}
		catch(Exception e)
		{
			System.err.println("Rule1.Signal: "+e.toString() );
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
				if (filter)
				{
					if (checking_stock.getCloseNumber()==0) return false;
				}	
				if (checking_stock.getOpenTotal(begin_mins)*100>checking_stock.getCloseAverage()*percent)
					return true;
				else
					return false;
			}
			catch(Exception e)
			{
				System.err.println("Rule1.RuleChecking: "+e.toString() );
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
				String Result="條件一";
				if (!valid) Result=Result+":沒作用"; 
				else	Result=Result+":使用中";
				if (filter)
				{
					if (checking_stock.getCloseNumber()==0) Result=Result+":過濾掉";
					else 	Result=Result+":符合過濾條件";
				}	
				
				Result=Result+":開盤"+((Integer)begin_mins).toString()+"分鐘交易量"+((Long)checking_stock.getOpenTotal(begin_mins)).toString();
				Result=Result+":前"
				+((Integer)back_days).toString()
				+"日均量"
				+((Long)checking_stock.getCloseAverage()).toString()
				+"百分比"
				+((Integer)percent).toString()
				+"% "
				+((Long)(checking_stock.getCloseAverage()*percent/100)).toString();

				return Result;
			}
			catch(Exception e)
			{
				System.err.println("Rule1.RuleChecking: "+e.toString() );
				return "條件一查詢錯誤: "+e.toString();
			}
		}
	}


	

	
}
