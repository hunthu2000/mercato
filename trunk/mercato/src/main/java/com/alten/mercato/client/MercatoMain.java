/**
 * 
 */
package com.alten.mercato.client;

import java.util.List;

import com.alten.mercato.client.data.ds.PersonDataSource;
import com.alten.mercato.client.service.DemoService;
import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.server.model.Departement;
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
	private TreeGrid tgOtherDepartmentConsultants = new TreeGrid();
	private TreeNode rootNodeMyDepartmentConsultants = null;
	private TreeNode rootNodeOtherDepartmentConsultants = null;
	private PersonDataSource dsMyDepartmentConsultants = new PersonDataSource("myDepartment");
	private PersonDataSource dsOtherDepartmentConsultants = new PersonDataSource("otherDepartments");

	public void onModuleLoad() {
		
		HLayout hlayout = new HLayout();  
        hlayout.setWidth(800);  
        hlayout.setHeight100();  
        hlayout.setMembersMargin(5);  
		hlayout.setPadding(5);
		
		initMyDepartmentTreeGrid();
		loadListConsultantsByRPC();
		
		initOtherDepartmentTreeGrid();
		loadOtherDepartmentsByRPC();
		loadOtherDepartmentListConsultantsByRPC();
		
		hlayout.addMember(tgMyDepartmentConsultants);
		hlayout.addMember(tgOtherDepartmentConsultants);
		
		Canvas canvas = new Canvas();
		canvas.addChild(hlayout);
		
		
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
				openAllNodesOfListGrid(tgMyDepartmentConsultants);
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
	
	private void loadOtherDepartmentListConsultantsByRPC() {

		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Personne>> callback = new AsyncCallback<List<Personne>>() {

			public void onSuccess(List<Personne> result) {
				if (result==null||result.size()==0) {
					dlg.hide();
					return;
				}
				System.out.println(result.size() + " consultants for this dd found");
				createOtherDepartmentTreeNodes(result);
				dlg.hide();
			}

			public void onFailure(Throwable ex) {
				dlg.hide();
			}
		};

		PersonService.Util.getInstance().getOtherDepartmentConsultantsForDD(callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}
	
	private void loadOtherDepartmentsByRPC() {

		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Departement>> callback = new AsyncCallback<List<Departement>>() {

			public void onSuccess(List<Departement> result) {
				if (result==null||result.size()==0) {
					dlg.hide();
					return;
				}
				System.out.println(result.size() + " department for this dd found");
				for (Departement dpmt: result) {
					dsOtherDepartmentConsultants.addData(createDsDpmtNode( String.valueOf(dpmt.getDepId()), dpmt.getDepLib(),"silk/chart_organisation.png", "root", dpmt));
				}
				openAllNodesOfListGrid(tgOtherDepartmentConsultants);
				dlg.hide();
			}

			public void onFailure(Throwable ex) {
				dlg.hide();
			}
		};

		PersonService.Util.getInstance().getOtherDepartements(callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}
	
	
	/**
	 * 
	 */
	private void initMyDepartmentTreeGrid() {
		tgMyDepartmentConsultants.setAutoFetchData(true);
		tgMyDepartmentConsultants.setLoadDataOnDemand(false); 
		tgMyDepartmentConsultants.setHeight(400);
		tgMyDepartmentConsultants.setWidth("50%");
		tgMyDepartmentConsultants.setCanDragRecordsOut(true);
		tgMyDepartmentConsultants.setDragDataAction(DragDataAction.COPY);
		tgMyDepartmentConsultants.setCanAcceptDroppedRecords(true);
		tgMyDepartmentConsultants.setDataSource(dsMyDepartmentConsultants);
		
		
		rootNodeMyDepartmentConsultants = createDsNode("root", "My department", "silk/chart_organisation.png", "", null);
		dsMyDepartmentConsultants.addData(rootNodeMyDepartmentConsultants);
		
	}
	
	private void initOtherDepartmentTreeGrid() {
		tgOtherDepartmentConsultants.setAutoFetchData(true);
		tgOtherDepartmentConsultants.setLoadDataOnDemand(false); 
		tgOtherDepartmentConsultants.setHeight(400);
		tgOtherDepartmentConsultants.setWidth("50%");
		tgOtherDepartmentConsultants.setCanDragRecordsOut(true);
		tgOtherDepartmentConsultants.setDragDataAction(DragDataAction.COPY);
		tgOtherDepartmentConsultants.setCanAcceptDroppedRecords(true);
		tgOtherDepartmentConsultants.setCanReorderRecords(false);
		tgOtherDepartmentConsultants.setDataSource(dsOtherDepartmentConsultants);
		
		
		rootNodeOtherDepartmentConsultants = createDsNode("root", "Other departments", "silk/chart_organisation.png", "", null);
		dsOtherDepartmentConsultants.addData(rootNodeOtherDepartmentConsultants);
		
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
	
	private TreeNode createDsDpmtNode(String id, String label, String icon, String parent, Departement obj) {
		TreeNode node = new TreeNode();
		node.setAttribute(PersonDataSource.KEY_ID, id);
		node.setAttribute(PersonDataSource.KEY_LABEL, label);
		node.setAttribute(PersonDataSource.KEY_ICON, icon);
		node.setAttribute(PersonDataSource.KEY_PARENT, parent);
		node.setAttribute(PersonDataSource.KEY_OBJECT, obj);
		node.setIsFolder(true);
		return node;
	}
	
	private void createTreeNodes(List<Personne> persons) {
		for (Personne person: persons) {
			dsMyDepartmentConsultants.addData(createDsNode( String.valueOf(person.getPerId()), person.getPerPrenom() + " "+ person.getPerNom(),"icons/16/person.png", "root", person));
		
		}
	}
	
	private void createOtherDepartmentTreeNodes(List<Personne> persons) {
		for (Personne person: persons) {
			dsOtherDepartmentConsultants.addData(createDsNode( String.valueOf(person.getPerId()), person.getPerPrenom() + " "+ person.getPerNom(),"icons/16/person.png", String.valueOf(person.getDepartement().getDepId()), person));
		
		}
	}
	
	public void openAllNodesOfListGrid(final TreeGrid treegrid) {
		Timer timer = new Timer() {

			@Override
			public void run() {
				if (!treegrid.isDrawn()) {
					return;
				}
				treegrid.selectAllRecords();
					// fetch data to copy all element from datasource to inner tree of listGrid
				treegrid.fetchData(null, new DSCallback() {

						public void execute(DSResponse dsresponse, Object obj, DSRequest dsrequest) {
							if (treegrid.getTree()!=null) {
								treegrid.getTree().openAll();
								treegrid.selectAllRecords();
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
				treegrid.deselectAllRecords();
			}
		};
		timer.scheduleRepeating(1000);
	}


}
