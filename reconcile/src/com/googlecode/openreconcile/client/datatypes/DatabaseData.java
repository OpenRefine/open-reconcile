package com.googlecode.openreconcile.client.datatypes;

import java.io.Serializable;

public class DatabaseData implements Serializable{
/** This object is for database link information. Please modify this file
 * to add new support for another database system if desired. 
 * 
 **/
	private static final long serialVersionUID = -4679964462915868356L;
	// Add to this list if you add an option, as it is what is polled to generate the options
	// on the front-end, and used throughout the code, please add to the end of the array only!!
	private static final String[] CONNECTION_OPTIONS = {"Oracle", "PostgresSQL", "MySQL"};
	
	public final static String[] databaseCols = {"source", "name", "portnumber", 
		"servername","username","password", "tablename", "columnname", "vocabid",
		"casesensitive", "punctuationsensitive"};

	public String source;
	public String servername;
	public String portnumber;
	public String name;
	public String username;
	public String password;
	public String tablename;
	public String columnname;
	public String vocabid;
	public Boolean casesensitive;
	public Boolean punctuationsensitive;
	
	/**
	 * Constructor used for constructing an empty DatabaseData object
	 * 
	 */
	public DatabaseData(){
	}
	
	/**
	 * Constructor used for constructing a DatabaseData object when using the wizard method, it populates only the fields necessary to find more information.
	 * 
	@param mSource The name of the source type to be added to the configuration file.
	@param mName The service name or the database name depending on the type to be added to the configuration file.
	@param mPortnumber The port number to be added to the configuration file.
	@param mServername The server name  to be added to the configuration file.
	@param mUser The username to be added to the configuration file.
	@param mPass The password to be added to the configuration file.
	 *  
	 */
	public DatabaseData(String mSource,String mServername,String mPortnumber,String mName,
			String mUser,String mPass) {
		source = mSource;
		name = mName;
		portnumber = mPortnumber;
		servername = mServername;
		username = mUser;
		password = mPass;
		tablename = "";
	}
	/**
	 * Constructor used for constructing a DatabaseData object when using the manual, it populates all of the information
	 * 
	@param mSource The name of the source type to be added to the configuration file.
	@param mName The service name or the database name depending on the type to be added to the configuration file.
	@param mPortnumber The port number to be added to the configuration file.
	@param mServername The server name  to be added to the configuration file.
	@param mUser The username to be added to the configuration file.
	@param mPass The password to be added to the configuration file.
 	@param mTable The table name to be added to the configuration file.
 	@param mCol The column name to be added to the configuration file.
 	@param mVocab The VocabName/VocabID to be added to the configuration file.
 	@param caps Should capitalization be considered during term reconciliation?
 	@param punct Should punctuation be considered during reconciliation?
	 *  
	 *  
	 */
	public DatabaseData(String mSource,String mServername,String mPortnumber,String mName,
		String mUser,String mPass, String mTable, String mCol, String mVocab, 
		boolean caps, boolean punct){
		source = mSource;
		name = mName;
		portnumber = mPortnumber;
		servername = mServername;
		username = mUser;
		password = mPass;
		tablename = mTable;
		columnname = mCol;
		vocabid = mVocab;
		casesensitive = caps;
		punctuationsensitive = punct;
	}
	
	/**
	 * Returns a list of options for database types supported
	 *                         	                          
	@return A string[] containing a list of options set in this class
	 *  
	 */
	public static String[] getOptions(){
		return CONNECTION_OPTIONS;
	}

