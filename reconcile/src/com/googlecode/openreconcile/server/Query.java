package com.googlecode.openreconcile.server;

public class Query{

	// this is the term to be matched
	String query;
	String limit;
	String type;
	String type_strict;
	String[] properties;
	/**
	 * Constructor
	 * 	  
	 @param queryterm the term to be queried for
	 @param newlimit the limit of number of results that Google Refine wants back
	 @param newtype the type assigned to the query
	 @param typeStrict if the type should be obeyed strictly (note: this is ignored)
	 @param newproperties additional properties sent along with the query (note: this is ignored)
	 */
	public Query(String queryterm, String newlimit, String newtype,
			String typeStrict, String[] newproperties) {
		query = queryterm;
		limit = newlimit;
		type = newtype;
		type_strict = typeStrict;
		properties = newproperties;
	}
	public Query() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * returns the query term 
	 * @return  the string value for the term to be queried.
	 */
	public String getQuery(){
		return query;
	}
	/**
	 * returns the results limit
	 * @return  the string value for the maximum number of items requested in the results
	 */
	public String getLimit(){
		return limit;
	}
	/**
	 * returns the type the query is to be reconciled against
	 * @return  the string value for the type the term is to be reconciled against
	 */
	public String getType(){
		if (type !=null)
			return type;
		return null;
	}
	/**
	 * returns the type strictness the query
	 * 	  
	 @return the string value for if the type is to be strictly enforced or no (note this is not used)
	 */
	public String getTypeStrict(){
		return type_strict;
	}
	/**
	 * returns the properties the query
	 * 	  
	 @return the array of string values included as properties in the query
	 */
	public String[] properties(){
		return properties;
	}
	/**
	 * changes the query term to be reconciled
	 * @param querystr  new query term
	 */
	public void setQuery(String querystr) {
		query = querystr;
	}
}