/**
 * 
 */
package com.alten.mercato.client.data.ds;

import java.util.Date;

import com.smartgwt.client.data.DataSource;

/**
 * @author Huage Chen
 *
 */
public class PersonDataSource extends DataSource {

	//public static final String KEY_ID =
		//public static final String KEY_ID =
	
	public PersonDataSource(String id) {
		// generate a id time based
		id = id+"_"+new Date().getTime();
		setID(id);
		
		
		setClientOnly(true);
	}

}
