/**
 * 
 */
package com.alten.mercato.client;

import java.util.List;

import com.alten.mercato.client.data.ds.PersonDataSource;
import com.alten.mercato.client.service.DemoService;
import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.client.ui.util.ConstantsMercato;
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
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;


/**
 * @author Huage Chen
 *
 */
public class MercatoMain implements EntryPoint {
	private ConsultantTreeGrid tgMyDepartmentConsultants = new ConsultantTreeGrid();
	private ConsultantTreeGrid tgOtherDepartmentConsultants = new ConsultantTreeGrid();
	private TreeNode rootNodeMyDepartmentConsultants = null;
	private TreeNode rootNodeOtherDepartmentConsultants = null;
	private PersonDataSource dsMyDepartmentConsultants = new PersonDataSource("myDepartment");
	private PersonDataSource dsOtherDepartmentConsultants = new PersonDataSource("otherDepartments");
	private ListGridRecord myDepartmentDraggedRecord = null;
	private ListGridRecord otherDepartmentDraggedRecord = null;

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {

		HLayout hlayout = new HLayout();  
		hlayout.setWidth(800);  
		hlayout.setHeight(600);  
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
				createMyConsultantTreeNodes(result);
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
	private void loadOtherDepartmentListConsultantsByRPC() {

		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Personne>> callback = new AsyncCallback<List<Personne>>() {

			public void onSuccess(List<Personne> result) {
				if (result==null||result.size()==0) {
					dlg.hide();
					return;
				}
				System.out.println(result.size() + " consultants for this dd found");
				createOtherDepartmentConsultantTreeNodes(result);
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

	/**
	 * 
	 */
	private void loadOtherDepartmentsByRPC() {

		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Departement>> callback = new AsyncCallback<List<Departement>>() {

			public void onSuccess(List<Departement> result) {
				if (result==null||result.size()==0) {
					dlg.hide();
					return;
				}
				System.out.println(result.size() + " department for this dd found");

				createOtherDepartmentTreeNodes(result);
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

	private void setMyDepartmentDraggedRecord(ListGridRecord record) {
		myDepartmentDraggedRecord = record;
	}
	
	private ListGridRecord getMyDepartmentDraggedRecord() {
		return this.myDepartmentDraggedRecord;
	}
	
	
	private void setOtherDepartmentDraggedRecord(ListGridRecord record) {
		otherDepartmentDraggedRecord = record;
	}
	
	private ListGridRecord getOtherDepartmentDraggedRecord() {
		return this.otherDepartmentDraggedRecord;
	}
	/**
	 * 
	 */
	private void initMyDepartmentTreeGrid() {

		Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		TreeNode rootNode = createRootNode("root", "My department", ConstantsMercato.ICON_DPMT, null);
		rootNode.setCanAcceptDrop(false);
		tree.setRoot(rootNode);
		rootNodeMyDepartmentConsultants = createRootNode("rootMyDpmt", "My department", ConstantsMercato.ICON_DPMT, null);
		tree.add(rootNodeMyDepartmentConsultants, rootNode);
		//rootNodeMyDepartmentConsultants.setCanAcceptDrop(true);
		tgMyDepartmentConsultants.setData(tree);
		tree.setNameProperty(PersonDataSource.KEY_LABEL);
		tgMyDepartmentConsultants.getData().openAll();
		
		tgMyDepartmentConsultants.addDragStartHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				setMyDepartmentDraggedRecord(tgMyDepartmentConsultants.getSelectedRecord());
			}
			
		});
		
		tgMyDepartmentConsultants.addDragStopHandler(new DragStopHandler() {

			public void onDragStop(DragStopEvent event) {
				System.out.println("drag stopped..");
				ListGridRecord record= getMyDepartmentDraggedRecord();
				if (record != null) {
					System.out.println("Record dragged " + record.getAttribute(PersonDataSource.KEY_LABEL));
					
					TreeNode treeNode = tgOtherDepartmentConsultants.getTree().findById(record.getAttribute(PersonDataSource.KEY_ID));
					if (treeNode!=null) {
						TreeNode parent = tgOtherDepartmentConsultants.getTree().getParent(treeNode);
						System.out.println("New parent " + parent.getAttribute(PersonDataSource.KEY_LABEL));
					}
				}
				setMyDepartmentDraggedRecord(null);
			}
			
		});

	}

	/**
	 * 
	 */
	private void initOtherDepartmentTreeGrid() {

		Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		TreeNode rootNode = createRootNode("root", "Other departments", ConstantsMercato.ICON_DPMT, null);
		rootNode.setCanAcceptDrop(false);
		tree.setRoot(rootNode);
		rootNodeOtherDepartmentConsultants = createRootNode("rootOtherDpmt", "Other departments", ConstantsMercato.ICON_DPMT, null);
		rootNodeOtherDepartmentConsultants.setCanAcceptDrop(false);
		tree.add(rootNodeOtherDepartmentConsultants, rootNode);
		tree.setNameProperty(PersonDataSource.KEY_LABEL);
		tgOtherDepartmentConsultants.setData(tree);
		tgOtherDepartmentConsultants.getData().openAll();
		
		tgOtherDepartmentConsultants.addDragStartHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				setOtherDepartmentDraggedRecord(tgOtherDepartmentConsultants.getSelectedRecord());
			}
			
		});
		
		tgOtherDepartmentConsultants.addDragStopHandler(new DragStopHandler() {

			public void onDragStop(DragStopEvent event) {
				ListGridRecord record= getOtherDepartmentDraggedRecord();
				if (record != null) {
					System.out.println("Record dragged " + record.getAttribute(PersonDataSource.KEY_LABEL));
					
					TreeNode treeNode = tgMyDepartmentConsultants.getTree().findById(record.getAttribute(PersonDataSource.KEY_ID));
					if (treeNode!=null) {
						TreeNode parent = tgMyDepartmentConsultants.getTree().getParent(treeNode);
						System.out.println("New parent " + parent.getAttribute(PersonDataSource.KEY_LABEL));
					}
				}
				setOtherDepartmentDraggedRecord(null);
				
			}
			
		});
		
	}

	private TreeNode createRootNode(String id, String label, String icon, Object obj) {
		TreeNode node = new TreeNode();
		node.setAttribute(PersonDataSource.KEY_ID, id);
		node.setAttribute(PersonDataSource.KEY_LABEL, label);
		node.setAttribute(PersonDataSource.KEY_ICON, icon);
		node.setAttribute(PersonDataSource.KEY_OBJECT, obj);
		return node;
	}


	/**
	 * @param departements
	 */
	private void createOtherDepartmentTreeNodes(List<Departement> departements) {


		Tree tree = tgOtherDepartmentConsultants.getTree();
		Object obj = null;
		for (Departement departement: departements) {
			TreeNode node = new TreeNode();
			node.setAttribute(PersonDataSource.KEY_ID, String.valueOf(departement.getDepId()));
			node.setAttribute(PersonDataSource.KEY_LABEL, departement.getDepLib());
			node.setAttribute(PersonDataSource.KEY_ICON, ConstantsMercato.ICON_DPMT);
			node.setAttribute(PersonDataSource.KEY_OBJECT, obj);
			node.setIsFolder(true);
			TreeNode parentNode = tree.findById("rootOtherDpmt");
			tree.add(node, parentNode);
		}

		tgOtherDepartmentConsultants.setData(tree);
		tgOtherDepartmentConsultants.getData().openAll();
	}


	/**
	 * @param persons
	 */
	private void createMyConsultantTreeNodes(List<Personne> persons) {
		/*for (Personne person: persons) {
			dsOtherDepartmentConsultants.addData(createOtherDepartementDsNode( String.valueOf(person.getPerId()), person.getPerPrenom() + " "+ person.getPerNom(),ConstantsMercato.ICON_USER, String.valueOf(person.getDepartement().getDepId()), person));

		}*/

		Tree tree = tgMyDepartmentConsultants.getTree();

		for (Personne person: persons) {
			TreeNode node = new TreeNode();
			node.setAttribute(PersonDataSource.KEY_ID, String.valueOf(person.getPerId()));
			node.setAttribute(PersonDataSource.KEY_LABEL, person.getPerPrenom() + " "+ person.getPerNom());
			node.setAttribute(PersonDataSource.KEY_ICON, ConstantsMercato.ICON_USER);
			TreeNode parentNode = tree.findById("rootMyDpmt");
			node.setAttribute(PersonDataSource.KEY_OBJECT, person);
			//node.setCanAcceptDrop(false);

			tree.add(node, parentNode);
		}

		tgMyDepartmentConsultants.setData(tree);
		tgMyDepartmentConsultants.getData().openAll();
	}


	/**
	 * @param persons
	 */
	private void createOtherDepartmentConsultantTreeNodes(List<Personne> persons) {

		Tree tree = tgOtherDepartmentConsultants.getTree();

		for (Personne person: persons) {
			TreeNode node = new TreeNode();
			node.setAttribute(PersonDataSource.KEY_ID, String.valueOf(person.getPerId()));
			node.setAttribute(PersonDataSource.KEY_LABEL, person.getPerPrenom() + " "+ person.getPerNom());
			node.setAttribute(PersonDataSource.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);

			TreeNode parentNode = tree.findById(String.valueOf(person.getDepartement().getDepId()));
			System.out.println("parent id :" + parentNode.getAttribute(ConstantsMercato.KEY_ID));
			node.setAttribute(PersonDataSource.KEY_OBJECT, person);
			//node.setCanAcceptDrop(true);

			tree.add(node, parentNode);
		}

		tgOtherDepartmentConsultants.setData(tree);
		tgOtherDepartmentConsultants.getData().openAll();
	}

	/**
	 * @param treegrid
	 */
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

	/**
	 * @author Huage Chen
	 *
	 */
	public static class ConsultantTreeGrid extends TreeGrid {  
		public ConsultantTreeGrid() {  
			super();
			setHeight100();
			setWidth("50%");
			setCanDragRecordsOut(true);
			setDragDataAction(DragDataAction.MOVE);
			setCanAcceptDroppedRecords(true);
			setAlternateRecordStyles(true);
			setShowHeader(true);
			setLeaveScrollbarGap(false);
		}  
		
		@Override
		public Boolean willAcceptDrop() {
			return true;
		}

	}  
}
