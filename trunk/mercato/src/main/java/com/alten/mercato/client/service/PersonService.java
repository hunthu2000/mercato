/**
 * 
 */
package com.alten.mercato.client.service;

import java.util.List;

import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Huage Chen
 *
 */
@RemoteServiceRelativePath("personService.rpc")
public interface PersonService extends RemoteService {
	public static class Util {

		public static PersonServiceAsync getInstance() {

			return GWT.create(PersonService.class);
		}
	}
	

	/**
	 * @param depId
	 * @return
	 * @throws Exception
	 */
	public List<Personne> getConsultantsForDD(long depId) throws Exception;
	
	/**
	 * @param depId
	 * @return
	 * @throws Exception
	 */
	public List<Departement> getOtherDepartements(long depId) throws Exception;
	
	/**
	 * @param depId
	 * @return
	 * @throws Exception
	 */
	public List<Personne> getOtherDepartmentConsultantsForDD(long depId) throws Exception;
}
