package com.googlecode.openreconcile.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.openreconcile.common.DatabaseData;
import com.googlecode.openreconcile.common.SynonymData;

public class DataManager {

	// holds the database connection information
	public DatabaseData myData;
	
	public SynonymData mySynonyms;
	// holds the collection of terms from the data source
	public List<String> vocab;
	// holds a map of the terms that are synonyms for the type
	public Map<String, String> subMap;
	/**
	 * Constructor, it accesses the configuration file and populates the DatabaseData object based on that, and calls a function to populate the vocab list.
	 * 
	@param  queryType The type from the query string
	 *  
	**/	
	DataManager(String queryType){
		String[] row = null;
	    Boolean cap= false, pun= false;

//		    // pull out data to create DatabaseData object
//		    ResultSet rs = stmt.executeQuery("SELECT "+  myData.getColumnList() +" FROM DatabaseTable WHERE vocabid ='"+ queryType +"'");
//		    int columnCount = rs.getMetaData().getColumnCount();
//		    while(rs.next())
//		    {
//		        row = new String[columnCount];
//		        for (int i=0; i <columnCount ; i++)
//		        {
//		           row[i] = rs.getString(i + 1);
//		        }
//		    }

		    if(row!=null && row.length>1){
			    if (row[9].equals("true")) {
                    cap = true;
                }
			    if (row[10].equals("true")) {
                    pun = true;
                }
			    myData = new DatabaseData(row[0], row[3], row[2], row[1], row[4], row[5], row[6], row[7], row[8], cap, pun);

		    }

	    vocab = new ArrayList<String>();
	    // get the vocab and the synonym map  for the type
		if(row != null && row[0] != null && row[10]!=null){
			getData();
			getSynonyms(queryType);
		}
	}
	/**
	 * Populates the Map of Strings that is the substitution table. It's called only at the end of the constructor and is private.
	 * 
	 *  
	**/		
	private void getSynonyms(String queryType){
		subMap = new HashMap<String,String>();

//		    ResultSet rs = stmt.executeQuery("SELECT fromterm AS FROMTERM, toterm AS TOTERM FROM SubstitutionTable WHERE type ='"+ queryType +"'");
//		    while(rs.next())
//		    {
//		    	// if the terms are both punctuation sensitive
//		    	// and case sensitive, don't change anything from
//		    	// the term, if it's only punctuation sensitive, 
//		    	// convert everything to lower case. If it's only
//		    	// case sensitive, take out all punctuation
//		    	// if it's not sensitive to anything, take out all 
//		    	// non alphanumeric characters and convert to lower
//		    	// case
//		    	String fromterm = rs.getString("FROMTERM");
//		    	String toterm = rs.getString("TOTERM");
//		    	subMap.put(fromterm, toterm);
//		    	if (punctSensitive()){
//		    		subMap.put(fromterm.toLowerCase(), toterm);
//		    	}else if (capsSensitive()){
//		    		subMap.put(fromterm.replaceAll("[\\W_]", ""), toterm);
//		    	}else{
//		    		subMap.put(fromterm.replaceAll("[\\W_]", "").toLowerCase(), toterm);
//		    	}
//		    }

	}
	
	/**
	 * Populates the List of Strings that is the vocabulary. It's called only at the end of the constructor and is private.
	 *  
	**/	
	private void getData(){
	    if (myData.source!=null){

//	        String sqlStatement = myData.getVocabQuery();
//	        Statement stmt = connection.createStatement();
//	        ResultSet rs = stmt.executeQuery(sqlStatement);
//	        while (rs.next()) {
//	            vocab.add(rs.getString("VOCABCOL"));
//	        }	

	    }
	}
	/**
	 * Returns the boolean value stored in the configuration file for sensitivity to case.
	 *                       	                          
	@return The value of the DatabaseData for the type, if it is supposed to be evaluated in a manner that is case sensitive or not
	 *  
	**/	
	public boolean capsSensitive(){
		return myData.casesensitive;
	}
	
	/**
	 * Returns the boolean value stored in the configuration file for sensitivity to punctuation.
	 *                       	                          
	@return The value of the DatabaseData for the type, if it is supposed to be evaluated in a manner that is punctuation sensitive or not
	 *  
	**/		
	public boolean punctSensitive(){
		return myData.punctuationsensitive;
	}
}