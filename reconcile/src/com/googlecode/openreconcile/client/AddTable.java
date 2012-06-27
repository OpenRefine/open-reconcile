package com.googlecode.openreconcile.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.openreconcile.client.datatypes.DatabaseData;

public class AddTable extends Composite {
	// These Labels and Boxes are declared as class variables because
	// multiple functions in this class use them. 
	// The naming should be self explanatory. The same textbox is used
	// for service name and database name as they are used by different
	// db systems for pretty much the same function in java.sql syntax
	
	public DatabaseData mDatabaseData;
	
	private final Label[] lbls = {
			new Label("Server Name:"), new Label("Port No.:"), new Label("Service Name:"),
			 new Label("User Name:"), new Label("Password:"), new Label("Table Name:"), 
			 new Label("Column Name:"), new Label("Vocab ID:"), new Label("Case Sensitive"), 
			 new Label("Punctuation Sensitive"), new Label("Database Name:"), new Label("Select Database Type")
	};

	private final HTML[] htmlExs={
			new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>localhost</font></i>"), 
			new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>5432</font></i>"), 
			new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>my.service.com</font></i>"),
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>readOnlyAccount</font></i>"), 
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>readOnlyPassword</font></i>"), 
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>Owner.TableName</font></i>"), 
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>Owner.TableName.ColumnName</font></i>"), 
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>myVocab</font></i>"), 
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>exampleDBName</font></i>"),
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>tableName</font></i>"), 
			 new HTML("<font size=\"1\" face=\"arial\" color=\"black\"><i>Columnname</font></i>")
	};

	private final TextBox[] tbs = new TextBox[8];	
	private final ListBox lbDBType = new ListBox();
	private final CheckBox cbCap = new CheckBox();
	private final CheckBox cbPunct = new CheckBox();
	private final FlexTable flexTable = new FlexTable();
	
	// RadioButtons for wizard or manual, the wizard allows the user to walkthru
	// the creation of the link to a db column (polling the database for available 
	// table names/column names/etc. The advanced just expands the form so that the 
	// user can manually enter in all of the information 
	private final RadioButton rbWizard = new RadioButton("setting", "Wizard");

	private final RadioButton rbManual = new RadioButton("setting","Manual Entry");

	private final Button bAddThis = new Button ("Add");

	private final Button bClearEntry = new Button("Clear Text");

