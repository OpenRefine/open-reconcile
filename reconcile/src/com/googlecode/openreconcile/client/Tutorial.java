package com.googlecode.openreconcile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Tutorial extends DialogBox {
	// this is general help information
	public static final String ADD_DATA = "To add a table select the source type and then enter in the desired information.";
	public static final String DATA_TABLE = "View current information about configured types and delete unwanted types. Deleting a type does not delete any information from the source database.";
	public static final String SYNONYMS = "A synonym is an automatic substitution that should be done during reconciliation, it will not be reflected in the source database.";
	
	public static final String ADD_TYPE = "This adds a new type to Open Reconcile. It will not modify the source database in any way";

	public static final String SERVER_HINT = "If using a local database, if 'localhost' does not work, try typing in '127.0.0.1'";
	public static final String PORT_HINT = "The default port for Oracle is 1521, the default port for PostgreSQL is 5432, and the default port for MySQL is 3306.";

	public static final String ADD_WIZARD = "This method guides the user through the selection of the table and column names. This way is recommended.";
	public static final String ADD_MANUAL = "This method allows the user to directly type in all the necessary information. Use with caution.";

	public static final String DELETE_TYPE = "This will NOT delete any information from the source database, it will only remove the available of reconciling against this type using Open Reconcile.";
	public static final String PREVIEW = "See the first 10 terms of a type";
	public static final String DATABASE_DATA = "See the connection configuration information";
	
	public static final String ADD_SYN = "Add a synonym for to the reconciliation data, this will not change any information in the source database";
	public static final String DELETE_SYN = "This will remove the synonym rule from the reconcilation process, it will not impact anything in the source database";

	
	/**
	 * A dialog box to show how to how to add this service to Google Refine as a reconciliation service
	 */
	public Tutorial(){
		VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setWidth("100%");
		
		Label lblVocabName = new Label("Welcome to Open Reconcile");
		verticalPanel.add(lblVocabName);

		HTML html = new HTML(
				"To use Open Reconcile with <a href=\"http://code.google.com/p/google-refine/wiki/Downloads\">Google Refine</a> please add <b>"+GWT.getHostPageBaseURL()+"reconcile</b> as a \"Standard Service\" during the \"Start reconciling...\" dialog in Google Refine."+
				"<br><br><br><img src=\"images/First.jpg\"><br>1) First select the column you want to reconcile.<br>2.) Select <b>\"Reconcile\"</b><br>3.) From the Reconcile submenu select <b>\"Start reconciling...\"</b><br><br>"+
				"A dialog box will pop up<br><br><img src=\"images/Second.jpg\"><br><br>4.) Select <b>\"Add Standard Service...\"</b> <br> A smaller box will pop up and prompt you for the url (<b>"+GWT.getHostPageBaseURL()+"reconcile</b>) you want to use this service<br><br> " +
						"<img src=\"images/Third.jpg\"><br><br>5.) Please use <b>"+GWT.getHostPageBaseURL()+"reconcile</b> for Open Reconcile.<br>6.) Click <b>\"Add Service\"</b> to add the service<br> <br><br>To use without Google Refine, please refer to <a href=\"code.google.com/p/google-refine/wiki/ReconciliationServiceApi\">the Reconciliation Service API</a>.", true);
		verticalPanel.add(html);
		Button bClose = new Button("Close Window");
		verticalPanel.add(bClose);
		bClose.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				clear();
				hide();
				setModal(false);
				setGlassEnabled(false);
			}
		});
		center();
		setModal(true);
		setGlassEnabled(true);
	}

}
