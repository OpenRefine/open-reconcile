package com.googlecode.openreconcile.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.openreconcile.client.ConfigDB;
import com.googlecode.openreconcile.client.datatypes.DatabaseData;
import com.googlecode.openreconcile.client.datatypes.SynonymData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.io.*;

// uncomment for logging.
//import java.util.logging.Level;
//import java.util.logging.Logger;


public class ConfigDBImpl extends RemoteServiceServlet implements ConfigDB{
// Uncomment this for logging
//	private static String nameOfLogger = ReconcileMatching.class.getName();
//	private static Logger myLogger = Logger.getLogger(nameOfLogger); 
	/**
	 *  Important note: the database name is saved in the servicenumber field
	 */
	private static final long serialVersionUID = -145830448759156312L;
	
	public DatabaseData myData;
	
	public static String DATA_FILE = DataStoreFile.DATA_FILE_NAME;

	/**
	 * Executes the SQL queries based on what is in the DatabaseData object handed to it
	 * if there is a table name specified, it will search for column names of that table,
	 * if there is no table name, it will search for table names.
	 * 
	@param inputs A DatabaseData object containing connection information about a database
	 *                     	                          
	@return An ArrayList of String objects with a "1" or a "0" in the first value, signifying 
	 * if the query was executed corrected, if it wasn't, the second String in the list will be the
	 * exception thrown. If everything went well the ArrayList will contain a "1" and then all of the
	 * desired names from the database. 
	 *  
	 */
	private ArrayList<String> executeSQL(DatabaseData inputs){
		ArrayList<String> result = new ArrayList<String>();
		Connection connection = null;
		String driverName=inputs.getDriver();
	    try {
				Class.forName(driverName);
			} catch (ClassNotFoundException e1) {
				result.add("0");
				result.add("Driver Class not found exception" +e1.toString());
			}
		    String username = inputs.username;
		    String password = inputs.password;
		    
		    try{
			    String url = inputs.getConnectionURL();
			    // Connect to the particular database in question
			    connection = DriverManager.getConnection(url, username, password);
			    connection.setAutoCommit(false);
			    String sqlStatement = null;
			    result.add("1");
			    // If the table name is empty, the table name is
			    // the focus of the search. There should never
			    // be a case where column names are desired but
			    // a table name isn't assigned yet.
			    if (inputs.tablename.isEmpty()){
			    	sqlStatement = inputs.getTableNamesQuery();
			    }else{
			    	sqlStatement = inputs.getColumnNamesQuery();
			    }
			    Statement stmt = connection.createStatement();
			    ResultSet rs = stmt.executeQuery(sqlStatement);
			    if(inputs.tablename.isEmpty()){
				    while (rs.next()) {
				    	String nextTerm;
				    	// I need to find a way to make this more
				    	// dynamic
				    	if (inputs.source.equals(DatabaseData.getOptions()[0])){
				    		nextTerm = rs.getString("owner")+"."+rs.getString("table_name");
				    	}else if (inputs.source.equals(DatabaseData.getOptions()[2])){
				    		nextTerm = inputs.name+"."+rs.getString(1);
				    	}else{
				    		nextTerm = rs.getString("table_name");
				    	}
				    	result.add(nextTerm);
				    }
			    }else{
			    	// Ditto, it seems with that it's hard to get away
			    	// from the hardcoding of particular names
			    	while (rs.next()) {
			    		String nextTerm;
				    	if (inputs.source.equals(DatabaseData.getOptions()[0])){
				    		nextTerm = inputs.tablename+"."+ rs.getString("column_name");
				    	}else{
				    		nextTerm = rs.getString("column_name");
				    	}
				    	result.add(nextTerm);
				    }
			    }
			    rs.close();
			    stmt.close();
			    connection.close();			   
			// catch and return any errors found
			}catch (SQLException e) {
				result.add("0");
				result.add("SQL Exception:"+e.toString());
				return (result);
		    }
			 
		 return result;
	}

	/**
	 * Calls the executeSQL function, see executeSQL
	 * 
	@param inputs A DatabaseData object containing connection information
	 *                      	                          
	@return The results from executeSQL, see above
	 *  
	 */	    
	@Override
	public ArrayList<String> getTables(DatabaseData inputs){
	    return executeSQL(inputs);
	}
	
	/**
	 * Calls the executeSQL function, see executeSQL
	 * 
	@param inputs A DatabaseData object containing connection information
	 *                      	                          
	@return The results from executeSQL, see above
	 *  
	 */	 
	@Override
	public ArrayList<String> getColumns(DatabaseData inputs) {
	    return executeSQL(inputs);
	}
	
