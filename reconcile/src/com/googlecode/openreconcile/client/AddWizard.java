package com.googlecode.openreconcile.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.p.gwtchismes.client.GWTCWait;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.openreconcile.common.ConfigDB;
import com.googlecode.openreconcile.common.ConfigDBAsync;
import com.googlecode.openreconcile.common.DatabaseData;

public class AddWizard {
	
	private final static DialogBox db = new DialogBox();
	private final static GWTCWait wait = new GWTCWait();
	public DatabaseData myData;
	private final static ConfigDBAsync configDBsvc = GWT.create(ConfigDB.class);
	
	/**
	 * Creates dialog box to ask for more data. It calls finishWizard for 
	 * the last data collection and to call the service to add the data to
	 * the configuration file. The process is split for clarity and for readability
	 * 
	@param myData A DatabaseData object, used to connect to find further options. 
	 *  
	 *  
	 */
	public static void doWizard(final DatabaseData myData){
		final VerticalPanel dbVP = new VerticalPanel();
		ServiceDefTarget endpoint = (ServiceDefTarget) configDBsvc;
		String URL = GWT.getHostPageBaseURL()+"/reconcile/ConfigDB";
        endpoint.setServiceEntryPoint(URL);
    	// Add a waiting message with animation to amuse and entertain the user
		wait.setMessage("Please wait");
		wait.show(300);
		wait.setGlassEnabled(true);
		wait.setModal(false);
		wait.center();
		@SuppressWarnings("rawtypes")
		AsyncCallback addCallback = new AsyncCallback(){
			// if the RPC is successful... 
	        @SuppressWarnings("static-access")
			public void onSuccess (Object result)  
	        {  
	        	wait.hide();
	        	@SuppressWarnings("unchecked")
				final List<String> resultsArray = (List<String>) result;
	        	if (resultsArray.get(0).equals("1")){
		        	db.setModal(true);
		        	db.setGlassEnabled(true);	        	
		        	Label title = new Label("Please select the table(s) you want the names from");
		        	dbVP.add(title);
		        	final ListBox lbOwners = new ListBox();
		        	lbOwners.addItem("All");
		        	lbOwners.setVisibleItemCount(1);
		        	lbOwners.setItemSelected(0, true);
		        	HorizontalPanel selectOwner = new HorizontalPanel();
		        	Label filter = new Label("Filter results: ");
		        	selectOwner.add(filter);
		        	selectOwner.add(lbOwners);
		        	// In the case of Oracle, add a list
		        	// box to allow the user to filter the 
		        	// results by table owner, in case too
		        	// many table names get returned.
		        	if (myData.source.equals(myData.getOptions()[0])){
		        		dbVP.add(selectOwner);
		        	}
		        	final VerticalPanel vpTables = new VerticalPanel();
		        	dbVP.add(vpTables);
	        		HorizontalPanel hp = new HorizontalPanel();
	        		final List<RadioButton> rbTabList = new ArrayList<RadioButton>();
	        		Set<String> owners = new HashSet<String>(); 
	        		//Create RadioButtons for each of the table names returned
	        		// and store them in a List. Also, keep track of the 
	        		//unique owner names for the list box.
		        	for (int i = 1; i < resultsArray.size(); i++){
		        		RadioButton tempRB = new RadioButton("tables",resultsArray.get(i));
		        		rbTabList.add(tempRB);
		        		vpTables.add(rbTabList.get(rbTabList.size()-1));
		        		if(myData.source.equals("Oracle")){
		        			owners.add(resultsArray.get(i).substring(0, resultsArray.get(i).indexOf(".")));
		        		}
		        	}
		        	// Add all of the owner names to the list box.
		        	final String[] ownersList = owners.toArray(new String[owners.size()]);
		        	for(int i =0; i<ownersList.length; i++){
		        		lbOwners.addItem(ownersList[i]);
		        	}
		        	// add the big panel of everything to the dialog box.
		        	db.add(dbVP);
		        	Button dbNext = new Button("Next");
		        	hp.add(dbNext);
		        	// Add a listener to the list box, to filter out the
		        	// owners as needed.
					lbOwners.addChangeHandler(new ChangeHandler(){
						public void onChange(ChangeEvent event){
							rbTabList.clear();
							vpTables.clear();
							int selectedIndex = lbOwners.getSelectedIndex();
							String selectedOwner = lbOwners.getValue(selectedIndex);
							for (int i = 1; i < resultsArray.size(); i++){
								String owner = resultsArray.get(i).substring(0, resultsArray.get(i).indexOf("."));
				        		if(owner.equals(selectedOwner)){
									final RadioButton tempRB = new RadioButton("tables",resultsArray.get(i));
					        		rbTabList.add(tempRB);
					        		vpTables.add(rbTabList.get(rbTabList.size()-1));
					        	}else if (selectedOwner.equals("All")){
					        		RadioButton tempRB = new RadioButton("tables",resultsArray.get(i));
					        		rbTabList.add(tempRB);
					        		vpTables.add(rbTabList.get(rbTabList.size()-1));
					        	}
							}
						}
					});
					// If the user hits the next button save the table name
					// selected into the DatabaseData and go to the last step(s).
					// or alert them if they forgot to select a table name
		        	dbNext.addClickHandler(new ClickHandler(){
		        		public void onClick(ClickEvent event){
		        			String tableSelected = null;
		        			for(int i = 0; i < rbTabList.size(); i++){
		        				if(rbTabList.get(i).getValue()){ 
		        					tableSelected =rbTabList.get(i).getText();
		        					myData.tablename =tableSelected;
		        				}
		        			}

		        			if (tableSelected == null){
	        					Window.alert("Please select one table name!");
	        				}else{
	        					finishWizard(myData);
		        			}
			        	}
		        	});
		 
		        	Button dbClose = new Button("Close");
		        	hp.add(dbClose);
		        	// if the user selects close, hide and 
		        	// clear everything.
		        	dbClose.addClickHandler(new ClickHandler() {
		    			public void onClick(ClickEvent event) {
		    				db.setModal(false);
				        	db.setGlassEnabled(false);
		    				db.hide();
		    				db.clear();
		    				dbVP.clear();
		    			}
		    		});
		        	dbVP.add(hp);
		        	db.center();
		        	db.setVisible(true); 
		        	
	        	}else{
	        		// If the results include an error 
	        		// clear everything and send an alert to the
	        		// user.
		        	db.setModal(false);
		        	db.setGlassEnabled(false);
		        	db.hide();
		        	db.clear();
		        	dbVP.clear();
	        		Window.alert("An error was returned from the server: "+resultsArray.get(1));
	        	}
	        }  
	        // On failure alert the user.
	        public void onFailure (Throwable ex)  
	        {  
	        	Window.alert("CONNECTION TO SERVER FAILED: "+ex.toString()); 
	        } 
	    };
	    // Call to server
		configDBsvc.getTables(myData, addCallback); 
	}
	
