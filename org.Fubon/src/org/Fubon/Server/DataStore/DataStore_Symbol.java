package org.Fubon.Server.DataStore;

import java.sql.ResultSet;
import java.sql.Statement;

//SQL Command
//drop table if exists Symbol;
//create table if not exists Symbol (ID VARCHAR(6) PRIMARY KEY , Name VARCHAR(20), SName VARCHAR(40), EName VARCHAR(20), SEName VARCHAR(40));
//insert or replace Symbol values (?, ?, ?, ?, ?);

public class DataStore_Symbol extends DataStorage{
	DataStore_Symbol()
	{
		super();
		try
		{
			//stat.executeUpdate("drop table if exists Symbol;");
		    stat.executeUpdate("create table if not exists Symbol (ID VARCHAR(6) PRIMARY KEY , Name VARCHAR(20), SName VARCHAR(40),CUR VARCHAR(5),Unit numeric(8,0),Reference numeric(6,2), EName VARCHAR(20), SEName VARCHAR(40),UpLimit numeric(6,2),DownLimit numeric(6,2));");
		    setStatement("insert or replace into Symbol values (?, ?, ?,? ,? ,? , ?, ?, ?, ?);");
		    setUpdateStatement("update or replace Symbol set UpLimit=? , DownLimit=? where ID=?;");

		}
		catch(Exception e)
		{
			System.err.println("Symbol Init Error:"+e.toString());
		}
	}
	
	void Update(float UpLimit,float DownLimit,String ID)
	{
		try
		{
			prep.setFloat(1, UpLimit);
			prep.setFloat(2, DownLimit);
			prep.setString(3, ID);
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

	
	void Insert(String ID,String Name,String SName,String CUR,int Unit,float Reference,String EName,String SEName)
	{
		try
		{
			prep.setString(1, ID);
			prep.setString(2, Name);
			prep.setString(3, SName);
			prep.setString(4, CUR);
			prep.setInt(5, Unit);
			prep.setFloat(6, Reference);
			prep.setString(7, EName);
			prep.setString(8, SEName);
			prep.setFloat(9, (float) 0.0);
			prep.setFloat(10, (float) 0.0);
			prep.addBatch();
		    conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			System.err.println("Symbol Insert Error:"+e.toString());
		}
	}
	
	ResultSet getDataSet()
	{
		try
		{
			Statement  querystat;
			ResultSet resultSet = null;  
			querystat= conn.createStatement();
			resultSet = querystat.executeQuery("SELECT * FROM Symbol;");  
			return resultSet;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("Symbol Insert Error:"+e.toString());
			return null;
		}

  
	}

}
