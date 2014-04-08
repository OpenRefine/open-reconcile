package com.googlecode.openreconcile.common;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SynonymManagerAsync {
	// See SynonymManager for java doc info
	public void addEntry (SynonymData inputs,  AsyncCallback<List<String>> callback);
	public void getCurrent (String primaryKey,  AsyncCallback<List<String[]>> callback);
	public void	deleteEntry(String primaryKey,  AsyncCallback<List<String>> callback);
}
