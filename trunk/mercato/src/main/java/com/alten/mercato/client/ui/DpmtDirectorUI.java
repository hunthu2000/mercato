package com.alten.mercato.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.service.TransferService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.client.ui.util.ConstantsMercato;
import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

public class DpmtDirectorUI extends HLayout {

	public static final String VALIDATE_TRANSFER_TRANSITION = "validateTransfer";
	public static final String CANCEL_TRANSFER_TRANSITION = "cancelTransfer";
	public static final String VALIDATION_MSG = "Veillez décider";
	public static final String WAITING_MSG = "Attente de décision";
	public static final String VALIDATE_TRANSFER = "Accepter";
	public static final String CANCEL_TRANSFER = "Refuser";
	public static final String INCOMING_VALIDATION = "wait for validation";
	public static final String OUTGOING_VALIDATION = "validateTransferProposal";

	private ConsultantTreeGrid tgMyDepartmentConsultants = new ConsultantTreeGrid();
	private ConsultantTreeGrid tgOtherDepartmentConsultants = new ConsultantTreeGrid();
	private DetailViewer detailViewerMyDepartment = new DetailViewer(); 
	private DetailViewer detailViewerOtherDepartment = new DetailViewer(); 
	private TreeNode rootNodeMyDepartmentConsultants = null;
	private TreeNode rootNodeOtherDepartmentConsultants = null;
	private ListGridRecord myDepartmentDraggedRecord = null;
	private ListGridRecord otherDepartmentDraggedRecord = null;
	private String myValidatingNodeId = "";
	private String otherValidatingNodeId = "";



