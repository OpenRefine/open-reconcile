package com.googlecode.openreconcile.common;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SynonymManagerAsync {
	// See SynonymManager for java doc info
	public void addEntry (SynonymData inputs, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void getCurrent (String primaryKey, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void	deleteEntry(String primaryKey, @SuppressWarnings("rawtypes") AsyncCallback callback);
}
