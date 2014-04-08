package com.googlecode.openreconcile.common;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
// See ConfigDB.java for javadoc information about these methods
public interface ConfigDBAsync {
	public void getTables (DatabaseData inputs, AsyncCallback<List<String>> callback);
	public void getColumns (DatabaseData inputs,  AsyncCallback<List<String>> callback);
	public void addEntry (DatabaseData inputs,  AsyncCallback<List<String>> callback);
	public void getCurrent ( AsyncCallback<List<String[]>> callback);
	public void	deleteThisEntry(String primaryKey,  AsyncCallback<List<String>> callback);
	public void	getPreview(String primaryKey,  AsyncCallback<List<String>> callback);
}
