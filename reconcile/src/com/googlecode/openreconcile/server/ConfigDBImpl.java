package com.googlecode.openreconcile.server;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.googlecode.openreconcile.common.ConfigDB;
import com.googlecode.openreconcile.common.DatabaseData;


public class ConfigDBImpl implements ConfigDB{
    private static String nameOfLogger = ReconcileMatching.class.getName();
    private static Logger myLogger = Logger.getLogger(nameOfLogger); 
    /**
     *  Important note: the database name is saved in the servicenumber field
     */
    private static final long serialVersionUID = -145830448759156312L;

    public DatabaseData myData;


    /**
     * Return a list of table names.
     * 
     * @param inputs
     *            A DatabaseData object containing connection information
     * 
     * @return the list of table names
     * 
     */   
    @Override
    public List<String> getTables(DatabaseData inputs){
        List<String>  result = new ArrayList<String>();
        return result;
    }

    /**
     * Return a list of column names for the given table.
     * 
     * @param inputs
     *            A DatabaseData object containing connection information
     * 
     * @return the list of column names.
     * 
     */
    @Override
    public List<String>  getColumns(DatabaseData inputs) {
        List<String>  result = new ArrayList<String>();
        return result;
    }

    /**
     * Add an entry to the database.
     * 
     * @param inputs
     *            A DatabaseData object containing information to be added to
     *            the configuration file.
     * @return An List of Strings. The first entry of the List is a "1" or a
     *         "0", a "1" signifies a successful execution. If there are any
     *         errors the List will contain two entries, a "0" and the exception
     *         caught.
     */
    @Override
    public boolean addEntry(DatabaseData inputs) {
        throw new UnsupportedOperationException("Add not supported");
    }

    /**
     * Returns all data in the configuration file
     * 
     * @return An List<String[]> of the data, each String[] contains all the
     *         entries for one entry in the file.
     */
    @Override
    public List<String[]> getCurrent() {
        File file = new File(ReconcileServlet.DATA_FILE_NAME);
        List<String[]> result = new ArrayList<String[]>();
        if (!file.exists()){
            result= null;
            return result;
        }

//        ResultSet rs = stmt.executeQuery("SELECT * FROM DatabaseTable");
//        int columnCount = rs.getMetaData().getColumnCount();
//        while(rs.next())
//        {
//            String[] row = new String[columnCount];
//            for (int i=0; i <columnCount ; i++)
//            {
//                row[i] = rs.getString(i + 1);
//            }
//            result.add(row);
//        }

        return result;
    }
    
    /**
     * Deletes an entry out of the configuration file
     * 
     * @param primaryKey
     *            This is the primary key for the entry which is to be deleted.
     * 
     * @throws FileNotFoundException 
     * 
     */
    @Override
    public void deleteThisEntry(String primaryKey) throws FileNotFoundException{
        File file = new File(ReconcileServlet.DATA_FILE_NAME);
        if (!file.exists()){
            throw new FileNotFoundException("Error reading database " + file);
        }

//        stmt.executeUpdate("DELETE FROM DatabaseTable WHERE pkey ='"+primaryKey+"';");
//        result.add("1");
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

//        ResultSet rs = stmt.executeQuery("SELECT "
//                + thisDatabase.getColumnList()
//                + " FROM DatabaseTable WHERE pkey ='" + primaryKey + "'");
//        int columnCount = rs.getMetaData().getColumnCount();
        String row[] = null;
//        while(rs.next())
//        {
//            row = new String[columnCount];
//            for (int i=0; i <columnCount ; i++)
//            {
//                row[i] = rs.getString(i + 1);
//            }
//        }

        boolean cap=false;
        boolean pun = false;
        if(row!=null && row.length>1){
            if (row[9].equals("true")) {
                cap = true;
            }
            if (row[10].equals("true")) {
                pun = true;
            }
            thisDatabase = new DatabaseData("foo","bar","foobar",cap,pun);		
        }

        return thisDatabase;
    }

    /**
     * Returns the first ten values polled out the type for preview purposes
     * 
     * @param primaryKey
     *            This is the primary key for the entry to fetch results for.
     * 
     @return An List<String> that will have 2 or 13 entries. The first entry
     *         will be either a "1" signaling a successful execution, or a "0"
     *         indicating an exception was thrown. The second entry will be the
     *         exception, if any, that was thrown. the rest of the entries,
     *         assuming it worked, will be terms of the type specified.
     * 
     */
    @Override
    public List<String> getPreview(String primaryKey) {
        DatabaseData myData = getDBData(primaryKey);
        List<String> result = new ArrayList<String>();
        if(myData != null /*&& myData.source!= null*/){

            String sqlStatement = myData.getVocabQuery();
//            Statement stmt = connection.createStatement();
//            ResultSet rs = stmt.executeQuery(sqlStatement);
//            result.add("1");
//            result.add(myData.vocabid);
//            while (rs.next() && result.size() < 13) {
//                result.add(rs.getString("VOCABCOL"));
//            }

        }else{
            throw new RuntimeException("error fetching database information");
        }
        return result;
    }
}
