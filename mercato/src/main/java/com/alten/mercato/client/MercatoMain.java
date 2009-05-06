/**
 * 
 */
package com.alten.mercato.client;

import com.alten.mercato.client.service.UserService;
import com.alten.mercato.client.ui.DpmtDirectorUI;
import com.alten.mercato.client.ui.HRUI;
import com.alten.mercato.client.ui.util.ConstantsMercato;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;


/**
 * Entry point user interface
 * @author Huage Chen
 *
 */
public class MercatoMain implements EntryPoint {
	

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {
		getCurrentUserByRPC();	
	}
	
	/**
	 * retrieve the current logged person
	 */
	private void getCurrentUserByRPC() {
		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onFailure(Throwable t) {
				SC.say("fail to load");
			}

			public void onSuccess(Personne result) {
				ConstantsMercato.initCurrentUser(result);
				if (result != null) {
					System.out.println(result.getPerNom());
				}
				//TODO generate different layout according to the role information, especially for human resource logins
				initLayout();
			}
		};
		UserService.Util.getInstance().getCurrentUser(callback);
	}

	/**
	 * initialize the layout
	 */
	private void initLayout() {
		
		VLayout main = new VLayout() ;
		main.setPadding(5);
		main.setAlign(Alignment.LEFT);
		
		final ToolStrip toolbar = new ToolStrip();		
		toolbar.setHeight(40);
		toolbar.setWidth(1200);
		toolbar.setPadding(5);
		toolbar.setMargin(5);
	
		
	

		HTMLPane paneLinkLogout = new HTMLPane();
		paneLinkLogout.setContents("<a href=\"/mercato/destroySession\">Logout</a>");
		paneLinkLogout.setHeight(20);

		Canvas canvasLogout = new Canvas();
		canvasLogout.addChild(paneLinkLogout);
		
		Personne user = ConstantsMercato.getCurrentUser();
		
		if (user != null ) {
			System.out.println(user.getPerNom());
		}
		
		// welcome message
		toolbar.setContents("Bienvenue " + user.getPerPrenom() + " " + user.getPerNom() + ". Cette application demo vous permet d'effectuer les transferts entre votre département et les autre départements sous le contrôle des workflows prédéfinis.");

		main.addMember(toolbar);
		// update the component according to the user's role
		if (user.getTypePersonne().getTpersCode().equals("DD")) {
			DpmtDirectorUI ddUI = new DpmtDirectorUI();
			main.addMember(ddUI);
		} else {
			HRUI hrUI = new HRUI();
			main.addMember(hrUI);
		}
		Canvas canvas = new Canvas();
		canvas.addChild(main);
		
		RootPanel.get().add(canvas); 
		RootPanel.get().add(canvasLogout);

		// clean the load message
		RootPanel.get("loadingWrapper").getElement().setInnerHTML("");
	}
	
	
}