	public DpmtDirectorUI() {
		super();
		this.setWidth(1200);  
		this.setHeight(800);  
		this.setLayoutMargin(5);
		this.setMembersMargin(5);  
		this.setPadding(5);

		// setup the treegrid rootnodes, fields
		initMyDepartmentTreeGrid();
		initOtherDepartmentTreeGrid();
		VLayout vLayoutCenter = new VLayout();  
		vLayoutCenter.setHeight("80%");
		vLayoutCenter.setWidth(200);  
		vLayoutCenter.setMembersMargin(5);  
		vLayoutCenter.setLayoutMargin(5);  

		VLayout blankVLayout = new VLayout(); 
		blankVLayout.setHeight(200);
		blankVLayout.setWidth100();  
		blankVLayout.setMembersMargin(5);  
		blankVLayout.setLayoutMargin(5);  


		VLayout blankVLayoutBottum = new VLayout(); 
		blankVLayoutBottum.setHeight("40%");
		blankVLayoutBottum.setWidth100();  
		blankVLayoutBottum.setMembersMargin(5);  
		blankVLayoutBottum.setLayoutMargin(5);

		VLayout buttonVLayout = new VLayout(); 
		buttonVLayout.setHeight("20%");
		buttonVLayout.setWidth100(); 
		buttonVLayout.setMembersMargin(5);  
		buttonVLayout.setLayoutMargin(10);  

		IButton btnCancelProposal = new IButton();
		btnCancelProposal.setTitle("Annuler proposition");
		btnCancelProposal.setHeight(20);
		btnCancelProposal.setWidth100();
		btnCancelProposal.setIcon(ConstantsMercato.ICON_LEFT);
		btnCancelProposal.setIconAlign(Alignment.LEFT.getValue());
		
		btnCancelProposal.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				
				ListGridRecord[] records = tgOtherDepartmentConsultants.getSelection();
				List<Transfert> transferts = new ArrayList<Transfert>();
				
				for (ListGridRecord record:records) {
					if (record.getAttribute(ConstantsMercato.KEY_STATUS).equals(INCOMING_VALIDATION)) {
						Personne personne = (Personne) record.getAttributeAsObject(ConstantsMercato.KEY_OBJECT);
						if (personne.getTransferCourant()!= null) {
							transferts.add(personne.getTransferCourant());
						}
					}
				}
				
				onClickCancelPropositionRPC(transferts);
			}

			
		});

		IButton btnCancelRequest = new IButton();
		btnCancelRequest.setTitle("Annuler demande");
		btnCancelRequest.setHeight(20);
		btnCancelRequest.setWidth100();
		btnCancelRequest.setIcon(ConstantsMercato.ICON_RIGHT);
		btnCancelRequest.setIconAlign(Alignment.LEFT.getValue());

		buttonVLayout.addMember(btnCancelProposal);
		buttonVLayout.addMember(btnCancelRequest);



		vLayoutCenter.addMember(blankVLayout);  
		vLayoutCenter.addMember(buttonVLayout);  
		vLayoutCenter.addMember(blankVLayoutBottum);  
		// load the departments to "other departments treegrid"
		loadOtherDepartmentsByRPC();

		// load all consultants information
		loadConsultantTransferInfoByRPC();


		detailViewerMyDepartment.setWidth100();
		detailViewerMyDepartment.setHeight(200);
		detailViewerMyDepartment.setFields(  
				new DetailViewerField(ConstantsMercato.KEY_LABEL, "Nom"),  
				new DetailViewerField(ConstantsMercato.KEY_MATRICULE, "Matricule"),
				new DetailViewerField(ConstantsMercato.KEY_MY_HR_COMMENT, "Avis mon RH"),
				new DetailViewerField(ConstantsMercato.KEY_OTHER_HR_COMMENT, "Avis autre RH"));  

		detailViewerMyDepartment.setEmptyMessage("Cliquer une ligne pour voir le détail");

		detailViewerOtherDepartment.setWidth100();
		detailViewerOtherDepartment.setHeight(200);
		detailViewerOtherDepartment.setFields(  
				new DetailViewerField(ConstantsMercato.KEY_LABEL, "Nom"),  
				new DetailViewerField(ConstantsMercato.KEY_MATRICULE, "Matricule"),
				new DetailViewerField(ConstantsMercato.KEY_MY_HR_COMMENT, "Avis mon RH"),
				new DetailViewerField(ConstantsMercato.KEY_OTHER_HR_COMMENT, "Avis autre RH"));  

		detailViewerOtherDepartment.setEmptyMessage("Cliquer une ligne pour voir le détail");

		VLayout vLayoutLeft = new VLayout(); 
		vLayoutLeft.setWidth(500);
		vLayoutLeft.setHeight100();
		vLayoutLeft.setMembersMargin(5);

		vLayoutLeft.addMember(tgMyDepartmentConsultants);
		vLayoutLeft.addMember(detailViewerMyDepartment);

		VLayout vLayoutRight = new VLayout(); 
		vLayoutRight.setWidth(470);
		vLayoutRight.setHeight100();
		vLayoutRight.setMembersMargin(5);

		vLayoutRight.addMember(tgOtherDepartmentConsultants);
		vLayoutRight.addMember(detailViewerOtherDepartment);

		this.addMember(vLayoutLeft);
		this.addMember(vLayoutCenter);
		this.addMember(vLayoutRight);
	}


	/**
	 * 
	 */
	private void initMyDepartmentTreeGrid() {

		// define the tree fields
		TreeGridField name = new TreeGridField(ConstantsMercato.KEY_LABEL,"Consultant");
		name.setCanEdit(false);
		TreeGridField originalDpmt = new TreeGridField(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT,"Dpt. courant",90);
		name.setCanEdit(false);
		TreeGridField matricule = new TreeGridField(ConstantsMercato.KEY_MATRICULE, "Matricule",70);
		matricule.setCanEdit(false);

		//combo box for validation
		TreeGridField validation = new TreeGridField(ConstantsMercato.KEY_VALIDATION,"Validation", 150);
		validation.setValueMap(VALIDATE_TRANSFER, CANCEL_TRANSFER);
		validation.setCanEdit(true);
		tgMyDepartmentConsultants.setFields(name,originalDpmt,matricule, validation);

		//define the root nodes
		Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		TreeNode rootNode = createRootNode("root", "My department", ConstantsMercato.ICON_DPMT, null);
		rootNode.setCanAcceptDrop(false);
		tree.setRoot(rootNode);
		rootNodeMyDepartmentConsultants = createRootNode("rootMyDpmt", "Mon département", ConstantsMercato.ICON_DPMT, null);
		tree.add(rootNodeMyDepartmentConsultants, rootNode);
		tgMyDepartmentConsultants.setData(tree);
		tree.setNameProperty(ConstantsMercato.KEY_LABEL);
		tgMyDepartmentConsultants.getData().openAll();

		//handlers

		// stock the dragged record
		tgMyDepartmentConsultants.addDragStartHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				ListGridRecord record = tgMyDepartmentConsultants.getSelectedRecord();

				if (record.getAttribute(ConstantsMercato.KEY_TYPE).equals("folder")) {
					event.cancel();
				}

				//stop the drag event if the note has the status "validating"
				if (record.getAttribute(ConstantsMercato.KEY_VALIDATION).equals(VALIDATION_MSG)) {
					event.cancel();
				}
				setMyDepartmentDraggedRecord(tgMyDepartmentConsultants.getSelectedRecord());
			}

		});

		// when a node is dropped, start the transfer
		// TODO attention for the public proposal
		tgMyDepartmentConsultants.addDragStopHandler(new DragStopHandler() {

			public void onDragStop(DragStopEvent event) {
				System.out.println("drag stopped..");
				ListGridRecord record= getMyDepartmentDraggedRecord();
				if (record != null) {
					System.out.println("Record dragged " + record.getAttribute(ConstantsMercato.KEY_LABEL));

					TreeNode treeNode = tgOtherDepartmentConsultants.getTree().findById(record.getAttribute(ConstantsMercato.KEY_ID));
					if (treeNode!=null) {
						TreeNode parent = tgOtherDepartmentConsultants.getTree().getParent(treeNode);
						System.out.println("New parent " + parent.getAttribute(ConstantsMercato.KEY_LABEL));
						startOutgoingTransferProposalRPC(new Long(parent.getAttribute(ConstantsMercato.KEY_ID)).longValue(), new Long(treeNode.getAttribute(ConstantsMercato.KEY_ID)).longValue());
					}
				}
				setMyDepartmentDraggedRecord(null);
			}

		});

		tgMyDepartmentConsultants.addRecordClickHandler(new RecordClickHandler() {  
			public void onRecordClick(RecordClickEvent event) {  
				detailViewerMyDepartment.setData(tgMyDepartmentConsultants.getSelection());  
			}  
		});  

		// ignore non permitted validations
		tgMyDepartmentConsultants.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = tgMyDepartmentConsultants.getSelectedRecord();
				setMyValidatingNodeId(record.getAttribute(ConstantsMercato.KEY_ID));
				if (!record.getAttribute(ConstantsMercato.KEY_VALIDATION).equals(VALIDATION_MSG)) {
					event.cancel();
					SC.say("Pas de validation à effectuer");
					return;
				}

				System.out.println("Trying to change " + record.getAttribute(ConstantsMercato.KEY_LABEL));
			}

		});

		// validation confirmed handler
		tgMyDepartmentConsultants.addCellSavedHandler(new CellSavedHandler() {

			public void onCellSaved(CellSavedEvent event) {
				TreeNode treeNode = tgMyDepartmentConsultants.getTree().findById(getMyValidatingNodeId());
				System.out.println("validation changed!");
				if (treeNode != null) {
					System.out.println("To " + treeNode.getAttribute("validation") + " status.");
					Personne pers = (Personne) treeNode.getAttributeAsObject(ConstantsMercato.KEY_OBJECT);
					String validation;

					if (treeNode.getAttribute("validation").equals(VALIDATE_TRANSFER)) {
						validation = VALIDATE_TRANSFER_TRANSITION;
					} else {
						validation = CANCEL_TRANSFER_TRANSITION;
					}
					if ( pers.getTransferCourant() != null) {
						validateIncomingTransferRequestByRPC(pers.getTransferCourant(), validation);
					} else {
						SC.say("no transfer to validate, bug to correct!");
					}
				}
			}

		});

	}


	/**
	 * 
	 */
	private void initOtherDepartmentTreeGrid() {

		// define the tree fields
		TreeGridField name = new TreeGridField(ConstantsMercato.KEY_LABEL,"Consultant");
		name.setCanEdit(false);
		TreeGridField matricule = new TreeGridField(ConstantsMercato.KEY_MATRICULE, "Matricule",70);
		matricule.setCanEdit(false);
		TreeGridField validation = new TreeGridField(ConstantsMercato.KEY_VALIDATION,"Validation", 150);
		validation.setValueMap(VALIDATE_TRANSFER, CANCEL_TRANSFER);
		validation.setCanEdit(true);
		tgOtherDepartmentConsultants.setFields(name,matricule, validation);


		Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		TreeNode rootNode = createRootNode("root", "Autres départements", ConstantsMercato.ICON_DPMT, null);
		rootNode.setCanAcceptDrop(false);
		tree.setRoot(rootNode);
		rootNodeOtherDepartmentConsultants = createRootNode("rootOtherDpmt", "Autres départements", ConstantsMercato.ICON_DPMT, null);
		rootNodeOtherDepartmentConsultants.setCanAcceptDrop(false);
		tree.add(rootNodeOtherDepartmentConsultants, rootNode);
		tree.setNameProperty(ConstantsMercato.KEY_LABEL);
		tgOtherDepartmentConsultants.setData(tree);
		tgOtherDepartmentConsultants.getData().openAll();

		//handlers

		// stock the dragged record
		tgOtherDepartmentConsultants.addDragStartHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				ListGridRecord record = tgOtherDepartmentConsultants.getSelectedRecord();

				if (record.getAttribute(ConstantsMercato.KEY_TYPE).equals("folder")) {
					event.cancel();
				}

				//stop the drag event if the note has the status "validating"
				if (record.getAttribute(ConstantsMercato.KEY_VALIDATION).equals(VALIDATION_MSG)) {
					event.cancel();
				}

				setOtherDepartmentDraggedRecord(tgOtherDepartmentConsultants.getSelectedRecord());
			}

		});

		// when a node is dropped, start the transfer
		tgOtherDepartmentConsultants.addDragStopHandler(new DragStopHandler() {

			public void onDragStop(DragStopEvent event) {
				ListGridRecord record= getOtherDepartmentDraggedRecord();
				if (record != null) {
					System.out.println("Record dragged " + record.getAttribute(ConstantsMercato.KEY_LABEL));

					TreeNode treeNode = tgMyDepartmentConsultants.getTree().findById(record.getAttribute(ConstantsMercato.KEY_ID));
					if (treeNode!=null) {
						TreeNode parent = tgMyDepartmentConsultants.getTree().getParent(treeNode);
						System.out.println("New parent " + parent.getAttribute(ConstantsMercato.KEY_LABEL));
						//start the workflow process
						System.out.println("starting transfer request for consultant " + treeNode.getAttribute(ConstantsMercato.KEY_LABEL) + " from " + treeNode.getAttribute(ConstantsMercato.KEY_DEPARTMENT) + " to " + ConstantsMercato.getCurrentUser().getDepartement().getDepLib() );
						startIncomingTransferRequestByRPC(ConstantsMercato.getCurrentUser().getDepartement().getDepId(), new Long(treeNode.getAttribute(ConstantsMercato.KEY_ID)).longValue());
					}
				}
				setOtherDepartmentDraggedRecord(null);
			}

		});

		// ignore non permitted validations
		tgOtherDepartmentConsultants.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			public void onCellDoubleClick(CellDoubleClickEvent event) {

				ListGridRecord record = tgOtherDepartmentConsultants.getSelectedRecord();
				setOtherValidatingNodeId(record.getAttribute(ConstantsMercato.KEY_ID));
				if (!record.getAttribute(ConstantsMercato.KEY_VALIDATION).equals(VALIDATION_MSG)) {
					event.cancel();
					SC.say("Pas de validation à effectuer");
					return;
				}

				System.out.println("Trying to change " + record.getAttribute(ConstantsMercato.KEY_LABEL));

			}

		});

		// validation confirmed handler
		tgOtherDepartmentConsultants.addCellSavedHandler(new CellSavedHandler() {

			public void onCellSaved(CellSavedEvent event) {
				TreeNode treeNode = tgOtherDepartmentConsultants.getTree().findById(getOtherValidatingNodeId());
				setOtherValidatingNodeId("");
				System.out.println("validation changed!");
				if (treeNode.getAttribute("validation").equals(VALIDATION_MSG)) {
					SC.say("Please choose a validation option");
					return;
				}
				if (treeNode != null) {
					System.out.println("To " + treeNode.getAttribute("validation") + " status.");
					Personne pers = (Personne) treeNode.getAttributeAsObject(ConstantsMercato.KEY_OBJECT);
					String validation;

					if (treeNode.getAttribute("validation").equals(VALIDATE_TRANSFER)) {
						validation = VALIDATE_TRANSFER_TRANSITION;
					} else {
						validation = CANCEL_TRANSFER_TRANSITION;
					}
					if ( pers.getTransferCourant() != null) {
						validateOutgoingTransferRequestByRPC(pers.getTransferCourant(), validation);
					} else {
						SC.say("no transfer to validate, bug to correct!");
					}

				}
			}

		});

		tgOtherDepartmentConsultants.addRecordClickHandler(new RecordClickHandler() {  
			public void onRecordClick(RecordClickEvent event) {  
				detailViewerOtherDepartment.setData(tgOtherDepartmentConsultants.getSelection());  
			}  
		});  


	}

	private void onClickCancelPropositionRPC(List<Transfert> transferts) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();
		AsyncCallback<List<Personne>> callback = new AsyncCallback<List<Personne>>() {

			public void onFailure(Throwable arg0) {
				dlg.hide();
				SC.say("Error loading consultant information");

			}

			public void onSuccess(List<Personne> result) {
				dlg.hide();
				if ((result!=null) && (result.size()>0)) {
					for (Personne personne: result) {
						updateOutgoingTransferedTreeNode(personne);
					}
				}

			}

		};
		TransferService.Util.getInstance().signalCancelTransfer(transferts, callback);
		dlg.show();
		
	}


	/**
	 * 
	 */
	private void loadConsultantTransferInfoByRPC() {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();
		AsyncCallback<List<InfoTransfer>> callback = new AsyncCallback<List<InfoTransfer>>() {

			public void onFailure(Throwable arg0) {
				dlg.hide();
				SC.say("Error loading consultant information");

			}

			public void onSuccess(List<InfoTransfer> result) {
				dlg.hide();
				if ((result!=null) && (result.size()>0)) {
					createConsultantTreeNodes(result);
				}

			}

		};
		TransferService.Util.getInstance().getConsultantWithTransferInfo(callback);
		dlg.show();
	}

	/**
	 * 
	 */
	private void loadOtherDepartmentsByRPC() {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Departement>> callback = new AsyncCallback<List<Departement>>() {

			public void onSuccess(List<Departement> result) {
				dlg.hide();
				if (result==null||result.size()==0) {
					return;
				}
				System.out.println(result.size() + " department for this dd found");

				createOtherDepartmentTreeNodes(result);
			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say("Error loading departments");
			}
		};

		PersonService.Util.getInstance().getOtherDepartements(ConstantsMercato.getCurrentUser().getDepartement().getDepId(),callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}

	/**
	 * @param transDepEntrId
	 * @param transDepConsulId
	 */
	private void startIncomingTransferRequestByRPC(long transDepEntrId, long transDepConsulId) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onSuccess(Personne result) {
				if (result!=null) {
					dlg.hide();
					updateIncomingRequestedTreeNode(result);
					return;
				}

			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		TransferService.Util.getInstance().startAndAskTransferProcess(transDepEntrId, transDepConsulId, callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}

	private void startOutgoingTransferProposalRPC(long transDepEntrId, long transDepConsulId) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onSuccess(Personne result) {
				if (result!=null) {
					dlg.hide();
					updateOutgoingProposedTreeNode(result);
					return;
				}

			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		//TransferService.Util.getInstance().startAndProposeTransferProcess(transDepEntrId, transDepConsulId, callback);
		TransferService.Util.getInstance().startAndProposeTransferProcess(transDepEntrId, transDepConsulId, callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}
	/**
	 * @param transfert
	 * @param validation
	 */
	private void validateOutgoingTransferRequestByRPC(Transfert transfert, String validation) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onSuccess(Personne result) {
				if (result!=null) {
					dlg.hide();
					updateOutgoingTransferedTreeNode(result);
					return;
				}

			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		TransferService.Util.getInstance().validateTransferRequestProcess(transfert, validation, callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}

	/**
	 * @param transfert
	 * @param validation
	 */
	private void validateIncomingTransferRequestByRPC(Transfert transfert, String validation) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onSuccess(Personne result) {
				if (result!=null) {
					dlg.hide();
					updateIncomingTransferedTreeNode(result);
					return;
				}

			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		TransferService.Util.getInstance().signalValidateTransferProposalV2(transfert, validation, callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}

	/**
	 * @param pers
	 */
	private void updateIncomingRequestedTreeNode(Personne pers) {
		Tree treeMyDepartment = tgMyDepartmentConsultants.getTree();

		TreeNode oldTreeNode = treeMyDepartment.findById(String.valueOf(pers.getPerId()));
		treeMyDepartment.remove(oldTreeNode);

		TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
		TreeNode node = new TreeNode();
		node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
		node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
		node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
		node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
		node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
		node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
		node.setAttribute(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT, pers.getDepartement().getDepLib());
		node.setAttribute(ConstantsMercato.KEY_VALIDATION, WAITING_MSG);
		node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
		node.setCanAcceptDrop(false);
		node.setIsFolder(false);
		node.setEnabled(false);

		treeMyDepartment.add(node, parentNode);
		tgMyDepartmentConsultants.setData(treeMyDepartment);
		tgMyDepartmentConsultants.getData().openAll();

	}

	/**
	 * @param pers
	 */
	private void updateOutgoingProposedTreeNode(Personne pers) {
		Tree treeOtherDepartment = tgOtherDepartmentConsultants.getTree();

		TreeNode oldTreeNode = treeOtherDepartment.findById(String.valueOf(pers.getPerId()));
		treeOtherDepartment.remove(oldTreeNode);

		TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(pers.getTransferCourant().getDepEntr().getDepId()));
		TreeNode node = new TreeNode();
		node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
		node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
		node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
		node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
		node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER);
		node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
		node.setAttribute(ConstantsMercato.KEY_VALIDATION, WAITING_MSG);
		node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
		node.setCanAcceptDrop(false);
		node.setCanDrag(false);
		node.setIsFolder(false);

		treeOtherDepartment.add(node, parentNode);
		tgOtherDepartmentConsultants.setData(treeOtherDepartment);
		tgOtherDepartmentConsultants.getData().openAll();

	}


	/**
	 * @param pers
	 */
	private void updateOutgoingTransferedTreeNode(Personne pers) {
		Tree treeOtherDepartment = tgOtherDepartmentConsultants.getTree();

		TreeNode oldTreeNode = treeOtherDepartment.findById(String.valueOf(pers.getPerId()));
		treeOtherDepartment.remove(oldTreeNode);

		TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(pers.getDepartement().getDepId()));
		if (parentNode == null) {
			Tree treeMyDepartment = tgMyDepartmentConsultants.getTree();
			parentNode = treeMyDepartment.findById("rootMyDpmt");

			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
			node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
			node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER);
			node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
			node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);

			treeMyDepartment.add(node, parentNode);
			tgMyDepartmentConsultants.setData(treeMyDepartment);
			tgMyDepartmentConsultants.getData().openAll();

		} else {
			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
			node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
			node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
			node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
			node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);

			treeOtherDepartment.add(node, parentNode);
			tgOtherDepartmentConsultants.setData(treeOtherDepartment);
			tgOtherDepartmentConsultants.getData().openAll();
		}
	}


	/**
	 * @param pers
	 */
	private void updateIncomingTransferedTreeNode(Personne pers) {
		Tree treeMyDepartment = tgMyDepartmentConsultants.getTree();

		TreeNode oldTreeNode = treeMyDepartment.findById(String.valueOf(pers.getPerId()));
		treeMyDepartment.remove(oldTreeNode);

		TreeNode parentNode = null;
		if (pers.getDepartement().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {

			parentNode = treeMyDepartment.findById("rootMyDpmt");

			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
			node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
			node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER);
			node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
			node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);

			treeMyDepartment.add(node, parentNode);
			tgMyDepartmentConsultants.setData(treeMyDepartment);
			tgMyDepartmentConsultants.getData().openAll();

		} else {
			Tree treeOtherDepartment = tgOtherDepartmentConsultants.getTree();
			parentNode = treeOtherDepartment.findById(String.valueOf(pers.getDepartement().getDepId()));

			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
			node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
			node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
			node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
			node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);

			treeOtherDepartment.add(node, parentNode);
			tgOtherDepartmentConsultants.setData(treeOtherDepartment);
			tgOtherDepartmentConsultants.getData().openAll();
		}
	}
	/**
	 * @param record
	 */
	private void setMyDepartmentDraggedRecord(ListGridRecord record) {
		myDepartmentDraggedRecord = record;
	}

	/**
	 * @return
	 */
	private ListGridRecord getMyDepartmentDraggedRecord() {
		return this.myDepartmentDraggedRecord;
	}


	/**
	 * @param record
	 */
	private void setOtherDepartmentDraggedRecord(ListGridRecord record) {
		otherDepartmentDraggedRecord = record;
	}

	/**
	 * @return
	 */
	private ListGridRecord getOtherDepartmentDraggedRecord() {
		return this.otherDepartmentDraggedRecord;
	}

	/**
	 * @return the validatingNodeId
	 */
	private String getMyValidatingNodeId() {
		return myValidatingNodeId;
	}

	/**
	 * @param validatingNodeId the validatingNodeId to set
	 */
	private void setMyValidatingNodeId(String validatingNodeId) {
		this.myValidatingNodeId = validatingNodeId;
	}

	/**
	 * @return the otherValidatingNodeId
	 */
	private String getOtherValidatingNodeId() {
		return otherValidatingNodeId;
	}

	/**
	 * @param otherValidatingNodeId the otherValidatingNodeId to set
	 */
	private void setOtherValidatingNodeId(String otherValidatingNodeId) {
		this.otherValidatingNodeId = otherValidatingNodeId;
	}

	/**
	 * @param id
	 * @param label
	 * @param icon
	 * @param obj
	 * @return
	 */
	private TreeNode createRootNode(String id, String label, String icon, Object obj) {
		TreeNode node = new TreeNode();
		node.setAttribute(ConstantsMercato.KEY_ID, id);
		node.setAttribute(ConstantsMercato.KEY_LABEL, label);
		node.setAttribute(ConstantsMercato.KEY_ICON, icon);
		node.setAttribute(ConstantsMercato.KEY_MATRICULE, "");
		node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, "");
		node.setAttribute(ConstantsMercato.KEY_OBJECT, obj);
		node.setAttribute(ConstantsMercato.KEY_TYPE, "folder");
		node.setCanDrag(false);
		node.setIsFolder(true);
		return node;
	}


	/**
	 * @param infoTransfers
	 */
	private void createConsultantTreeNodes(List<InfoTransfer> infoTransfers) {
		Tree treeMyDepartment = tgMyDepartmentConsultants.getTree();
		Tree treeOtherDepartment = tgOtherDepartmentConsultants.getTree();
		for (InfoTransfer infoTransfer: infoTransfers) {


			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(infoTransfer.getPersonne().getPerId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, infoTransfer.getPersonne().getPerPrenom() + " "+ infoTransfer.getPersonne().getPerNom());
			node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, infoTransfer.getPersonne().getPerMatricule());
			node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, infoTransfer.getPersonne().getDepartement().getDepLib());
			node.setAttribute(ConstantsMercato.KEY_OBJECT, infoTransfer.getPersonne());
			node.setAttribute(ConstantsMercato.KEY_STATUS, infoTransfer.getTransferStatus());
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);

			// Check if it is my departement
			if (infoTransfer.getPersonne().getDepartement().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {
				node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER);

				// if the personne has no transfer
				if (infoTransfer.getPersonne().getTransferCourant() == null) {
					TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
					node.setAttribute(ConstantsMercato.KEY_MY_HR_COMMENT, "");
					node.setAttribute(ConstantsMercato.KEY_OTHER_HR_COMMENT, "");
					node.setCanDrag(true);
					treeMyDepartment.add(node, parentNode);
				} else {

					TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepId()));
					if (infoTransfer.getTransferStatus().equals(INCOMING_VALIDATION)) {
						node.setCanDrag(false);
						node.setAttribute(ConstantsMercato.KEY_MY_HR_COMMENT, infoTransfer.getCommentHR1());
						node.setAttribute(ConstantsMercato.KEY_OTHER_HR_COMMENT, infoTransfer.getCommentHR2());
						node.setAttribute(ConstantsMercato.KEY_VALIDATION, WAITING_MSG);
					} else {
						// TODO modify after the introduction of transfer proposal
						node.setEnabled(false);
						node.setAttribute(ConstantsMercato.KEY_MY_HR_COMMENT, infoTransfer.getCommentHR2());
						node.setAttribute(ConstantsMercato.KEY_OTHER_HR_COMMENT, infoTransfer.getCommentHR1());
						node.setAttribute(ConstantsMercato.KEY_VALIDATION, VALIDATION_MSG);
					}

					treeOtherDepartment.add(node, parentNode);
				}
			} else {
				node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
				if (infoTransfer.getPersonne().getTransferCourant() == null) {
					TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(infoTransfer.getPersonne().getDepartement().getDepId()));
					node.setAttribute(ConstantsMercato.KEY_MY_HR_COMMENT, "");
					node.setAttribute(ConstantsMercato.KEY_OTHER_HR_COMMENT, "");
					node.setCanDrag(true);
					treeOtherDepartment.add(node, parentNode);
				} else {
					if (infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {

						TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
						node.setAttribute(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT, infoTransfer.getPersonne().getDepartement().getDepLib());
						if (infoTransfer.getTransferStatus().equals(INCOMING_VALIDATION)) {
							node.setAttribute(ConstantsMercato.KEY_MY_HR_COMMENT, infoTransfer.getCommentHR2());
							node.setAttribute(ConstantsMercato.KEY_OTHER_HR_COMMENT, infoTransfer.getCommentHR1());
							node.setAttribute(ConstantsMercato.KEY_VALIDATION, VALIDATION_MSG);
							node.setCanDrag(false);
						} else {
							// TODO modify after the introduction of transfer proposal
							node.setCanDrag(false);
							node.setAttribute(ConstantsMercato.KEY_VALIDATION, WAITING_MSG);
						}
						treeMyDepartment.add(node, parentNode);
					}
				}
			}
		}

		tgMyDepartmentConsultants.setData(treeMyDepartment);
		tgMyDepartmentConsultants.getData().openAll();
		tgOtherDepartmentConsultants.setData(treeOtherDepartment);
		tgOtherDepartmentConsultants.getData().openAll();
	}

	/**
	 * Create the department list tree node without the own department
	 * @param departements
	 */
	private void createOtherDepartmentTreeNodes(List<Departement> departements) {


		Tree tree = tgOtherDepartmentConsultants.getTree();
		Object obj = null;
		for (Departement departement: departements) {
			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(departement.getDepId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, departement.getDepLib());
			node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_DPMT);
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, "");
			node.setAttribute(ConstantsMercato.KEY_OBJECT, obj);
			node.setAttribute(ConstantsMercato.KEY_TYPE, "folder");
			node.setIsFolder(true);
			node.setCanDrag(false);
			TreeNode parentNode = tree.findById("rootOtherDpmt");
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
	 * custom tree grid for both my department consultant treegrid and other department consultant treegrd
	 * @author Huage Chen
	 *
	 */
	public static class ConsultantTreeGrid extends TreeGrid {  
		public ConsultantTreeGrid() {  
			super();
			setHeight(480);
			setWidth100();
			setCanDragRecordsOut(true);
			setDragDataAction(DragDataAction.MOVE);
			setCanAcceptDroppedRecords(true);
			setAlternateRecordStyles(true);
			setShowHeader(true);
			setLeaveScrollbarGap(false);
			setEditByCell(true);
			setCanReorderRecords(false);
			setCanReparentNodes(false);

		}  

		@Override
		public Boolean willAcceptDrop() {
			return true;
		}
	}  


}
