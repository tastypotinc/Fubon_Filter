package org.Fubon.Server;
import java.util.Date;

//drop table if exists Tick;
//drop index if exists Tick_Index;
//create table if not exists Tick (ID VARCHAR(6) ,tTime DateTime, Price numeric(6,2),Bid numeric(6,2),Volume numeric(8,0),TotalVolume numeric(8,0),BestBid1 numeric(6,2),BestBid2 numeric(6,2),BestBid3 numeric(6,2),BestBid4 numeric(6,2),BestBid5 numeric(6,2),BestAsk1 numeric(6,2),BestAsk2 numeric(6,2),BestAsk3 numeric(6,2),BestAsk4 numeric(6,2),BestAsk5 numeric(6,2),BestBidSize1 int,BestBidSize2 int,BestBidSize3 int,BestBidSize4 int,BestBidSize5 int,BestAskSize1 int,BestAskSize2 int,BestAskSize3 int,BestAskSize4 int,BestAskSize5 int,TickIndex int );
//CREATE UNIQUE INDEX if not exists  Tick_Index ON Tick ( tTime,TickIndex,  ID );
public class Tick {
	public Date		Time;
	public float	Price;
	public float	Bid;
	public float	Ask;
	public long		Volume;
	public long		TotalVolume;
	public float	BestBid1;
	public float	BestBid2;
	public float	BestBid3;
	public float	BestBid4;
	public float	BestBid5;
	public float	BestAsk1;
	public float	BestAsk2;
	public float	BestAsk3;
	public float	BestAsk4;
	public float	BestAsk5;
	public int		BestBidSize1;
	public int		BestBidSize2;
	public int		BestBidSize3;
	public int		BestBidSize4;
	public int		BestBidSize5;
	public int		BestAskSize1;
	public int		BestAskSize2;
	public int		BestAskSize3;
	public int		BestAskSize4;
	public int		BestAskSize5;
	public int		TickIndex;
	Tick() 
	{
	}
	public void setTime(String time)
	{
		Time=DateConvertor.getTradingTime(time);
	}

}
