package com.googlecode.openreconcile.server;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.openreconcile.client.ConfigDB;
import com.googlecode.openreconcile.client.datatypes.DatabaseData;


public class ConfigDBImpl extends RemoteServiceServlet implements ConfigDB{
	private static String nameOfLogger = ReconcileMatching.class.getName();
	private static Logger myLogger = Logger.getLogger(nameOfLogger); 
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
	@return An List of String objects with a "1" or a "0" in the first value, signifying 
	 * if the query was executed corrected, if it wasn't, the second String in the list will be the
	 * exception thrown. If everything went well the List will contain a "1" and then all of the
	 * desired names from the database. 
	 *  
	 */
	private List<String> executeSQL(DatabaseData inputs){
		List<String> result = new ArrayList<String>();

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
//			    Statement stmt = connection.createStatement();
//			    ResultSet rs = stmt.executeQuery(sqlStatement);
			    if(inputs.tablename.isEmpty()){
//				    while (rs.next()) {
//				    	String nextTerm;
//				    	// I need to find a way to make this more
//				    	// dynamic
//				    	if (inputs.source.equals(DatabaseData.getOptions()[0])){
//				    		nextTerm = rs.getString("owner")+"."+rs.getString("table_name");
//				    	}else if (inputs.source.equals(DatabaseData.getOptions()[2])){
//				    		nextTerm = inputs.name+"."+rs.getString(1);
//				    	}else{
//				    		nextTerm = rs.getString("table_name");
//				    	}
//				    	result.add(nextTerm);
//				    }
			    }else{
			    	// Ditto, it seems with that it's hard to get away
			    	// from the hardcoding of particular names
//			    	while (rs.next()) {
//			    		String nextTerm;
//				    	if (inputs.source.equals(DatabaseData.getOptions()[0])){
//				    		nextTerm = inputs.tablename+"."+ rs.getString("column_name");
//				    	}else{
//				    		nextTerm = rs.getString("column_name");
//				    	}
//				    	result.add(nextTerm);
//				    }
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
	public List<String> getTables(DatabaseData inputs){
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
	public List<String> getColumns(DatabaseData inputs) {
	    return executeSQL(inputs);
	}
	
	/**
	 * Gets a list of accessible tables in the database. 
	 * 
	@param inputs A DatabaseData object containing information to be added to the configuration file.
	 *                          	                          
	@return An List of Strings. The first entry of the List is a "1" or a "0",
	 *  a "1" signifies a successful execution. If there are any errors 
	 * the List will contain two entries, a "0" and the exception caught.
	 *  
	 */
	@Override
	public List<String> addEntry(DatabaseData inputs) {
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		List<String> result = new ArrayList<String>();
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

		    String sqlStatement = inputs.getVocabQuery();
//		    Statement stmt = connection.createStatement();
//		    // This is going to be unused, but it's necessary to execute the Query. 
//			ResultSet rs = stmt.executeQuery(sqlStatement);

		String addStat = "insert into DatabaseTable ( "+inputs.getColumnList() + 
				") values (" + inputs.getValuesList() +");";

		    // If the table doesn't exist, create it.
		    String sqlstat = "create table if not exists DatabaseTable (pkey INTEGER PRIMARY KEY, "+
		    inputs.getColumnNameList() + ");";
//		    connection.setAutoCommit(true);
//		    stmt.executeUpdate(sqlstat);
//		    stmt.execute(addStat);
		    result.add("1");
		    result.add("Entry Added");

		return result;
	}
	/**
	 * Returns all data in the configuration file 
	 *     	                          
	@return An List<String[]> of the data, each String[] contains all the 
	 * entries for one entry in the file.
	 *  
	 */
	@Override
	public List<String[]> getCurrent() {
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		List<String[]> result = new ArrayList<String[]>();
		if (!file.exists()){
			result= null;
			return result;
		}
		
//		    ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseTable");
//		    int columnCount = rs.getMetaData().getColumnCount();
//		    while(rs.next())
//		    {
//		        String[] row = new String[columnCount];
//		        for (int i=0; i <columnCount ; i++)
//		        {
//		           row[i] = rs.getString(i + 1);
//		        }
//		        result.add(row);
//		    }

		return result;
	}
	/**
	 * Deletes an entry out of the configuration file
	 * 
	@param primaryKey This is the primary key for the entry which is to be deleted.
	 *                    	                          
	@return An List<String> that will have 1 or 2 entries. The first entry will be either a "1" signaling a
	 * successful execution, or a "0" indicating an exception was thrown. The second entry will be the exception, if any, that
	 * was thrown.
	 *  
	 */
	@Override
	public List<String> deleteThisEntry(String primaryKey){
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		List<String> result = new ArrayList<String>();
		if (!file.exists()){
			result.add("0");
			result.add("Error reading DB");
			return result;
		}

//		    stmt.executeUpdate("DELETE FROM DatabaseTable WHERE pkey ='"+primaryKey+"';");
//		    result.add("1");

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

//	    ResultSet rs = stmt.executeQuery("SELECT "+  thisDatabase.getColumnList() +" FROM DatabaseTable WHERE pkey ='"+ primaryKey +"'");
//	    int columnCount = rs.getMetaData().getColumnCount();
	    String row[] = null;
//	    while(rs.next())
//	    {
//	        row = new String[columnCount];
//	        for (int i=0; i <columnCount ; i++)
//	        {
//	           row[i] = rs.getString(i + 1);
//	        }
//	    }

	    boolean cap=false;
	    boolean pun = false;
	    if(row!=null && row.length>1){
		    if (row[9].equals("true")) {
                cap = true;
            }
		    if (row[10].equals("true")) {
                pun = true;
            }
		   thisDatabase = new DatabaseData(row[0], row[3], row[2], row[1], row[4], row[5], row[6], row[7], row[8], cap, pun);		
	    }

	    return thisDatabase;
	}
	/**
	 * Returns the first ten values polled out the type for preview purposes
	 * 
	@param primaryKey This is the primary key for the entry to fetch results for.
	 *                    	                          
	@return An List<String> that will have 2 or 13 entries. The first entry will be either a "1" signaling a
	 * successful execution, or a "0" indicating an exception was thrown. The second entry will be the exception, if any, that
	 * was thrown. the rest of the entries, assuming it worked, will be terms of the type specified.
	 *  
	 */
	@Override
	public List<String> getPreview(String primaryKey) {
	    DatabaseData myData = getDBData(primaryKey);
	    List<String> result = new ArrayList<String>();
		if(myData != null && myData.source!= null){

		    String sqlStatement = myData.getVocabQuery();
//		    Statement stmt = connection.createStatement();
//		    ResultSet rs = stmt.executeQuery(sqlStatement);
//		    result.add("1");
//		    result.add(myData.vocabid);
//		    while (rs.next() && result.size() < 13) {
//		        result.add(rs.getString("VOCABCOL"));
//		    }

		}else{
			result.add("0");
			result.add("error fetching database information");
		}
		return result;
	}
}
