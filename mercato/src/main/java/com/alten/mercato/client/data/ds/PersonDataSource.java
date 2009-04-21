/**
 * 
 */
package com.alten.mercato.client.data.ds;

import java.util.Date;

import com.alten.mercato.client.ui.util.ConstantsMercato;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceBinaryField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * 
 * DataSource for the person model to fill the treegrids
 * @author Huage Chen
 *
 */
public class PersonDataSource extends DataSource {

	public static final String KEY_ID = ConstantsMercato.KEY_ID;
	public static final String KEY_LABEL = ConstantsMercato.KEY_LABEL;
	public static final String KEY_PARENT = ConstantsMercato.KEY_PARENT;
	public static final String KEY_ICON = ConstantsMercato.KEY_ICON;
	public static final String KEY_OBJECT = ConstantsMercato.KEY_OBJECT;
	
	public PersonDataSource(String id) {
		// generate a id time based
		id = id+"_"+new Date().getTime();
		setID(id);
		DataSourceTextField personId = new DataSourceTextField(KEY_ID,"id", 25);
		personId.setPrimaryKey(true);
		personId.setRequired(true);
		personId.setHidden(true);

		DataSourceTextField label = new DataSourceTextField(KEY_LABEL, "Consultant", 120);
		
		/*DataSourceTextField parent = new DataSourceTextField(KEY_PARENT);
		parent.setHidden(true);
		parent.setRequired(true);
		parent.setForeignKey(id+"."+KEY_ID);
		parent.setRootValue("1");  */
		
		DataSourceTextField icon = new DataSourceTextField(KEY_ICON);
		
		DataSourceBinaryField obj = new DataSourceBinaryField(KEY_OBJECT, KEY_OBJECT);
		setClientOnly(true);
		
		setFields(personId, label, icon, obj);
	}

}
