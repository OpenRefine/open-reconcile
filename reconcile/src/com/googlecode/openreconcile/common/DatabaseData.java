package com.googlecode.openreconcile.common;

import java.io.Serializable;

public class DatabaseData implements Serializable{
    /** This object is for database link information. Please modify this file
     * to add new support for another database system if desired. 
     * 
     **/
    private static final long serialVersionUID = -4679964462915868356L;

    public final static String[] databaseCols = { "tablename", "columnname", "vocabid",
        "casesensitive", "punctuationsensitive"};


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
     * Constructor used for constructing a DatabaseData object when using the
     * manual, it populates all of the information.
     * 
     * @param mTable
     *            The table name to be added to the configuration file.
     * @param mCol
     *            The column name to be added to the configuration file.
     * @param mVocab
     *            The VocabName/VocabID to be added to the configuration file.
     * @param caps
     *            Should capitalization be considered during term
     *            reconciliation?
     * @param punct
     *            Should punctuation be considered during reconciliation?
     */
    public DatabaseData( String mTable, String mCol, String mVocab, 
            boolean caps, boolean punct){

        tablename = mTable;
        columnname = mCol;
        vocabid = mVocab;
        casesensitive = caps;
        punctuationsensitive = punct;
    }



    /**
     * Returns the names for the columns of data stored in the configuration
     * file This is done to keep the data all in one place.
     * 
     * @return A string of all of the names of columns in quotes
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
    public String getValuesList() {
        String list = "'" + tablename + "', '" + columnname + "', '" + vocabid
                + "', '" + casesensitive.toString() + "', '"
                + punctuationsensitive.toString() + "'";
        return list;
    }

    /**
     * Returns the appropriate query string to get column names from a database
     * 
     * @return A string of valid SQL for a query of column names.
     * 
     */
    public String getColumnNamesQuery(){
        return "select column_name from all_tab_columns where table_name = \'foo\'";
    }

    /**
     * Returns the appropriate query string to get tables names from a database
     * 
     * @return A string with a valid SQL query for retrieving table names.
     * 
     */
    public String getTableNamesQuery(){
        // This is used in ConfigDBImpl.executeSQL();
        String query = "SELECT owner, table_name FROM user_tab_privs";
        return query;
    }

    /**
     * Returns the appropriate query string to poll the database for vocabulary
     * terms
     * 
     * @return A string with a valid SQL query
     * 
     */
    public String getVocabQuery(){
        // This is used in ConfigDBImpl getPreview() and DataManager getData()
        String query = "SELECT distinct "+columnname+" as vocabCol FROM "+ tablename;
        return query;		
    }
}