	/**
	 * Gets a list of accessible tables in the database. 
	 * 
	@param inputs A DatabaseData object containing information to be added to the configuration file.
	 *                          	                          
	@return An ArrayList of Strings. The first entry of the ArrayList is a "1" or a "0",
	 *  a "1" signifies a successful execution. If there are any errors 
	 * the ArrayList will contain two entries, a "0" and the exception caught.
	 *  
	 */
	@Override
	public ArrayList<String> addEntry(DatabaseData inputs) {
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		ArrayList<String> result = new ArrayList<String>();
		// If the file doesn't exist, create it.
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				result.add("0");
				result.add(e.toString());
				return result;
			}
		}
		// Here the entry is tested to insure it's functional. If it throws an error
		// there's no point in trying to add it to the DB
		// Passing back the SQLException is the best way to 
		// show the user what exactly is wrong, as it tends to be 
		// pretty descriptive
		try {
			Class.forName(inputs.getDriver());	
		    String username = inputs.username;
		    String password = inputs.password;
		    String url = inputs.getConnectionURL();
		    Connection connection = DriverManager.getConnection(url, username, password);
		    connection.setAutoCommit(false);
		    String sqlStatement = inputs.getVocabQuery();
		    Statement stmt = connection.createStatement();
		    // This is going to be unused, but it's necessary to execute the Query. 
			ResultSet rs = stmt.executeQuery(sqlStatement);
		    rs.close();
		    stmt.close();
		    connection.close();
		} catch (ClassNotFoundException e) {
			result.add("0");
			result.add("ClassNotFound:"+e.toString());
			return (result);
		    // Could not find the database driver
		} catch (SQLException e) {
			result.add("0");
			result.add("SQL Exception: "+e.toString());
			return (result);
		    // Could not connect to the database
		}
		String addStat = "insert into DatabaseTable ( "+inputs.getColumnList() + 
				") values (" + inputs.getValuesList() +");";
	    try{
			String driverName = "org.sqlite.JDBC";
		    Class.forName(driverName);
		    Connection connection = DriverManager.getConnection("jdbc:sqlite:"+DataStoreFile.DATA_FILE_NAME);
		    Statement stmt = connection.createStatement();
		    // If the table doesn't exist, create it.
		    String sqlstat = "create table if not exists DatabaseTable (pkey INTEGER PRIMARY KEY, "+
		    inputs.getColumnNameList() + ");";
		    connection.setAutoCommit(true);
		    stmt.executeUpdate(sqlstat);
		    stmt.execute(addStat);
		    result.add("1");
		    result.add("Entry Added");
		    stmt.close();
		    connection.close();
		} catch (ClassNotFoundException e) {
			result.add("0");
			result.add("ClassNotFound");
		    // Could not find the database driver
		} catch (SQLException e) {
			result.add("0");
			result.add("SQL Exception: "+e.toString()+ " on sql statement "+addStat+ " please note that capitalization matters");
		}
		return result;
	}
	/**
	 * Returns all data in the configuration file 
	 *     	                          
	@return An ArrayList<String[]> of the data, each String[] contains all the 
	 * entries for one entry in the file.
	 *  
	 */
	@Override
	public ArrayList<String[]> getCurrent() {
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		ArrayList<String[]> result = new ArrayList<String[]>();
		if (!file.exists()){
			result= null;
			return result;
		}
	    try{
			String driverName = "org.sqlite.JDBC";
		    Class.forName(driverName);
		    Connection connection = DriverManager.getConnection("jdbc:sqlite:"+DataStoreFile.DATA_FILE_NAME);
		    Statement stmt = connection.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseTable");
		    int columnCount = rs.getMetaData().getColumnCount();
		    while(rs.next())
		    {
		        String[] row = new String[columnCount];
		        for (int i=0; i <columnCount ; i++)
		        {
		           row[i] = rs.getString(i + 1);
		        }
		        result.add(row);
		    }
		    rs.close();
		    stmt.close();
		    connection.close();
		} catch (ClassNotFoundException e) {
			String[] errors = new String[] {"0","ClassNotFound"};
			result.add(errors);
		    // Could not find the database driver
		} catch (SQLException e) {
			String[] errors = new String[] {"0","SQL Exception: "+e.toString()};
			result.add(errors);		
			}
		return result;
	}
	/**
	 * Deletes an entry out of the configuration file
	 * 
	@param primaryKey This is the primary key for the entry which is to be deleted.
	 *                    	                          
	@return An ArrayList<String> that will have 1 or 2 entries. The first entry will be either a "1" signaling a
	 * successful execution, or a "0" indicating an exception was thrown. The second entry will be the exception, if any, that
	 * was thrown.
	 *  
	 */
	@Override
	public ArrayList<String> deleteThisEntry(String primaryKey){
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		ArrayList<String> result = new ArrayList<String>();
		if (!file.exists()){
			result.add("0");
			result.add("Error reading DB");
			return result;
		}
		try{
			String driverName = "org.sqlite.JDBC";
		    Class.forName(driverName);
		    Connection connection = DriverManager.getConnection("jdbc:sqlite:"+DataStoreFile.DATA_FILE_NAME);
		    Statement stmt = connection.createStatement();
		    connection.setAutoCommit(true);
		    stmt.executeUpdate("DELETE FROM DatabaseTable WHERE pkey ='"+primaryKey+"';");
		    result.add("1");
		    stmt.close();
		    connection.close();
		} catch (ClassNotFoundException e) {
			result.add("0");
			result.add("ClassNotFound");
		    // Could not find the database driver
		} catch (SQLException e) {
			result.add("0");
			result.add("SQL Exception: "+e.toString());
		}
		return result;
	}
	/**
	 * Returns the DatabaseData object for a type
	 * 
	@param primaryKey This is the primary key for the entry to fetch results for.
	 *                    	                          
	@return An DatabaseData object for the desired type.
	 *  
	 */
	public DatabaseData getDBData(String primaryKey){
		DatabaseData thisDatabase = new DatabaseData();
		try{
		String driverName = "org.sqlite.JDBC";
	    Class.forName(driverName);
	    Connection connection = DriverManager.getConnection("jdbc:sqlite:"+DataStoreFile.DATA_FILE_NAME);
	    Statement stmt = connection.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT "+  thisDatabase.getColumnList() +" FROM DatabaseTable WHERE pkey ='"+ primaryKey +"'");
	    int columnCount = rs.getMetaData().getColumnCount();
	    String row[] = null;
	    while(rs.next())
	    {
	        row = new String[columnCount];
	        for (int i=0; i <columnCount ; i++)
	        {
	           row[i] = rs.getString(i + 1);
	        }
	    }
	    rs.close();
	    stmt.close();
	    connection.close();
	    boolean cap=false;
	    boolean pun = false;
	    if(row!=null && row.length>1){
		    if (row[9].equals("true"))
		    	cap = true;
		    if (row[10].equals("true"))
		    	pun = true;
		   thisDatabase = new DatabaseData(row[0], row[3], row[2], row[1], row[4], row[5], row[6], row[7], row[8], cap, pun);		
	    }
	    } catch (ClassNotFoundException e) {
	    	thisDatabase = null;
		} catch (SQLException e) {
			thisDatabase = null;
		}
	    return thisDatabase;
	}
	/**
	 * Returns the first ten values polled out the type for preview purposes
	 * 
	@param primaryKey This is the primary key for the entry to fetch results for.
	 *                    	                          
	@return An ArrayList<String> that will have 2 or 13 entries. The first entry will be either a "1" signaling a
	 * successful execution, or a "0" indicating an exception was thrown. The second entry will be the exception, if any, that
	 * was thrown. the rest of the entries, assuming it worked, will be terms of the type specified.
	 *  
	 */
	@Override
	public ArrayList<String> getPreview(String primaryKey) {
	    DatabaseData myData = getDBData(primaryKey);
	    ArrayList<String> result = new ArrayList<String>();
		if(myData != null && myData.source!= null){
			try {
				Class.forName(myData.getDriver());	
			    String username = myData.username;
			    String password = myData.password;
			    String url = myData.getConnectionURL();
			    Connection connection = DriverManager.getConnection(url, username, password);
			    connection.setAutoCommit(false);
			    String sqlStatement = myData.getVocabQuery();
			    Statement stmt = connection.createStatement();
			    ResultSet rs = stmt.executeQuery(sqlStatement);
			    result.add("1");
			    result.add(myData.vocabid);
			    while (rs.next() && result.size() < 13) {
			    	result.add(rs.getString("VOCABCOL"));
		    	}
			    rs.close();
			    stmt.close();
			    connection.close();
			} catch (ClassNotFoundException e) {
				result.add("0");
				result.add("ClassNotFound:"+e.toString());
			    // Could not find the database driver
			} catch (SQLException e) {
				result.add("0");
				result.add("vocab db SQL Exception: "+e.toString());
			}
		}else{
			result.add("0");
			result.add("error fetching database information");
		}
		return result;
	}
}