	/**
	 * Finishes collecting information for user and sends a request to add the
	 * references to the configuration file. It is split out of the doWizard
	 * method for readability.
	 * 
	@param data A DatabaseData object, used to query for column names and then sent via RPC to be added to the configuration file.
	 *   
	 */
	private static void finishWizard(final DatabaseData data){
		final List<RadioButton> rbColList = new ArrayList<RadioButton>();
		final VerticalPanel dbVP = new VerticalPanel();
		// clear all of the table-name widgets/data from the dialog box
		db.clear();
		
		final VerticalPanel colSelectVP = new VerticalPanel();		
		db.add(dbVP);
		dbVP.add(new Label("Please finish entering information to complete the addition:"));
		dbVP.add(colSelectVP);
		final Label tableName = new Label(data.tablename);
    	colSelectVP.add(tableName);
    	// Add a waiting message with animation to amuse and entertain the user
		wait.setMessage("Please wait");
		wait.show(300);
		wait.setGlassEnabled(true);
		wait.setModal(false);
		wait.center();

		@SuppressWarnings("rawtypes")
		AsyncCallback callback = new AsyncCallback(){
	        public void onSuccess (Object result)  
	        {
	    		// if the callback is successful populate the display with the column names
	        	wait.hide();
	        	@SuppressWarnings("unchecked")
	        	List<String> resultsArray = (List<String>) result;
	        	for(int k=1; k<resultsArray.size(); k++){
	        		rbColList.add(new RadioButton(tableName.toString(), resultsArray.get(k)));        				        		
	        		colSelectVP.add(rbColList.get(rbColList.size()-1));
	        	}
		     }
		      public void onFailure (Throwable ex)
		        {
		        	Window.alert("FAILED "+ex.toString()); 
		        }
		};
			
		configDBsvc.getColumns(data, callback);    
    	HorizontalPanel hpID = new HorizontalPanel();
    	
    	Label lblVocabID = new Label("Vocab ID:");
    	final TextBox tbVocabID = new TextBox();

    	hpID.add(lblVocabID);
    	hpID.add(tbVocabID);
    	HorizontalPanel hpSensitivities = new HorizontalPanel();
    	// Create boxes for capitalization/punctuation sensitivity 
    	Label lblCap = new Label("Capitalization Sensitivity");
    	final CheckBox cbCap = new CheckBox();
    	hpSensitivities.add(lblCap);
    	hpSensitivities.add(cbCap);

    	Label lblPunct = new Label("Punctuation Sensitivity");
    	final CheckBox cbPunct = new CheckBox();
    	hpSensitivities.add(lblPunct);
    	hpSensitivities.add(cbPunct);
    	dbVP.add(hpID);
    	dbVP.add(hpSensitivities);

		HorizontalPanel hpButtons = new HorizontalPanel();
    	Button bAddThis = new Button("Select");
    	bAddThis.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(rbColList.size()>0 && tbVocabID.getValue().length()>=1){
					for(int i = 0; i < rbColList.size(); i++){
						if(rbColList.get(i).getValue()){
							data.columnname = rbColList.get(i).getText();
						}
					}
					data.vocabid = tbVocabID.getText();
					data.casesensitive =cbCap.getValue();
					data.punctuationsensitive = cbPunct.getValue();
					// Get all of the values and send the RPC to add
					// the data to the configuration file.
					db.hide();
					db.clear();
					dbVP.clear();
					addData(data);
					
				}else{
					// Catch if no column is selected and/or the vocabID is empty
					Window.alert("Please fill out everything completely");	
				}
			}
    	});
	    hpButtons.add(bAddThis);
    	Button dbClose = new Button("Close");
    	hpButtons.add(dbClose);
    	// If the user clicks the close button, pack up everything and send the
    	// dialog box away.
    	dbClose.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				db.setModal(false);
	        	db.setGlassEnabled(false);
				db.hide();
				db.clear();
				dbVP.clear();
			}
		});
    	dbVP.add(hpButtons);

	}
	/**
	 * Adds a database reference to the configuration file. It is called
	 * When the "Select Database" is clicked and all of the input is properly
	 * gathered. It sends a request via RPC to the server to It returns nothing
	 *                           
	@param inputs  The DatabaseData object of the input values to be stored in the configuration file
	 *  
	 *  
	 */
	protected static void addData(final DatabaseData inputs) {
		@SuppressWarnings("rawtypes")
		AsyncCallback addCallback = new AsyncCallback(){
	        public void onSuccess (Object result)  
	        {
	        	@SuppressWarnings("unchecked")
				List<String> resultsArray = (List<String>) result;
	        	if(resultsArray!=null && resultsArray.size()>0){
		        	if (resultsArray.get(0).equals("1")){
		        		Window.alert("Entry added successfully.");
		        	}else{
		        		if(resultsArray.size()==2){
		        			Window.alert("There was an error from addData: "+ resultsArray.get(1).toString());
		        		}else{
		        			Window.alert("There was an unknown error from addData");
		        		}
		        	}
	        	}else{
	        		// If there's a problem with the input, alert the user.
	        		Window.alert("There were too few arguments returned");
	        	}
	        	
	        }
	        //If the RPC call failed, alert the user.
			public void onFailure(Throwable caught) {
				Window.alert("There was an error from request to add data on server: "+caught.toString());
			}
		};
		configDBsvc.addEntry(inputs, addCallback);
	}
}
	
