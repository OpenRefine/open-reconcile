package com.googlecode.openreconcile.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.openreconcile.client.datatypes.DatabaseData;
// See ConfigDB.java for javadoc information about these methods
public interface ConfigDBAsync {
	public void getTables (DatabaseData inputs, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void getColumns (DatabaseData inputs, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void addEntry (DatabaseData inputs, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void getCurrent (@SuppressWarnings("rawtypes") AsyncCallback callback);
	public void	deleteThisEntry(String primaryKey, @SuppressWarnings("rawtypes") AsyncCallback callback);
	public void	getPreview(String primaryKey, @SuppressWarnings("rawtypes") AsyncCallback callback);
}
