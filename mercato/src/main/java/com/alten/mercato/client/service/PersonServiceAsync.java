/**
 * 
 */
package com.alten.mercato.client.service;

import java.util.List;

import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Huage Chen
 *
 */
public interface PersonServiceAsync {

	public void getConsultantsForDD(AsyncCallback<List<Personne>> callback);
	
	public void getOtherDepartmentConsultantsForDD(AsyncCallback<List<Personne>> callback);
	
	public void getOtherDepartements(AsyncCallback<List<Departement>> callback);

}
