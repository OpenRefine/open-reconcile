package com.googlecode.openreconcile.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 */

public class Reconcile implements EntryPoint {	
	
	private final VerticalPanel vpBig = new VerticalPanel();
	

	public AddTable addPanel = new AddTable();
	public DataTable dataPanel = new DataTable();
	public Tutorial info = new Tutorial();
	public SynonymTable synonymPanel = new SynonymTable();
	
	/**
	 * Loads the main page. This is the go to class which leads to everything
	 * else. 
	 *  
	 */
	public void onModuleLoad() {
		
		// The rootPanel is the base panel, everything is added to it.
		RootPanel rootPanel = RootPanel.get();
		rootPanel.add(vpBig);
		vpBig.add(new HTML("<font size=\"6\" face=\"arial\">Open Reconcile Configuration Manager</font>"));
		final Button bSelectDB = new Button("Add New Type");
		bSelectDB.setTitle(Tutorial.ADD_DATA);
		final Button bListCurrent = new Button("Manage Current Types");
		bListCurrent.setTitle(Tutorial.DATA_TABLE);
		final Button bSynonyms = new Button ("Manage Synonyms");
		bSynonyms.setTitle(Tutorial.SYNONYMS);
		vpBig.setSpacing(25);
		vpBig.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		final HorizontalPanel hpButtons = new HorizontalPanel();
		hpButtons.setWidth("350px");
		hpButtons.setSpacing(20);
		hpButtons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		hpButtons.setStyleName("verticalPanel");
		hpButtons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		hpButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

		hpButtons.add(bSelectDB);
		bSelectDB.setSize("142px", "34px");
		hpButtons.add(bListCurrent);
		bListCurrent.setWidth("156px");
		hpButtons.add(bSynonyms);
		bSynonyms.setWidth("136px");
		
		// This  creates the widget to add a new type
		vpBig.add(hpButtons);
		
		bSelectDB.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				vpBig.clear();
				vpBig.add(new HTML("<font size=\"6\" face=\"arial\">Open Reconcile Configuration Manager</font>"));
				vpBig.add(hpButtons);
				vpBig.add(new AddTable());
			}
		});
		
		// This creates the widget to edit the type data
		bListCurrent.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vpBig.clear();
				vpBig.add(new HTML("<font size=\"6\" face=\"arial\">Open Reconcile Configuration Manager</font>"));
				vpBig.add(hpButtons);
				vpBig.add(new DataTable());
			}
		});
		bSynonyms.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				vpBig.clear();
				vpBig.add(new HTML("<font size=\"6\" face=\"arial\">Open Reconcile Configuration Manager</font>"));
				vpBig.add(hpButtons);
				vpBig.add(new SynonymTable());
			}
		});
		// Have as the first thing on the page a basic "You may be in the wrong place"
		// with help.
		Image image = new Image("images/smpublicDomain-Questionmark.png");
		image.setSize("70px", "66px");
		PushButton pbHelp = new PushButton(image);
		pbHelp.setSize("75px", "75px");
		
		Label tutorial = new Label("Looking to use Google Refine with this service?");
		
		vpBig.add(tutorial);
		vpBig.add(pbHelp);
		
		// show the tutorial if the help button is clicked
		pbHelp.addClickHandler(new ClickHandler(){		
			public void onClick(ClickEvent event){
				new Tutorial().show();
			}
		});

	}

}