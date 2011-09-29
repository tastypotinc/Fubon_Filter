package org.Fubon.Server;

import java.io.File;
//import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class DataStore {
	 static final String DriverName =  "org.sqlite.JDBC";
	 static final String DBPath =	"jdbc:sqlite::memory:";
	 static String BackupDB = "";
	 //static final String DBPath =	"jdbc:sqlite:Fubon.db";
	 Connection conn;
	 Statement  stat;
	 PreparedStatement prep;
	 PreparedStatement updateprep;
	 DataStore()
	 {
		if (conn==null)
		{
			try
			{
				Calendar cal = new GregorianCalendar();
				Class.forName(DriverName);
				conn = DriverManager.getConnection(DBPath);
				stat = conn.createStatement();
				BackupDB= "Fubon"+((Integer)cal.get(Calendar.DAY_OF_MONTH)).toString()+".DB"; 

		    }
			catch(Exception e)
			{
				System.err.println("DataStore Init Error:"+e.toString());
			}
		}
	 }
	 
	 public boolean Backup()
	 {
/*		 try
			{
			 	ResultSet rs;
			 	stat.executeUpdate("attach '"+ BackupDB + "' as __extdb");
			 				 	rs = stat.executeQuery("select name from sqlite_master where type='table'");
                while(rs.next()) {
                	stat.executeUpdate("create table __extdb."+rs.getString("name")+" as select * from "+rs.getString("name"));
                 }
                stat.executeUpdate("detach __extdb");
			}
			catch(Exception e)
			{
				System.err.println("DataStore Backup Error:"+e.toString());
			}
*/
		 File file = new File(BackupDB);
		 file.delete();
         System.out.println("Loading " + file.getAbsolutePath() + " into memory");
         try {
                 // load the sqlite driver
                 Class.forName("org.sqlite.JDBC");
//                 Connection connFile;
                 Statement stmt;
                 ResultSet rs;
//                 ResultSet rs2;
  //               connFile = DriverManager.getConnection("jdbc:sqlite:"+BackupDB);
                 stmt = conn.createStatement();
  //               stmtFile = connFile.createStatement();
                 rs = null;

                 // attach local database to in-memory database
                 stmt.executeUpdate("ATTACH '" + file.getAbsolutePath() + "' AS src");

                 // copy table definition
                 rs = stat.executeQuery("select name from sqlite_master where type='table'");
                 while(rs.next()) {
                 	stat.executeUpdate("create table src."+rs.getString("name")+" as select * from "+rs.getString("name"));
                  }

  /*               String tableQuery = "SELECT sql FROM sqlite_master WHERE type='table';";
                 rs = stmt.executeQuery(tableQuery);
                 while(rs.next()) {
                         stmtFile.execute(rs.getString(1));
                 }
*/
                 // copy data
                 String tableNameQuery = "SELECT name FROM sqlite_master WHERE type='table'";
                 rs = stmt.executeQuery(tableNameQuery);
                 while(rs.next()) {
                         String copyDataQuery =
                                 "INSERT INTO " + rs.getString(1) + " SELECT * FROM src." + rs.getString(1);
                         stmt.execute(copyDataQuery);
                 }

                 // copy other definitions (i.e. indices)
 /*                String nonTableQuery = "SELECT sql FROM sqlite_master WHERE type!='table';";
                 rs = stmt.executeQuery(nonTableQuery);
                 while(rs.next()) {
                         stmtFile.execute(rs.getString(1));
                 }
*/
                 // detach local db
                 String detachStmt = "DETACH src";
                 stmt.execute(detachStmt);

                 // test if everything went well
                 String sqlQuery = "SELECT sql FROM src.sqlite_master";
                 String recordsQuery = "SELECT COUNT(*) FROM src.emissions";
                 rs = stmt.executeQuery(sqlQuery);
                 System.out.println("Database in memory has the following definition:");
                 while(rs.next())
                         System.out.println(rs.getString(1));
                 rs = stmt.executeQuery(recordsQuery);
                 while(rs.next())
                         System.out.println("Database contains " + rs.getInt(1) + " records");
//                 connFile.close();
         } catch (Exception e) {
                 e.printStackTrace();
                 return false;
         }
         return true;
	 }
	 
	 /**
	  * Open a connection to the database and copy it into RAM. This results
	  * in better performance for queries but also means that any changes
	  * will not be persistent.
	  */
	 public boolean openConnectionAndCopyDbInMemory() {
	         File file = new File(BackupDB);
	         System.out.println("Loading " + file.getAbsolutePath() + " into memory");
	         try {
	                 // load the sqlite driver
	                 Class.forName("org.sqlite.JDBC");
	                 Statement stmt;
	                 ResultSet rs;
	                 stmt = conn.createStatement();
	               
	                 rs = null;

	                 // attach local database to in-memory database
	                 stmt.executeUpdate("ATTACH '" + file.getAbsolutePath() + "' AS src");

	                 // copy table definition
	                 String tableQuery = "SELECT sql FROM src.sqlite_master WHERE type='table';";
	                 rs = stmt.executeQuery(tableQuery);
	                 if (rs.isAfterLast())
	                 {
	                	 // 沒有DB 不需 Copy 至 memory
	                	 String detachStmt = "DETACH src";
		                 stmt.execute(detachStmt);
	                	 return true;
	                 }	 
	                 while(rs.next()) {
	                         stmt.execute(rs.getString(1));
	                 }

	                 // copy data
	                 String tableNameQuery = "SELECT name FROM sqlite_master WHERE type='table'";
	                 rs = stmt.executeQuery(tableNameQuery);
	                 while(rs.next()) {
	                         String copyDataQuery =
	                                 "INSERT INTO " + rs.getString(1) + " SELECT * FROM src." + rs.getString(1);
	                         stmt.execute(copyDataQuery);
	                 }

	                 // copy other definitions (i.e. indices)
	                 String nonTableQuery = "SELECT sql FROM src.sqlite_master WHERE type!='table';";
	                 rs = stmt.executeQuery(nonTableQuery);
	                 while(rs.next()) {
	                         stmt.execute(rs.getString(1));
	                 }

	                 // detach local db
	                 String detachStmt = "DETACH src";
	                 stmt.execute(detachStmt);

	                 // test if everything went well
	                 String sqlQuery = "SELECT sql FROM sqlite_master";
	                 String recordsQuery = "SELECT COUNT(*) FROM emissions";
	                 rs = stmt.executeQuery(sqlQuery);
	                 System.out.println("Database in memory has the following definition:");
	                 while(rs.next())
	                         System.out.println(rs.getString(1));
	                 rs = stmt.executeQuery(recordsQuery);
	                 while(rs.next())
	                         System.out.println("Database contains " + rs.getInt(1) + " records");

	         } catch (Exception e) {
	                 e.printStackTrace();
	                 return false;
	         }
	         return true;
	 }
	 void setStatement(String statment)
	 {
		 try
		 {
			 prep = conn.prepareStatement(statment);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 System.err.println("DataStore setStatement Error:"+e.toString());
		 }
	 }
	 void setUpdateStatement(String statment)
	 {
		 try
		 {
			 updateprep = conn.prepareStatement(statment);
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 System.err.println("DataStore setupdateStatement Error:"+e.toString());
		 }
	 }
}
