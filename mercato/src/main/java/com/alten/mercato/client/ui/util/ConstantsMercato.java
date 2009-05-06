/**
 * 
 */
package com.alten.mercato.client.ui.util;

import com.alten.mercato.server.model.Personne;



/**
 * client side user interface constants
 * @author Huage Chen
 */
public class ConstantsMercato {

	
	private static Personne currentUser = null;
	
	// key for recordDef and store
	public static final String KEY_AUTHS = "auths";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_BENEFICIARY = "beneficiary";
	public static final String KEY_CODE = "code";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_CREATOR = "creator";
	public static final String KEY_DATE = "date";
	public static final String KEY_DEPARTMENT = "department";
	public static final String KEY_ORIGINAL_DEPARTMENT = "originalDepartment";
	public static final String KEY_ICON = "icon";
	public static final String KEY_ID = "id";
	public static final String KEY_LABEL = "label";
	public static final String KEY_MATRICULE = "matricule";
	public static final String KEY_NAME = "name";
	public static final String KEY_OBJECT = "object";
	public static final String KEY_PATH = "path";
	public static final String KEY_SCREEN = "screen";
	public static final String KEY_SELECTED = "selected";
	public static final String KEY_SIZE = "size";
	public static final String KEY_STATUS = "status";
	public static final String KEY_TITLE = "title";
	public static final String KEY_TYPE = "type";
	public static final String KEY_UNIQUE = "unique";
	public static final String KEY_PARENT = "ReportsTo";
	public static final String KEY_VALIDATION = "validation";
	public static final String KEY_MY_HR_COMMENT = "comment1";
	public static final String KEY_OTHER_HR_COMMENT = "comment2";

	// PATH for icons
	public static final String ICON_ADD = "[SKIN]/actions/add.png";
	public static final String ICON_APPROVE = "[SKIN]/actions/approve.png";
	public static final String ICON_CANCEL = "[SKIN]/actions/cancel.png";
	public static final String ICON_EDIT = "[SKIN]/actions/edit.png";
	public static final String ICON_FOLDER_OPEN = "[SKIN]/TreeGrid/folder_open.png";
	public static final String ICON_LOADING = "[SKIN]/loading.gif";
	public static final String ICON_OK = "[SKIN]/actions/ok.png";
	public static final String ICON_REMOVE = "[SKIN]/actions/remove.png";
	public static final String ICON_SAVE = "[SKIN]/actions/save.png";
	public static final String ICON_SEARCH = "[SKIN]/actions/search.png";
	public static final String ICON_VIEW = "[SKIN]/actions/view.png";
	public static final String ICON_UNDO = "icons/16/undo.png";
	public static final String ICON_DPMT = "silk/chart_organisation.png";
	public static final String ICON_USER = "icons/16/person.png";
	public static final String ICON_USER_BLUE = "icons/16/person_blue.png";
	public static final String ICON_LEFT = "icons/32/arrow_left.png";
	public static final String ICON_RIGHT = "icons/32/arrow_right.png";
	
	public static Personne getCurrentUser() {
		return ConstantsMercato.currentUser;
	}
	
	public static void initCurrentUser(Personne personne) {
		ConstantsMercato.currentUser = personne;
	}

}
