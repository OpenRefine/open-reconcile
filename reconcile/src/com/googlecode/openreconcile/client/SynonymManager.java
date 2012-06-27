package com.googlecode.openreconcile.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.googlecode.openreconcile.client.datatypes.SynonymData;

public interface SynonymManager extends RemoteService {
	/**
	 * Adds an entry to the substitution database 
	 * 
	@param inputs A SubstitutionData object containing information to be added to the configuration file.
	 *                          	                          
	@return An ArrayList of Strings.  The first entry of the ArrayList is a "1" or a "0",
	 *  a "1" signifies a successful execution. If there are any errors 
	 * the ArrayList will contain two entries, a "0" and the exception caught.
	 *  
	 */
	public ArrayList<String> addEntry (SynonymData inputs);

	/**
	 * Returns all data in the configuration file 
	 *     	                          
	@return An ArrayList<String[]> of the data, each String[] contains all the 
	 * entries for one entry in the file.
	 *  
	 */
	public ArrayList<String[]> getCurrent (String primaryKey);
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
	public ArrayList<String> deleteEntry(String primaryKey);
	
	
}
