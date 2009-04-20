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
	
	
	List<Personne> getConsultantsForDD() throws Exception;
	
	List<Personne> getOtherDepartmentConsultantsForDD() throws Exception;
	
	List<Departement> getOtherDepartements() throws Exception;
}
