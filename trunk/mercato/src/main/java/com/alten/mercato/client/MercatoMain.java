/**
 * 
 */
package com.alten.mercato.client;

import java.util.List;

import com.alten.mercato.client.service.DemoService;
import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;


/**
 * @author Huage Chen
 *
 */
public class MercatoMain implements EntryPoint {

	public void onModuleLoad() {
		// loadingMsg
		
		
		
		ButtonItem logout = new ButtonItem("Logout");
		logout.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					public void onFailure(Throwable t) {
						// display error text if we can't get the quote:
						SC.say("Failed to logout");
					}

					public void onSuccess(Boolean arg0) {
		
						
					}
				};
				DemoService.Util.getInstance().logout(callback);
			}
			
		});


		TreeGrid treeGrid = new TreeGrid();
		treeGrid.setWidth(300);
		treeGrid.setHeight(400);

		TreeGridField field = new TreeGridField("Name", "My department");
		field.setCanSort(false);

		treeGrid.setFields(field);

		final Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		tree.setNameProperty("Name");
		tree.setIdField("EmployeeId");
		tree.setParentIdField("ReportsTo");
		tree.setShowRoot(true);

		EmployeeTreeNode root = new EmployeeTreeNode("4", "1", "Charles Madigen");
		EmployeeTreeNode node2 = new EmployeeTreeNode("188", "4", "Rogine Leger");
		EmployeeTreeNode node3 = new EmployeeTreeNode("189", "4", "Gene Porter");
		EmployeeTreeNode node4 = new EmployeeTreeNode("265", "189", "Olivier Doucet");
		EmployeeTreeNode node5 = new EmployeeTreeNode("264", "189", "Cheryl Pearson");
		
		tree.setData(new TreeNode[]{root, node2, node3, node4, node5});

		treeGrid.setNodeIcon("icons/16/person.png");
		treeGrid.setFolderIcon("icons/16/person.png");

		treeGrid.setData(tree);
		
		Canvas canvas = new Canvas();
		canvas.addChild(treeGrid);
		
		
		//test open the saved devis pdf
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
		
		loadListConsultants();
		
		RootPanel.get().add(quoteText);
		RootPanel.get().add(canvas); 
		RootPanel.get().add(canvasLogout);
		RootPanel.get("loadingWrapper").getElement().setInnerHTML("");
	}
	
	private void loadListConsultants() {

		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();

		AsyncCallback<List<Personne>> callback = new AsyncCallback<List<Personne>>() {

			public void onSuccess(List<Personne> result) {
				if (result==null||result.size()==0) {
					dlg.hide();
					return;
				}
				SC.say(result.size() + " consultants for this dd found");
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

	public static class EmployeeTreeNode extends TreeNode {

		public EmployeeTreeNode(String employeeId, String reportsTo, String name) {
			setEmployeeId(employeeId);
			setReportsTo(reportsTo);
			setName(name);
		}

		public void setEmployeeId(String value) {
			setAttribute("EmployeeId", value);
		}

		public void setReportsTo(String value) {
			setAttribute("ReportsTo", value);
		}

		public void setName(String name) {
			setAttribute("Name", name);
		}
	}

}
