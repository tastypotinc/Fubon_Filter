package org.Fubon.Server.DataStore;

//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
//import java.util.Date;

//drop table if exists Tick;
//drop index if exists Tick_Index;
//create table if not exists Tick (ID VARCHAR(6) ,tTime DateTime, Price float,Bid float,Ask float,Volume numeric(8,0),TotalVolume numeric(8,0),BestBid1 float,BestBid2 float,BestBid3 float,BestBid4 float,BestBid5 float,BestAsk1 float,BestAsk2 float,BestAsk3 float,BestAsk4 float,BestAsk5 float,BestBidSize1 int,BestBidSize2 int,BestBidSize3 int,BestBidSize4 int,BestBidSize5 int,BestAskSize1 int,BestAskSize2 int,BestAskSize3 int,BestAskSize4 int,BestAskSize5 int,TickIndex int );
//CREATE UNIQUE INDEX if not exists  Tick_Index ON Tick ( tTime,TickIndex,  ID );
//insert or replace into Tick values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);

public class DataStore_Tick extends DataStorage{
	
	DataStore_Tick()
	{
		super();
		try
		{
			
			//stat.executeUpdate("drop table if exists Tick;");
			//stat.executeUpdate("drop index if exists Tick_Index;");
			
		    stat.executeUpdate("create table if not exists Tick (ID VARCHAR(6) ,TradingDate VARCHAR(8),Time VARCHAR(6), Price FLOAT,Bid FLOAT,Ask FLOAT,Volume BIGINT,TotalVolume BIGINT,BestBid1 FLOAT,BestBid2 FLOAT,BestBid3 FLOAT,BestBid4 FLOAT,BestBid5 FLOAT,BestAsk1 FLOAT,BestAsk2 FLOAT,BestAsk3 FLOAT,BestAsk4 FLOAT,BestAsk5 FLOAT,BestBidSize1 int,BestBidSize2 int,BestBidSize3 int,BestBidSize4 int,BestBidSize5 int,BestAskSize1 int,BestAskSize2 int,BestAskSize3 int,BestAskSize4 int,BestAskSize5 int,TickIndex int);");
		    stat.executeUpdate("CREATE UNIQUE INDEX if not exists  Tick_Index ON Tick ( TickIndex,  ID );");
		    setStatement("insert or replace into Tick values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		    setUpdateStatement("insert or replace into Tick(ID ,TradingDate, Time,Price,Bid,Ask,Volume,TotalVolume,BestBid1,BestBid2,BestBid3,BestBid4,BestBid5,BestAsk1,BestAsk2,BestAsk3,BestAsk4,BestAsk5,BestBidSize1,BestBidSize2,BestBidSize3,BestBidSize4,BestBidSize5,BestAskSize1,BestAskSize2,BestAskSize3,BestAskSize4,BestAskSize5,TickIndex) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		}
		catch(Exception e)
		{
			System.err.println("Tick Init Error:"+e.toString());
		}
	}
	
	void Update(String ID,String TradingDate,String Time,float Price,float Bid,float Ask,long Volume,
	long TotalVolume,float PTV,float BestBid1,float BestBid2,float BestBid3,float BestBid4,
	float BestBid5,float BestAsk1,float BestAsk2,float BestAsk3,float BestAsk4,
	float BestAsk5,int BestBidSize1,int BestBidSize2,int BestBidSize3,
	int BestBidSize4,int BestBidSize5,int BestAskSize1,int BestAskSize2,
	int BestAskSize3,int BestAskSize4,int BestAskSize5,int TickIndex,String Ex)
	{
		/**
		 *create table if not exists Tick (
		 *	ID VARCHAR(6) ,
		 *	tTime DateTime, 
		 *	Price float,
		 *	Bid float,
		 * 	Ask float,
		 *	Volume numeric(8,0),
		 *	TotalVolume numeric(8,0),
		 *	BestBid1 float,
		 *	BestBid2 float,
		 *	BestBid3 float,
		 *	BestBid4 float,
		 *	BestBid5 float,
		 *	BestAsk1 float,
		 *	BestAsk2 float,
		 *	BestAsk3 float,
		 *	BestAsk4 float,
		 *	BestAsk5 float,
		 *	BestBidSize1 int,
		 *	BestBidSize2 int,
		 *	BestBidSize3 int,
		 *	BestBidSize4 int,
		 *	BestBidSize5 int,
		 *	BestAskSize1 int,
		 *	BestAskSize2 int,
		 *	BestAskSize3 int,
		 *	BestAskSize4 int,
		 *	BestAskSize5 int,
		 *	TickIndex int );
		 *	
		 */
		
		try
		{
			updateprep.setString(1, ID);
			updateprep.setString(2, TradingDate);
			updateprep.setString(3, Time);
			updateprep.setString(4, Float.toString(Price));
			updateprep.setString(5, Float.toString(Bid));
			updateprep.setString(6, Float.toString(Ask));
			updateprep.setLong(7, Volume);
			updateprep.setLong(8, TotalVolume);
			updateprep.setString(9, Float.toString(BestBid1));
			updateprep.setString(10, Float.toString(BestBid2));
			updateprep.setString(11, Float.toString(BestBid3));
			updateprep.setString(12, Float.toString(BestBid4));
			updateprep.setString(13, Float.toString(BestBid5));
			updateprep.setString(14, Float.toString(BestAsk1));
			updateprep.setString(15, Float.toString(BestAsk2));
			updateprep.setString(16, Float.toString(BestAsk3));
			updateprep.setString(17, Float.toString(BestAsk4));
			updateprep.setString(18, Float.toString(BestAsk5));
			updateprep.setInt(19, BestBidSize1);
			updateprep.setInt(20, BestBidSize2);
			updateprep.setInt(21, BestBidSize3);
			updateprep.setInt(22, BestBidSize4);
			updateprep.setInt(23, BestBidSize5);
			updateprep.setInt(24, BestAskSize1);
			updateprep.setInt(25, BestAskSize2);
			updateprep.setInt(26, BestAskSize3);
			updateprep.setInt(27, BestAskSize4);
			updateprep.setInt(28, BestAskSize5);
			updateprep.setInt(29, TickIndex);
			//prep.setString(30, FrameType);
			updateprep.addBatch();
		    conn.setAutoCommit(false);
		    updateprep.executeBatch();
		    conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			System.err.println("Tick Update Error:"+e.toString());
		}
	}
	

	void Insert(String ID,String TD,String Time,String Price,String Bid,String Ask,String Volume,
					String TotalVolume,String BestBid1,String BestBid2,String BestBid3,String BestBid4,
					String BestBid5,String BestAsk1,String BestAsk2,String BestAsk3,String BestAsk4,
					String BestAsk5,String BestBidSize1,String BestBidSize2,String BestBidSize3,
					String BestBidSize4,String BestBidSize5,String BestAskSize1,String BestAskSize2,
					String BestAskSize3,String BestAskSize4,String BestAskSize5,String TickIndex,String Ex)
	{
		/**
		 *create table if not exists Tick (
		 *	ID VARCHAR(6) ,
		 *	tTime DateTime, 
		 *	Price float,
		 *	Bid float,
		 * 	Ask float,
		 *	Volume numeric(8,0),
		 *	TotalVolume numeric(8,0),
		 *	BestBid1 float,
		 *	BestBid2 float,
		 *	BestBid3 float,
		 *	BestBid4 float,
		 *	BestBid5 float,
		 *	BestAsk1 float,
		 *	BestAsk2 float,
		 *	BestAsk3 float,
		 *	BestAsk4 float,
		 *	BestAsk5 float,
		 *	BestBidSize1 int,
		 *	BestBidSize2 int,
		 *	BestBidSize3 int,
		 *	BestBidSize4 int,
		 *	BestBidSize5 int,
		 *	BestAskSize1 int,
		 *	BestAskSize2 int,
		 *	BestAskSize3 int,
		 *	BestAskSize4 int,
		 *	BestAskSize5 int,
		 *	TickIndex int );
		 *	
		 */
		
		try
		{
			prep.setString(1, ID);
			prep.setString(2, TD);
			prep.setString(3, Time);
			prep.setString(4, Price);
			prep.setString(5, Bid);
			prep.setString(6, Ask);
			prep.setString(7, Volume);
			prep.setString(8, TotalVolume);
			prep.setString(9, BestBid1);
			prep.setString(10, BestBid2);
			prep.setString(11, BestBid3);
			prep.setString(12, BestBid4);
			prep.setString(13, BestBid5);
			prep.setString(14, BestAsk1);
			prep.setString(15, BestAsk2);
			prep.setString(16, BestAsk3);
			prep.setString(17, BestAsk4);
			prep.setString(18, BestAsk5);
			prep.setString(19, BestBidSize1);
			prep.setString(20, BestBidSize2);
			prep.setString(21, BestBidSize3);
			prep.setString(22, BestBidSize4);
			prep.setString(23, BestBidSize5);
			prep.setString(24, BestAskSize1);
			prep.setString(25, BestAskSize2);
			prep.setString(26, BestAskSize3);
			prep.setString(27, BestAskSize4);
			prep.setString(28, BestAskSize5);
			prep.setString(29, TickIndex);
			//prep.setString(30, FrameType);
			prep.addBatch();
		    conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			System.err.println("Tick Insert Error:"+e.toString());
		}
	}
	
	ResultSet getDataSet()
	{
		try
		{
			Statement  querystat;
			ResultSet resultSet = null;  
			querystat= conn.createStatement();
			resultSet = querystat.executeQuery("SELECT * FROM Tick;");  
			return resultSet;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Symbol getDataSet Error:"+e.toString());
			return null;
		}

  
	}

}
