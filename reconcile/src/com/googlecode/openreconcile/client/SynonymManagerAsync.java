package com.googlecode.openreconcile.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.openreconcile.client.datatypes.SynonymData;

public interface SynonymManagerAsync {
	// See SynonymManager for java doc info
	public void addEntry (SynonymData inputs, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void getCurrent (String primaryKey, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void	deleteEntry(String primaryKey, @SuppressWarnings("rawtypes") AsyncCallback callback);
}
