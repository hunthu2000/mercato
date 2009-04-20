/**
 * 
 */
package com.alten.mercato.client;

import java.util.List;

import com.alten.mercato.client.data.ds.PersonDataSource;
import com.alten.mercato.client.service.DemoService;
import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;


/**
 * @author Huage Chen
 *
 */
public class MercatoMain implements EntryPoint {
	private TreeGrid tgMyDepartmentConsultants = new TreeGrid();
	private TreeNode rootNodeMyDepartmentConsultants = null;
	private PersonDataSource dsMyDepartmentConsultants = new PersonDataSource("myDepartment");
	

	public void onModuleLoad() {
		
		HLayout hlayout = new HLayout();  
        hlayout.setWidth(600);  
        hlayout.setHeight100();  
        hlayout.setMembersMargin(5);  
		hlayout.setPadding(5);
		
		initTreeGrid();
		loadListConsultantsByRPC();
		
		hlayout.addMember(tgMyDepartmentConsultants);
		
		
		Canvas canvas = new Canvas();
		canvas.addChild(tgMyDepartmentConsultants);
		
		
		HTMLPane paneLinkLogout = new HTMLPane();
		paneLinkLogout.setContents("<a href=\"/mercato/destroySession\">Logout</a>");
		paneLinkLogout.setHeight(20);
	
		Canvas canvasLogout = new Canvas();
		canvasLogout.addChild(paneLinkLogout);
		

		final Label quoteText = new Label();
		
	
		
		AsyncCallback<String> callback = new AsyncCallback<String>() {

			public void onFailure(Throwable t) {
				// display error text if we can't get the quote:
				quoteText.setText("Failed to get a quote");
			}

			public void onSuccess(String result) {
				// display the retrieved quote in the label:
				quoteText.setText(result);
			}
		};
		DemoService.Util.getInstance().getString(callback);
		
		
		
		RootPanel.get().add(quoteText);
		RootPanel.get().add(canvas); 
		RootPanel.get().add(canvasLogout);
		
		// clean the load message
		RootPanel.get("loadingWrapper").getElement().setInnerHTML("");
	}
	
	/**
	 * 
	 */
	private void loadListConsultantsByRPC() {

		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Personne>> callback = new AsyncCallback<List<Personne>>() {

			public void onSuccess(List<Personne> result) {
				if (result==null||result.size()==0) {
					dlg.hide();
					return;
				}
				System.out.println(result.size() + " consultants for this dd found");
				createTreeNodes(result);
				openAllNodesOfListGrid();
				dlg.hide();
			}

			public void onFailure(Throwable ex) {
				dlg.hide();
			}
		};

		PersonService.Util.getInstance().getConsultantsForDD(callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}
	
	/**
	 * 
	 */
	private void initTreeGrid() {
		tgMyDepartmentConsultants.setAutoFetchData(true);
		tgMyDepartmentConsultants.setLoadDataOnDemand(false); 
		tgMyDepartmentConsultants.setHeight(400);
		tgMyDepartmentConsultants.setWidth(300);
		tgMyDepartmentConsultants.setCanDragRecordsOut(true);
		tgMyDepartmentConsultants.setDragDataAction(DragDataAction.COPY);
		tgMyDepartmentConsultants.setDataSource(dsMyDepartmentConsultants);
		
		
		rootNodeMyDepartmentConsultants = createDsNode("root", "My department", "silk/chart_organisation.png", "", null);
		dsMyDepartmentConsultants.addData(rootNodeMyDepartmentConsultants);
		
	}
	
	private TreeNode createDsNode(String id, String label, String icon, String parent, Personne obj) {
		TreeNode node = new TreeNode();
		node.setAttribute(PersonDataSource.KEY_ID, id);
		node.setAttribute(PersonDataSource.KEY_LABEL, label);
		node.setAttribute(PersonDataSource.KEY_ICON, icon);
		node.setAttribute(PersonDataSource.KEY_PARENT, parent);
		node.setAttribute(PersonDataSource.KEY_OBJECT, obj);
		node.setIsFolder(false);
		return node;
	}
	
	private void createTreeNodes(List<Personne> persons) {
		for (Personne person: persons) {
			dsMyDepartmentConsultants.addData(createDsNode( String.valueOf(person.getPerId()), person.getPerPrenom() + " "+ person.getPerNom(),"icons/16/person.png", "root", person));
		
		}
	}
	
	public void openAllNodesOfListGrid() {
		Timer timer = new Timer() {

			@Override
			public void run() {
				if (!tgMyDepartmentConsultants.isDrawn()) {
					return;
				}
				tgMyDepartmentConsultants.selectAllRecords();
					// fetch data to copy all element from datasource to inner tree of listGrid
					tgMyDepartmentConsultants.fetchData(null, new DSCallback() {

						public void execute(DSResponse dsresponse, Object obj, DSRequest dsrequest) {
							if (tgMyDepartmentConsultants.getTree()!=null) {
								tgMyDepartmentConsultants.getTree().openAll();
								tgMyDepartmentConsultants.selectAllRecords();
								endTimerAndPostProcess();
							}
						}
					});
					return;
			}

			/**
			 * cancel the timer and update a virtual field of the datasource (workaround to allow inline edit)
			 */
			private void endTimerAndPostProcess() {
				cancel();
				tgMyDepartmentConsultants.deselectAllRecords();
			}
		};
		timer.scheduleRepeating(1000);
	}


}
