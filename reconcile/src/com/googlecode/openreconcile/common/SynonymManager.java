package com.googlecode.openreconcile.common;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SynonymManager extends RemoteService {
    /**
     * Adds an entry to the substitution database
     * 
     * @param inputs
     *            A SubstitutionData object containing information to be added
     *            to the configuration file.
     * 
     @return An ArrayList of Strings. The first entry of the ArrayList is a "1"
     *         or a "0", a "1" signifies a successful execution. If there are
     *         any errors the ArrayList will contain two entries, a "0" and the
     *         exception caught.
     * 
     */
    public List<String> addEntry(SynonymData inputs);

    /**
     * Returns all data in the configuration file
     * 
     * @return An List<String[]> of the data, each String[] contains all the
     *         entries for one entry in the file.
     * 
     */
    public List<String[]> getCurrent(String primaryKey);

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
    public List<String> deleteEntry(String primaryKey);

}
