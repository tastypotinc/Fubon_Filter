package org.Fubon.Server.DataStore;

import java.sql.Date;


//drop table if exists Close;
//drop index if exists Close_Index;
//create table if not exists Close (ID VARCHAR(6) , tDate Date,Close numeric(6,2), Volume numeric(8,0));
//CREATE UNIQUE INDEX if not exists  Close_Index ON Close ( tDate,  ID );
//
//insert or replace into Close values (?,?,?,?);


public class DataStore_Close extends DataStorage{
	DataStore_Close()
	{
		super();
		try
		{
			//stat.executeUpdate("drop table if exists Close;");
			//stat.executeUpdate("drop index if exists Close_Index;");
		    stat.executeUpdate("create table if not exists Close (ID VARCHAR(6) , tDate Date,Close numeric(6,2), Volume numeric(8,0),Open numeric(6,2),High numeric(6,2),Low numeric(6,2));");
		    stat.executeUpdate("CREATE UNIQUE INDEX if not exists  Close_Index ON Close ( tDate,  ID );");
		    setStatement("insert or replace into Close values (?,?,?,?,?,?,?);");
		    
		}
		catch(Exception e)
		{
			System.err.println("Close Init Error:"+e.toString());
		}
	}
	
	void Update(Date date,String ID,float Close,long Volume,float Open,float High,float Low)
	{
		try
		{
			prep.setString(1, ID);
			prep.setDate(2, date);
			prep.setFloat(3, Close);
			prep.setLong(4, Volume);
			prep.setFloat(5, Open);
			prep.setFloat(6, High);
			prep.setFloat(7, Low);
			prep.addBatch();
		    conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			System.err.println("Close Insert Error:"+e.toString());
		}
	}
	
	void Update(String Date,String ID,float Close,long Volume,float Open,float High,float Low)
	{
		try
		{
			prep.setString(1, ID);
			prep.setString(2, Date);
			prep.setFloat(3, Close);
			prep.setLong(4, Volume);
			prep.setFloat(5, Open);
			prep.setFloat(6, High);
			prep.setFloat(7, Low);
			prep.addBatch();
		    conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			System.err.println("Close Insert Error:"+e.toString());
		}
	}
	
	void Insert(String Date,String ID,String Close,String Volume,String Open,String High,String Low)
	{
		try
		{
			prep.setString(1, ID);
			prep.setString(2, Date);
			prep.setString(3, Close);
			prep.setString(4, Volume);
			prep.setString(5, Open);
			prep.setString(6, High);
			prep.setString(7, Low);
			prep.addBatch();
		    conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			System.err.println("Close Insert Error:"+e.toString());
		}
	}

}
