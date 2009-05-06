/**
 * 
 */
package com.alten.mercato.client.ui;

import java.util.List;

import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.service.TransferService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.client.ui.util.ConstantsMercato;
import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * @author Huage Chen
 *
 */
public class HRUI extends HLayout {
	public static final String COMMENT_MSG = "Votre avis?";
	public static final String WAITING_MSG = "Attente de décision";
	public static final String POSITIVE_COMMENT = "Positif";
	public static final String NEGATIVE_COMMENT = "Négatif";
	public static final String WAIT_FOR_VALIDATION_PROPOSAL = "wait for validation";
	public static final String OUTGOING_VALIDATION = "validateTransferProposal";
	
	private ConsultantTreeGrid tgMyDepartmentConsultants = new ConsultantTreeGrid();
	private ConsultantTreeGrid tgOtherDepartmentConsultants = new ConsultantTreeGrid();
	private TreeNode rootNodeMyDepartmentConsultants = null;
	private TreeNode rootNodeOtherDepartmentConsultants = null;
	private String myValidatingNodeId = "";
	private String otherValidatingNodeId = "";
	
	
	
	public HRUI() {
		super();
		this.setWidth(1200);  
		this.setHeight(800);  
		this.setMembersMargin(5);  
		this.setPadding(5);

		// setup the treegrid rootnodes, fields
		initMyDepartmentTreeGrid();
		initOtherDepartmentTreeGrid();
		
		// load the departments to "other departments treegrid"
		loadOtherDepartmentsByRPC();
		
		// load all consultants information
		loadConsultantTransferInfoByRPC();

		this.addMember(tgMyDepartmentConsultants);
		this.addMember(tgOtherDepartmentConsultants);
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
		TreeGridField matricule = new TreeGridField(ConstantsMercato.KEY_MATRICULE, "Matricule",100);
		matricule.setCanEdit(false);
		
		//combo box for validation
		TreeGridField validation = new TreeGridField(ConstantsMercato.KEY_COMMENT,"Avis", 150);
		validation.setValueMap(POSITIVE_COMMENT, NEGATIVE_COMMENT);
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
		
		// ignore non permitted validations
		tgMyDepartmentConsultants.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = tgMyDepartmentConsultants.getSelectedRecord();
				setMyValidatingNodeId(record.getAttribute(ConstantsMercato.KEY_ID));
				if (record.getAttribute(ConstantsMercato.KEY_COMMENT).equals("")) {
					event.cancel();
					SC.say("Pas de avis à émettre");
					return;
				}
				
				System.out.println("Trying to change " + record.getAttribute(ConstantsMercato.KEY_LABEL));
			}
			
		});
		
		// validation confirmed handler
		tgMyDepartmentConsultants.addCellSavedHandler(new CellSavedHandler() {

			public void onCellSaved(CellSavedEvent event) {
				TreeNode treeNode = tgMyDepartmentConsultants.getTree().findById(getMyValidatingNodeId());
				setMyValidatingNodeId("");
				System.out.println("validation changed!");
				if (treeNode.getAttribute(ConstantsMercato.KEY_COMMENT).equals(COMMENT_MSG)) {
					SC.say("Please choose a validation option");
					return;
				}
				System.out.println("validation changed!");
				if (treeNode != null) {
					System.out.println("To " + treeNode.getAttribute("validation") + " status.");
					Personne pers = (Personne) treeNode.getAttributeAsObject(ConstantsMercato.KEY_OBJECT);
					
					if ( pers.getTransferCourant() != null) {
						// TODO
						//validateIncomingTransferRequestByRPC(pers.getTransferCourant(), validation);
						commentIncomingTransferProposalByRPC(pers.getTransferCourant(), treeNode.getAttribute(ConstantsMercato.KEY_COMMENT));
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
		TreeGridField matricule = new TreeGridField(ConstantsMercato.KEY_MATRICULE, "Matricule",100);
		matricule.setCanEdit(false);
		TreeGridField validation = new TreeGridField(ConstantsMercato.KEY_COMMENT,"Avis", 150);
		validation.setValueMap(POSITIVE_COMMENT, NEGATIVE_COMMENT);
		validation.setCanEdit(true);
		tgOtherDepartmentConsultants.setFields(name, matricule, validation);

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
		
		
		// ignore non permitted validations
		tgOtherDepartmentConsultants.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			public void onCellDoubleClick(CellDoubleClickEvent event) {
				
				ListGridRecord record = tgOtherDepartmentConsultants.getSelectedRecord();
				setOtherValidatingNodeId(record.getAttribute(ConstantsMercato.KEY_ID));
				if (record.getAttribute(ConstantsMercato.KEY_COMMENT).equals("")) {
					event.cancel();
					SC.say("Pas de avis à émettre");
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
				if (treeNode.getAttribute(ConstantsMercato.KEY_COMMENT).equals(COMMENT_MSG)) {
					SC.say("Please choose a validation option");
					return;
				}
				if (treeNode != null) {
					Personne pers = (Personne) treeNode.getAttributeAsObject(ConstantsMercato.KEY_OBJECT);
					
				
					if ( pers.getTransferCourant() != null) {
						commentOutgoingTransferProposalByRPC(pers.getTransferCourant(), treeNode.getAttribute(ConstantsMercato.KEY_COMMENT));
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
	 * @param transfert
	 * @param comment
	 */
	private void commentOutgoingTransferProposalByRPC(Transfert transfert, String comment) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();
		
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onSuccess(Boolean result) {
					dlg.hide();
					SC.say("Avis émis");
					return;
			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		//TransferService.Util.getInstance().validateTransferRequestProcess(transfert, validation, callback);
		TransferService.Util.getInstance().signalCommentHR1(transfert, comment, callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}
	
	
	/**
	 * @param transfert
	 * @param validation
	 */
	private void commentIncomingTransferProposalByRPC(Transfert transfert, String comment) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();
		
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onSuccess(Boolean result) {
					dlg.hide();
					SC.say("Avis émis");
					return;
			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		//TransferService.Util.getInstance().validateTransferRequestProcess(transfert, validation, callback);
		TransferService.Util.getInstance().signalCommentHR2(transfert, comment, callback);
		// give user a wait message while retrieving datas
		dlg.show();
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
			
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);
			
			// Check if it is my departement
			if (infoTransfer.getPersonne().getDepartement().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {
				node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER);
				
				// if the personne has no transfer
				if (infoTransfer.getPersonne().getTransferCourant() == null) {
					TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
					node.setAttribute(ConstantsMercato.KEY_COMMENT, "");
					treeMyDepartment.add(node, parentNode);
				} else {
					
					TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepId()));
					if (infoTransfer.getTransferStatus().equals(WAIT_FOR_VALIDATION_PROPOSAL)) {
						
						if (infoTransfer.getCommentHR1() == null) {
							node.setAttribute(ConstantsMercato.KEY_COMMENT, COMMENT_MSG);
						} else {
							node.setAttribute(ConstantsMercato.KEY_COMMENT, infoTransfer.getCommentHR1());
						}
					}
					
					treeOtherDepartment.add(node, parentNode);
				}
			} else {
				node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
				if (infoTransfer.getPersonne().getTransferCourant() == null) {
					TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(infoTransfer.getPersonne().getDepartement().getDepId()));
					node.setAttribute(ConstantsMercato.KEY_COMMENT, "");
					treeOtherDepartment.add(node, parentNode);
				} else {
					if (infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {
						
						TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
						node.setAttribute(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT, infoTransfer.getPersonne().getDepartement().getDepLib());
						if (infoTransfer.getTransferStatus().equals(WAIT_FOR_VALIDATION_PROPOSAL)) {
							if (infoTransfer.getCommentHR2() == null) {
								node.setAttribute(ConstantsMercato.KEY_COMMENT, COMMENT_MSG);
							} else {
								node.setAttribute(ConstantsMercato.KEY_COMMENT, infoTransfer.getCommentHR2());
							}
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
	 * custom tree grid for both my department consultant treegrid and other department consultant treegrd
	 * @author Huage Chen
	 *
	 */
	public static class ConsultantTreeGrid extends TreeGrid {  
		public ConsultantTreeGrid() {  
			super();
			setHeight100();
			setWidth("40%");
			setCanDragRecordsOut(false);
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
