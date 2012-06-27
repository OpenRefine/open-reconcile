package com.googlecode.openreconcile.client;

import java.util.ArrayList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.openreconcile.client.datatypes.SynonymData;

public class SynonymTable extends Composite{
	private final static ConfigDBAsync svcDatabase = GWT.create(ConfigDB.class);
	private final static SynonymManagerAsync svcSynonym = GWT.create(SynonymManager.class);


	public static SynonymData mySynonymRule;
	
	private FlexTable ftSubPrint = new FlexTable();

	private final VerticalPanel vpSynonyms = new VerticalPanel();
	
	/**
	 * The panel to display synonym data
	 */
	SynonymTable(){
		initWidget(vpSynonyms);
		vpSynonyms.setVisible(true);
		final ListBox lbTypes = new ListBox();
		final HorizontalPanel hpTypeSelect = new HorizontalPanel();
		hpTypeSelect.add(new Label("Please select a type: "));
		hpTypeSelect.add(lbTypes);
		hpTypeSelect.setSpacing(20);
		vpSynonyms.add(hpTypeSelect);
		final ArrayList<String> typeList= new ArrayList<String>();
		ServiceDefTarget endpointDB = (ServiceDefTarget) svcDatabase;
		String DBURL = GWT.getHostPageBaseURL()+"/reconcile/ConfigDB";
        endpointDB.setServiceEntryPoint(DBURL);
        @SuppressWarnings("rawtypes")
		AsyncCallback listCurentCallback = new AsyncCallback(){
	        public void onSuccess (Object result)  
	        {
	        	// clear out any existing current lists, so the screen doesn't
	        	// get to be too cluttered
	        	@SuppressWarnings({ "unchecked" })
				final ArrayList<String[]> rMatrix = (ArrayList<String[]>) result; 
				for( int i =0; i < rMatrix.size(); i++){
					typeList.add(rMatrix.get(i)[9]);				
				}
				if (typeList.size() == 0){
					vpSynonyms.add(new Label("Please add a type first"));
				}
				
				for (int i = 0; i < typeList.size(); i++){
					lbTypes.addItem(typeList.get(i));
				}
		    	lbTypes.setVisibleItemCount(1);
		    	lbTypes.setItemSelected(0, true);
		    	// pull up the table for the first type
				drawSubTable(lbTypes.getValue(0));
				lbTypes.addChangeHandler(new ChangeHandler(){
					public void onChange(ChangeEvent event){
						int selectedIndex = lbTypes.getSelectedIndex();
						String selectedOwner = lbTypes.getValue(selectedIndex);		
						vpSynonyms.clear();
						vpSynonyms.add(hpTypeSelect);
						vpSynonyms.add(ftSubPrint);
						for (int i = ftSubPrint.getRowCount()-1; i >0.; i--){
							ftSubPrint.removeRow(i);
						}
						ftSubPrint.clear();
						drawSubTable(selectedOwner);
					}
				});
	        }
	        // In case of error, alert the user.
			public void onFailure(Throwable caught) {
				Window.alert("There was an error thrown by the server: "+caught.toString());
			}
		};
		svcDatabase.getCurrent(listCurentCallback);
		vpSynonyms.add(ftSubPrint);
	}
	/**
	 * This draws the table and populates it with synonym data
	 * 
	 * @param vocabID
	 */
	private void drawSubTable(final String vocabID) {
		ServiceDefTarget endpointSyn = (ServiceDefTarget) svcSynonym;
		String SynURL = GWT.getHostPageBaseURL()+"/reconcile/SynonymManager";
        endpointSyn.setServiceEntryPoint(SynURL);


		@SuppressWarnings("rawtypes")
		AsyncCallback addCallback = new AsyncCallback(){
	        public void onSuccess (Object result)  
	        {
	        	@SuppressWarnings("unchecked")
				final
				ArrayList<String[]> rMatrix = (ArrayList<String[]>) result;
				
				String[] headers = {"Remove", "From", "To", "VocabID/Type"};
				for (int i = 0; i< headers.length; i++){
					ftSubPrint.setText(0, i, headers[i]);
					ftSubPrint.getCellFormatter().addStyleName(0, i, "displaytable");
				}				
				
				ftSubPrint.setStyleName("displaytable");
				ftSubPrint.getRowFormatter().addStyleName(0, "displaytable");
				// Iterate through the matrix printing all values except
				// for the primary key value (which is of no use to the 
				// user.)				
				for( int i =0; i < rMatrix.size(); i++){
					final int rowNumber = i+1;
					final int matrixRow = i;
					Button bDelete = new Button("X");
					if ((rowNumber % 2) != 0) {
						for (int j = 0; j< headers.length; j++){
							ftSubPrint.getRowFormatter().addStyleName(rowNumber, "displaytable-odd");
							ftSubPrint.getCellFormatter().addStyleName(rowNumber, j, "displaytable-odd");
						}
				      }
				      else {
							for (int j = 0; j< headers.length; j++){
								ftSubPrint.getRowFormatter().addStyleName(rowNumber, "displaytable");
								ftSubPrint.getCellFormatter().addStyleName(rowNumber, j, "displaytable");
							}				      }
					ftSubPrint.setWidget(rowNumber,0,bDelete);
					ftSubPrint.setText(rowNumber,1,rMatrix.get(i)[1]);
					ftSubPrint.setText(rowNumber,2,rMatrix.get(i)[2]);
					ftSubPrint.setText(rowNumber,3,rMatrix.get(i)[3]);
					// Add functionality for the delete button
					bDelete.setTitle(Tutorial.DELETE_SYN);
					bDelete.addClickHandler(new ClickHandler(){
						@Override
						public void onClick(ClickEvent event) {
							deleteRowDialog(rMatrix.get(matrixRow)[0], rowNumber);
						}
					});	
				}
				vpSynonyms.add(new Label("Add a new synonym rule"));
	    		HorizontalPanel hpAdd = new HorizontalPanel();
	    		Label lblFrom = new Label("From:");
	    		Label lblTo = new Label("To:");
	    		final TextBox tbFrom = new TextBox();
	    		final TextBox tbTo = new TextBox();
				Button bAdd = new Button("+");
				hpAdd.add(lblFrom);
				hpAdd.add(tbFrom);
				hpAdd.add(lblTo);
				hpAdd.add(tbTo);
				hpAdd.add(bAdd);
				bAdd.setTitle(Tutorial.ADD_SYN);
				vpSynonyms.add(hpAdd);
	    		bAdd.addClickHandler(new ClickHandler(){
	    			public void onClick(ClickEvent event){
	    				if (tbFrom.getValue().length()>1 && tbTo.getValue().length()>1){
	    					addThis(vocabID, tbFrom.getValue(), tbTo.getValue());
	    				}else{
	    					Window.alert("Please fill out both fields");
	    				}
	    			}
	    		});
	        }
			public void onFailure(Throwable caught) {
				Window.alert("There was an error when trying to get synonym data: "+caught.toString());
			}
		};
		// RPC call to get the current data
		svcSynonym.getCurrent(vocabID, addCallback);
	}
	/**
	 * This generates the dialog to delete the value, if the user click yes, it will 
	 * execute the function that actually deletes the value
	 * 
	 * @param primaryKey
	 * @param rowNumber
	 */
	private void deleteRowDialog(final String primaryKey,
			final int rowNumber) {
		final DialogBox areYouSure = new DialogBox();
		areYouSure.setGlassEnabled(true);
		areYouSure.setModal(true);
		areYouSure.center();
		VerticalPanel vpConfirm = new VerticalPanel();
		HorizontalPanel hpOptions = new HorizontalPanel();
		Label askUser = new Label("Are you sure you want to delete this entry?");
		Button bYes = new Button ("Yes");
		Button bNo = new Button ("No");
		areYouSure.add(vpConfirm);
		vpConfirm.add(askUser);
		vpConfirm.add(hpOptions);
		hpOptions.add(bYes);
		hpOptions.add(bNo);
		bYes.addClickHandler(new ClickHandler(){
			// If they click yes, remove the row from
			// the display and delete the entry
			public void onClick (ClickEvent event){
				ftSubPrint.removeRow(rowNumber);
				deleteRow(primaryKey);
				areYouSure.clear();
				areYouSure.hide();
			}
		});
		bNo.addClickHandler(new ClickHandler(){
			// If they click no, just close the dialog
			// box and leave everything the same
			public void onClick (ClickEvent event){
				areYouSure.clear();
				areYouSure.hide();
			}
		});
	}
	/**
	 * This sends a primary key value to be deleted from the database
	 * 
	 * @param primaryKey
	 */
	private void deleteRow(String primaryKey){
		@SuppressWarnings("rawtypes")
		// This function requires a RPC
		AsyncCallback addCallback = new AsyncCallback(){
	        public void onSuccess (Object result)  
	        {
	        	@SuppressWarnings("unchecked")
				ArrayList<String> resultsArray = (ArrayList<String>) result;
	        	if(resultsArray==null || resultsArray.size()==0){
	        		Window.alert("There was an unknown problem");
	        	}else if (resultsArray.get(0).equals("0") && resultsArray.size()==2){
	        		Window.alert("There was an error on delete row: "+resultsArray.get(1));
	        	}else if (resultsArray.get(0).equals("1")){
	        		Window.alert("Row deleted successfully");
	        	}
	        }
			public void onFailure(Throwable caught) {
				Window.alert("There was an error when trying to delete a row: "+caught.toString());
			}
		};
		svcSynonym.deleteEntry(primaryKey, addCallback);
	}
	
	/**
	 * This sends an entry to be added to the RPC
	 * 
	 * @param vocabID
	 * @param fromText
	 * @param toText
	 */
	private void addThis(String vocabID, String fromText, String toText){
		SynonymData myData = new SynonymData(fromText, toText, vocabID);
		@SuppressWarnings("rawtypes")
		// This function requires a RPC
		AsyncCallback addCallback = new AsyncCallback(){
	        public void onSuccess (Object result)  
	        {
	        	@SuppressWarnings("unchecked")
				ArrayList<String> resultsArray = (ArrayList<String>) result;
	        	if(resultsArray==null || resultsArray.size()==0){
	        		Window.alert("There was an unknown problem");
	        	}else if (resultsArray.get(0).equals("0") && resultsArray.size()==2){
	        		Window.alert("There was an error adding the row: "+resultsArray.get(1));
	        	}else if (resultsArray.get(0).equals("1")){
	        		Window.alert("Row added successfully");
	        	}
	        }
			public void onFailure(Throwable caught) {
				Window.alert("There was an error when trying to add the row: "+caught.toString());
			}
		};
		svcSynonym.addEntry(myData, addCallback);
		
	}
}
