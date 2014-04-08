package com.googlecode.openreconcile.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.openreconcile.common.ConfigDB;
import com.googlecode.openreconcile.common.ConfigDBAsync;

@SuppressWarnings("deprecation")
public class DataTable extends Composite{
	
	private final static ConfigDBAsync svcDatabase = GWT.create(ConfigDB.class);

	private final FlexTable ftPrint = new FlexTable();
    private final VerticalPanel vpLayout = new VerticalPanel();

    /**
	 * 	This class extends Composite and creates a table to show all the information about the types collected 	                          
	 */
	DataTable(){
		vpLayout.setWidth("600px");
		ftPrint.setWidth("600px");
		ServiceDefTarget endpoint = (ServiceDefTarget) svcDatabase;
		String URL = GWT.getHostPageBaseURL()+"/reconcile/ConfigDB";
        endpoint.setServiceEntryPoint(URL);
		// a flex table is used for the results so that it can adjust to
		// the string lengths is an easy going manner.
		// this references a style from Reconcile.css to keep the font
		// small enough and add a border
		initWidget(vpLayout);
        AsyncCallback<List<String[]>> listCurentCallback = new AsyncCallback<List<String[]>>(){
	        public void onSuccess (List<String[]> result)  
	        {
	        	// clear out any existing current lists, so the screen doesn't
	        	// get to be too cluttered
				final List<String[]> rMatrix = (List<String[]>) result; 
				final Label lblTable = new Label("All Current Configured Vocabulary Sources");
				final FlexTable ftPrint = new FlexTable();
				vpLayout.add(lblTable);
				vpLayout.add(ftPrint);
				ftPrint.setStyleName("displaytable");
				ftPrint.getRowFormatter().addStyleName(0, "displaytable");
				
				String[] headers = {"Remove DB Link", "Preview Terms",
						"Vocab ID",	"Case Sensitive", "Punct. Sensitive", "Database Connection Info" };
				for (int i = 0; i< headers.length; i++){
					ftPrint.setText(0, i, headers[i]);
					ftPrint.getCellFormatter().addStyleName(0, i, "displaytable");
				}

				// Iterate through the matrix printing all values except
				// for the primary key value (which is of no use to the 
				// user.
				if (rMatrix.size() < 1){
					vpLayout.add(new Label("Oops, no data was found. Are you sure there are supposed to be entries?"));
				}
				for( int i =0; i < rMatrix.size(); i++){
					final int rowNumber = i+1;
					final int matrixRow = i;
					Button bDelete = new Button("X");
					bDelete.setTitle(Tutorial.DELETE_TYPE);
//					PushButton pbConfig = new PushButton(new Image("images/gear.gif"));
					PushButton pbPreview = new PushButton(new Image("images/preview.gif"));
					PushButton pbServerConfig = new PushButton(new Image("images/servericon.png"));
					pbServerConfig.setTitle(Tutorial.DATABASE_DATA);
//					pbConfig.setPixelSize(25, 25);
					pbPreview.setPixelSize(25, 25);
					pbPreview.setTitle(Tutorial.PREVIEW);
					pbServerConfig.setPixelSize(25, 25);
					if ((rowNumber % 2) != 0) {
						for (int j = 0; j< headers.length; j++){
							ftPrint.getRowFormatter().addStyleName(rowNumber, "displaytable-odd");
							ftPrint.getCellFormatter().addStyleName(rowNumber, j, "displaytable-odd");
						}
				      }
				      else {
							for (int j = 0; j< headers.length; j++){
								ftPrint.getRowFormatter().addStyleName(rowNumber, "displaytable");
								ftPrint.getCellFormatter().addStyleName(rowNumber, j, "displaytable");
							}				      }
					ftPrint.setWidget(rowNumber,0,bDelete);
//					ftPrint.setWidget(rowNumber,1,pbConfig);
					ftPrint.setWidget(rowNumber,1,pbPreview);
					ftPrint.setText(rowNumber,2,rMatrix.get(i)[9]);
					ftPrint.setText(rowNumber,3,rMatrix.get(i)[10]);
					ftPrint.setText(rowNumber,4,rMatrix.get(i)[11]);
					ftPrint.setWidget(rowNumber,5, pbServerConfig);

					// Add functionality for the delete button
					bDelete.addClickHandler(new ClickHandler(){
						@Override
						public void onClick(ClickEvent event) {
//							deleteRowDialog(rMatrix.get(matrixRow)[0], rowNumber);
							final DialogBox areYouSure = new DialogBox();
							VerticalPanel vpConfirm = new VerticalPanel();
							HorizontalPanel hpOptions = new HorizontalPanel();
							Label askUser = new Label("Are you sure you want to delete this entry?");
							Button bYesDelete = new Button ("Yes");
							Button bNo = new Button ("No");
							areYouSure.add(vpConfirm);
							vpConfirm.add(askUser);
							vpConfirm.add(hpOptions);
							hpOptions.add(bYesDelete);
							hpOptions.add(bNo);
							bYesDelete.addClickHandler(new ClickHandler(){
								// If they click yes, remove the row from
								// the display and delete the entry
								public void onClick (ClickEvent event){
									deleteDataRow(rMatrix.get(matrixRow)[0]);
									areYouSure.clear();
									areYouSure.hide();
									areYouSure.setModal(false);
									areYouSure.setGlassEnabled(false);
									ftPrint.removeRow(rowNumber);
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
							areYouSure.setGlassEnabled(true);
							areYouSure.setModal(true);
							areYouSure.center();
						}
					});	
//					// Add functionality to configure synonyms
//					pbConfig.addClickHandler(new ClickHandler(){
//						public void onClick(ClickEvent event){
//							configureSynonyms(rMatrix.get(matrixRow)[9]);
//						}
//					});
					// Get connection information.
					pbServerConfig.addClickHandler(new ClickHandler(){
						public void onClick(ClickEvent event){
							String [] serverHeaders = {"Data Source", "Service/DB Name", "Port Number", 
									"Server Name", "User Name", "Password", "Table Name", "Column Name"};
							final DialogBox serverInfo = new DialogBox();
							serverInfo.setModal(true);
							serverInfo.setGlassEnabled(true);
							VerticalPanel vpData = new VerticalPanel();
							serverInfo.add(vpData);
							serverInfo.center();
							vpData.add(new HTML("<b>Server Connection Information:</b>"));
							FlexTable ftData = new FlexTable();
							vpData.add(ftData);
							for (int j = 1; j < 9; j++){
								ftData.setText(j-1, 0, serverHeaders[j-1]+":");
								ftData.setText(j-1, 1, rMatrix.get(matrixRow)[j]);
							}
							serverInfo.center();
							Button bClose = new Button("Close Window");
							vpData.add(bClose);
							bClose.addClickHandler(new ClickHandler(){
								public void onClick(ClickEvent event){
									serverInfo.clear();
									serverInfo.hide();
									serverInfo.setModal(false);
									serverInfo.setGlassEnabled(false);
								}
							});
						}
					});
					
					pbPreview.addClickHandler(new ClickHandler(){
						public void onClick(ClickEvent event){
							dataPreview dp = new dataPreview(rMatrix.get(matrixRow)[0]);
							dp.show();
							dp.center();
							dp.setModal(true);
							dp.setGlassEnabled(true);
						}
					});
				}
	        }
	        // In case of error, alert the user.
			public void onFailure(Throwable caught) {
				Window.alert("There was an error thrown by the server: "+caught.toString());
			}
		};
		svcDatabase.getCurrent(listCurentCallback);
		return;
	}
	
	/**
	 * Sends a request to the server to remove a row. It will alert the user if there is an error, or if it is successful.
	 * 	                          
	@param primaryKey The primary key of the row to be deleted.
	 *
	 *  
	 */
	private void deleteDataRow(String primaryKey) {
		// This function requires a RPC
		AsyncCallback<List<String>> addCallback = new AsyncCallback<List<String>>(){
	        public void onSuccess (List<String> result)  
	        {
	        	List<String> resultsArray = (List<String>) result;
	        	if(resultsArray==null || resultsArray.size()==0){
	        		Window.alert("There was an unknown problem");
	        	}else if (resultsArray.get(0).equals("0") && resultsArray.size()==2){
	        		Window.alert("There was an error on delete row: "+resultsArray.get(1));
	        	}else if (resultsArray.get(0).equals("1")){
	        		Window.alert("Row deleted successfully");
	        	}else{
	        		Window.alert("This was unexpected");
	        	}
	        }
			public void onFailure(Throwable caught) {
				Window.alert("There was an error when trying to delete a row: "+caught.toString());
			}
		};
		svcDatabase.deleteThisEntry(primaryKey, addCallback);
	}


	/**
	 * Sends a request to the server to retrieve some data from the database link specified. It displays this information in a pop-up box
	 * Only the first 10 items are displayed. 
	 * 
	@param primaryKey The primary key of the row to have data displayed for.
	 */
	private static class dataPreview extends DialogBox{
		public dataPreview(String primaryKey){
			setGlassEnabled(true);
			setModal(true);
			setWidth("250px");
			final VerticalPanel vpShow = new VerticalPanel();
			add(vpShow);
			vpShow.setWidth("250px");
			// This function requires a RPC
			AsyncCallback<List<String>> addCallback = new AsyncCallback<List<String>>(){
		        public void onSuccess (List<String> result)  
		        {
		        	List<String> resultsArray = (List<String>) result;
		        	if(resultsArray==null || resultsArray.size()==0){
		        		Window.alert("There was an unknown problem");
		        	}else if (resultsArray.get(0).equals("0") && resultsArray.size()==2){
		        		Window.alert("There was an error: "+resultsArray.get(1));
		        	}else if (resultsArray.get(0).equals("1")){
		        		int height = Window.getClientHeight();
		        		int width = Window.getClientWidth();
		        		setPopupPosition(width/2 - 125, height/8);
		        		show();
	        		if (resultsArray.get(1) == null){
	        			vpShow.add(new Label("There are no results"));
	        		}
	        		for (int i = 1; i < resultsArray.size(); i++){
	        			vpShow.add(new Label(resultsArray.get(i)));
	        		}
	        		vpShow.add(new Button("Close", new ClickListener() {
						@Override
						public void onClick(Widget sender) {
							clear();
							hide();
							setGlassEnabled(false);
							setModal(false);
						}
	        		  }));
	        	}else{
	        		vpShow.add(new Label("Where am I?"+resultsArray.get(0)));
	        	}
	        }
			public void onFailure(Throwable caught) {
				Window.alert("There was an error when trying to get preview data: "+caught.toString());
			}
		};
		svcDatabase.getPreview(primaryKey, addCallback);
	}
		
	}
}
