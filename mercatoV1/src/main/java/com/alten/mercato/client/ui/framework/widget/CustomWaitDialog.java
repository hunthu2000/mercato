package com.alten.mercato.client.ui.framework.widget;

import com.alten.mercato.client.ui.util.ConstantsMercato;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;

/**
 * @author Huage Chen Waiting Dialog
 */
public class CustomWaitDialog extends Window {

	
	private static CustomWaitDialog instance = null;
	//TODO move this hard coded constant to a file
	private Label lab = new Label("Please wait...");
	private int cptLoadingTask = 0;

	/**
	 * singleton mechanism
	 * @return
	 */
	public static CustomWaitDialog getInstance() {
		if (instance==null) {
			instance = new CustomWaitDialog();
		}
		return instance;
	}

	private CustomWaitDialog() {
		super();
		customize();
	}

	/**
	 * initialisation
	 */
	private void customize() {
		this.setShowModalMask(true);
		this.setWidth("25%");
		this.setHeight("15%");
		this.centerInPage();
		this.setShowTitle(false);
		this.setShowCloseButton(false);
		this.setShowMinimizeButton(false);
		this.setShowCustomScrollbars(false);
		this.setShowHeaderIcon(false);
		lab.setHeight100();
		lab.setWidth100();
		lab.setAlign(Alignment.CENTER);
		lab.setIcon(ConstantsMercato.ICON_LOADING);
		this.addMember(lab);
		this.setIsModal(true);
	}

	/**
	 * some accessor
	 * @param message
	 */
	public CustomWaitDialog setMessage(String message) {
		lab.setContents(message);
		return this;
	}

	/**
	 * some accessor
	 * @param icon
	 */
	public CustomWaitDialog setIcon(String icon) {
		lab.setIcon(icon);
		return this;
	}

	/**
	 * some accessor
	 * @return
	 */
	public Label getLabel() {
		return lab;
	}

	/**
	 * manager used to display the loading dialog
	 */
	public void addLoadingTask() {
		cptLoadingTask++;
		show();
	}

	/**
	 * manager used to hide loading dialog
	 */
	public void removeLoadingTask() {
		cptLoadingTask--;
		if (cptLoadingTask<=0) {
			hide();
		}
	}

	/**
	 * Force the dlg to hide and reset the loading cpt
	 */
	public void forceHide() {
		cptLoadingTask = 0;
		hide();
	}

	/**
	 * accessor
	 * @return
	 */
	public int getCptLoadingTask() {
		return cptLoadingTask;
	}

	/**
	 * accessor
	 * @param cptLoadingTask
	 */
	public void setCptLoadingTask(int cptLoadingTask) {
		this.cptLoadingTask = cptLoadingTask;
	}

}
