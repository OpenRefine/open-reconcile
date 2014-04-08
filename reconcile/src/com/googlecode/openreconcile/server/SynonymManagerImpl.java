package com.googlecode.openreconcile.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.openreconcile.common.SynonymData;
import com.googlecode.openreconcile.common.SynonymManager;

public class SynonymManagerImpl extends RemoteServiceServlet implements
        SynonymManager {
    /**
     * Important note: the database name is saved in the
     * DataStoreFile.DATA_FILE_NAME field
     */

    public SynonymData mySynonyms;

    private static final long serialVersionUID = -145830448759156312L;

    /**
     * Gets a list of accessible tables in the database.
     * 
     * @param inputs
     *            A DatabaseData object containing information to be added to
     *            the configuration file.
     * 
     @return An ArrayList of Strings. The first entry of the ArrayList is a "1"
     *         or a "0", a "1" signifies a successful execution. If there are
     *         any errors the ArrayList will contain two entries, a "0" and the
     *         exception caught.
     * 
     */
    public List<String> addEntry(SynonymData inputs) {
        File file = new File(ReconcileServlet.DATA_FILE_NAME);
        List<String> result = new ArrayList<String>();
        // If the file doesn't exist, create it.
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                result.add("0");
                result.add(e.toString());
                return result;
            }
        }

        String addStat = "insert into SubstitutionTable ( "
                + inputs.getColumnList() + ") values ("
                + inputs.getValuesList() + ");";
        DataManager myData = new DataManager(inputs.getDBtype());
        boolean termExists = false;
        for (int i = 0; i < myData.vocab.size(); i++) {
            if (myData.vocab.get(i).equals(inputs.getTo())) {
                termExists = true;
            }
        }
        List<String[]> existingterms = getCurrent(inputs.getDBtype());
        boolean fromExists = false;
        for (int i = 0; i < existingterms.size(); i++) {
            if (existingterms.get(i)[1].equals(inputs.getFrom())) {
                fromExists = true;
            }
        }
        // Don't bother creating a new entry if the to and from are the same
        // also don't add entries that won't point to entries in the vocab,
        // because there's really
        // no point.
        if (!inputs.getTo().equals(inputs.getFrom()) && termExists
                && !fromExists) {

            // If the table doesn't exist, create it.
            String sqlstat = "create table if not exists SubstitutionTable (pkey INTEGER PRIMARY KEY, fromterm UNIQUE, toterm, type)";
            // connection.setAutoCommit(true);
            // stmt.executeUpdate(sqlstat);
            // stmt.execute(addStat);

            result.add("1");
            result.add("Entry Added");

        } else {
            result.add("0");
            if (inputs.getTo().equals(inputs.getFrom())) {
                result.add("To and From are identical, substitution rule will have no effect");
            } else if (!termExists) {
                result.add("To term does not exists in the type library specified");
            } else if (fromExists) {
                result.add("From term does already has an entry only one entry per from term is permissible");
            } else {
                result.add("Unknown error");
            }
        }
        return result;
    }

    /**
     * Returns all data in the configuration file
     * 
     * @return An List<String[]> of the data, each String[] contains all the
     *         entries for one entry in the file.
     * 
     */
    @Override
    public List<String[]> getCurrent(String type) {
        File file = new File(ReconcileServlet.DATA_FILE_NAME);
        List<String[]> result = new ArrayList<String[]>();
        if (!file.exists()) {
            result = null;
            return result;
        }

//        Connection connection = DriverManager.getConnection("jdbc:sqlite:"
//                + DataStoreFile.DATA_FILE_NAME);
//        Statement stmt = connection.createStatement();
//        ResultSet rs = stmt
//                .executeQuery("SELECT * FROM SubstitutionTable WHERE type='"
//                        + type + "'");
//        int columnCount = rs.getMetaData().getColumnCount();
//        while (rs.next()) {
//            String[] row = new String[columnCount];
//            for (int i = 0; i < columnCount; i++) {
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
     @return An List<String> that will have 1 or 2 entries. The first entry will
     *         be either a "1" signaling a successful execution, or a "0"
     *         indicating an exception was thrown. The second entry will be the
     *         exception, if any, that was thrown.
     * 
     */
    @Override
    public List<String> deleteEntry(String primaryKey) {
        File file = new File(ReconcileServlet.DATA_FILE_NAME);
        List<String> result = new ArrayList<String>();
        if (!file.exists()) {
            result.add("0");
            result.add("Error reading DB");
            return result;
        }

        // Connection connection =
        // DriverManager.getConnection("jdbc:sqlite:"+DataStoreFile.DATA_FILE_NAME);
        // Statement stmt = connection.createStatement();
        // stmt.executeUpdate("DELETE FROM SubstitutionTable WHERE pkey ='"+primaryKey+"';");
        // result.add("1");

        return result;
    }

}