	public AddWizard adder = new AddWizard();
	/**
	 * Creates a FlexTable to contain the form for adding an entry into the 
	 * configuration file.
	 */
	AddTable(){
		// the widget to be returned is a flextable
		initWidget(flexTable);
		
		// The default entry is Oracle using the Wizard.
		rbWizard.setValue(true);
		// initialize the textboxes
		for (int i=0; i<tbs.length; i++){
			tbs[i] = new TextBox();
		}
		// add hints for problem areas
		tbs[0].setTitle(Tutorial.SERVER_HINT);
		tbs[1].setTitle(Tutorial.PORT_HINT);
		// Generate the drop-down box of options from
		// those configured in DatabaseData
		String[] optionList = DatabaseData.getOptions();
		for (int i=0; i< optionList.length; i++){
			lbDBType.addItem(optionList[i]);
		}
		
		lbDBType.setVisibleItemCount(1);
		flexTable.setWidth("475px");
		bAddThis.setTitle(Tutorial.ADD_TYPE);		
		bAddThis.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DatabaseData myInput;
				if(rbWizard.getValue()){
					myInput = new DatabaseData(lbDBType.getItemText(lbDBType.getSelectedIndex()), 
							tbs[0].getText(), tbs[1].getText(), tbs[2].getText(),
							tbs[3].getText(), tbs[4].getText());
					AddWizard.doWizard(myInput);
					
				}else if (rbManual.getValue()){
					myInput = new DatabaseData(lbDBType.getItemText(lbDBType.getSelectedIndex()), 
							tbs[0].getText(), tbs[1].getText(), tbs[2].getText(),
							tbs[3].getText(), tbs[4].getText(), tbs[5].getText(),
							tbs[6].getText(), tbs[7].getText(), cbCap.getValue(), 
							cbPunct.getValue());
					AddWizard.addData(myInput);
				}else{
					Window.alert("I shouldn't be here... oops!");
				}
				
			}
		});
        bClearEntry.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent event){
        		clearTextInputs();
        	}
        });
        setDisplayOracle();
        // Adding a listener for source type, so that the 
        // proper form will be displayed
        lbDBType.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				if (lbDBType.getItemText(lbDBType.getSelectedIndex()).equals(DatabaseData.getOptions()[0]))	{
					setDisplayOracle();
				}else{
					setDisplayPostgresSQL();
				}
			}
		});
        rbManual.setTitle(Tutorial.ADD_MANUAL);
		// This changes the form based on what the user has
		// clicked on.
		rbManual.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				rbManual.setValue(true);
				rbWizard.setValue(false);
				if (lbDBType.getItemText(lbDBType.getSelectedIndex()).equals(DatabaseData.getOptions()[0])){
					setDisplayOracle();
				}else{
					setDisplayPostgresSQL();
				}
			}
		});
		rbWizard.setTitle(Tutorial.ADD_WIZARD);
		// This changes the form based on what the user has
		// clicked on.
		rbWizard.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				if (lbDBType.getItemText(lbDBType.getSelectedIndex()).equals(DatabaseData.getOptions()[0]))	{
					setDisplayOracle();
				}else{
					setDisplayPostgresSQL();
				}
			}
		});
	}
	
	/**
	 * Clear all data on the page. It sets all text boxes to blank.
	 *  
	 */
	private void clearTextInputs(){
		for (int i=0; i<tbs.length; i++){
			tbs[i].setText("");
		}
		cbCap.setValue(false);
		cbPunct.setValue(false);
	}
	
	
	/**
	 * Displays the fields required for Oracle database information. A flextable is the home 
	 * of the forms on the main page.
	 *  
	 */
	private void setDisplayOracle(){		
		clearTextInputs();
		flexTable.clear();
		flexTable.setWidget(0,0,rbWizard); 
		flexTable.setWidget(0,1,lbls[11]);
		flexTable.setWidget(1,0,rbManual);
		flexTable.setWidget(1,1, lbDBType);
		int max;
		if (rbWizard.getValue()){
			max = 5;
			flexTable.setWidget(8, 0, bAddThis);
			flexTable.setWidget(8, 1, bClearEntry);
		}else{
			max = 8;
			flexTable.setWidget(13,0,lbls[8]);
			flexTable.setWidget(13,1,cbCap);
			flexTable.setWidget(13,2,lbls[9]);
			flexTable.setWidget(13,3,cbPunct);
			flexTable.setWidget(14, 0, bAddThis);
			flexTable.setWidget(14, 1, bClearEntry);
		}
		for (int i = 0; i < max; i ++){
			flexTable.setWidget(i+2,0,lbls[i]);
			flexTable.setWidget(i+2,1,tbs[i]);
			flexTable.setWidget(i+2,2,htmlExs[i]);
		}
	}
	
	/**
	 * Displays the fields required for postgres or mysql database information. Because it's identical
	 * to the Oracle Database, I just have it set it to Oracle, but change the
	 * one label that is different.A flextable is the home 
	 * of the forms on the main page.
	 * 
	 */
	private void setDisplayPostgresSQL(){
		flexTable.clear();
		setDisplayOracle();
		// just change the one from Service name to DB name
		flexTable.setWidget(4,0,lbls[10]);
		flexTable.setWidget(4, 2, htmlExs[8]);
		if (rbManual.getValue()){
			flexTable.setWidget(7,2,htmlExs[9]);
			flexTable.setWidget(8,2,htmlExs[10]);
		}
		
	}	

}
