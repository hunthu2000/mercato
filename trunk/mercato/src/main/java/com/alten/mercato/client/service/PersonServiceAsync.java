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

	/**
	 * @param depId
	 * @param callback
	 */
	public void getConsultantsForDD(long depId,AsyncCallback<List<Personne>> callback);
	
	/**
	 * @param depId
	 * @param callback
	 */
	public void getOtherDepartements(long depId, AsyncCallback<List<Departement>> callback);
	
	/**
	 * @param depId
	 * @param callback
	 */
	public void getOtherDepartmentConsultantsForDD(long depId, AsyncCallback<List<Personne>> callback);
}