	/**
	 * Returns the appropriate connection driver based on the source type
	 *                      	                          
	@return A string with the driver's name
	 *  
	 */
	public String getDriver(){
		// this is used several places
		String driver= null;
		if (source.equals(CONNECTION_OPTIONS[0])){
		    driver = "oracle.jdbc.driver.OracleDriver";
		}else if (source.equals(CONNECTION_OPTIONS[1])){
			driver = "org.postgresql.Driver";
		}else if (source.equals(CONNECTION_OPTIONS[2])){
			driver = "com.mysql.jdbc.Driver";
		}
		return driver;
	}
	/**
	 * Returns the appropriate connection URL based on the source type
	 *                        	                          
	@return A string that produces the correct connection URL
	 *  
	 */	
	public String getConnectionURL(){
		// this is used several places
		String url=null;
		if (source.equals(CONNECTION_OPTIONS[0])){
		    url = "jdbc:oracle:thin:@//" + servername + ":" + portnumber + "/" + name;
		}else if (source.equals(CONNECTION_OPTIONS[1])){
			url = "jdbc:postgresql://"+servername+":"+portnumber+"/"+name;
		}else if (source.equals(CONNECTION_OPTIONS[2])){
			url = "jdbc:mysql://"+servername+":"+portnumber+"/"+name;
		}
		return url;
	}
	/**
	 * Returns the names for the columns of data stored in the configuration file
	 * This is done to keep the data all in one place.
	 *                   	                          
	@return A string of all of the names of columns in quotes
	 *  
	 */
	public String getColumnNameList(){
		String list = "";
		for (int i = 0; i < databaseCols.length; i++){
			list = list +"'"+ databaseCols[i]+"', ";
		}
		list = list.substring(0, list.length()-2);
		return list;
	}
	
	/**
	 * Returns the names for the columns of data stored in the configuration file
	 *              	                          
	@return A string of all of the names of columns
	 *  
	 */
	public String getColumnList(){
		String list = "";
		for (int i = 0; i < databaseCols.length; i++){
			list = list + databaseCols[i]+", ";
		}
		list = list.substring(0, list.length()-2);
		return list;
	}
	
	/**
	 * Returns the values that are to be stored in the configuration file
	 *                        	                          
	@return A string for adding data to the configuration file form the object
	 *  
	 */
	public String getValuesList(){
		String list = "'"+source +"', '"+ name +"', '"+ portnumber +"', '"+ servername +"', '"+ username +"', '"+ password +"', '"+ tablename +"', '"+ columnname +"', '"+ vocabid +"', '"+ casesensitive.toString() +"', '"+ punctuationsensitive.toString()+"'";
		return list;
	}
	/**
	 * Returns the appropriate query string to get column names from a database
	 * 
   	                          
	@return A string of valid SQL for a query of column names.
	 *  
	 */
	public String getColumnNamesQuery(){
		// this is used many places
		String query = null;
		if (source.equals(CONNECTION_OPTIONS[0])){
    		String cleanedTable = tablename.substring(tablename.indexOf(".")+1);
    		query = "select column_name from all_tab_columns where table_name = \'"+cleanedTable+"\'";
		}else if (source.equals(CONNECTION_OPTIONS[1])){
			query = "SELECT attname as column_name FROM pg_attribute, pg_class WHERE attrelid = pg_class.oid AND relname = '"+tablename+"' AND attnum > 0 ";		
		}else if (source.equals(CONNECTION_OPTIONS[2])){
			String cleanedTable = tablename.substring(tablename.indexOf(".")+1);
			query = "select column_name from information_schema.columns where table_name='"+cleanedTable+"'";
		}
		return query;
	}

	/**
	 * Returns the appropriate query string to get tables names from a database
	 * 
	 *                           	                          
	@return A string with a valid SQL query for retrieving table names.
	 *  
	 */
	public String getTableNamesQuery(){
		// This is used in ConfigDBImpl.executeSQL();
		String query = null;
		if (source.equals(CONNECTION_OPTIONS[0])){
			query = "SELECT owner, table_name FROM user_tab_privs";
		}else if (source.equals(CONNECTION_OPTIONS[1])){
			query = "select tablename as table_name from pg_tables WHERE pg_tables.schemaname = 'public'";
		}else if (source.equals(CONNECTION_OPTIONS[2])){
			query = "SHOW TABLES IN "+name;
		}
		return query;
	}
	/**
	 * Returns the appropriate query string to poll the database for vocabulary terms
	 * 
	 *                           	                          
	@return A string with a valid SQL query 
	 *  
	 */
	public String getVocabQuery(){
		// This is used in ConfigDBImpl getPreview() and DataManager getData()
		String query = null;
		if (source.equals(CONNECTION_OPTIONS[0]) || source.equals(CONNECTION_OPTIONS[2])){
			query = "SELECT distinct "+columnname+" as vocabCol FROM "+ tablename;
		}else if (source.equals(CONNECTION_OPTIONS[1])){
			query = "SELECT distinct \""+columnname+"\" as vocabCol FROM \""+ tablename+"\"";
		}
		return query;		
	}
}
