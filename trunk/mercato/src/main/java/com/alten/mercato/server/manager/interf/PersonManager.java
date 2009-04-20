package com.alten.mercato.server.manager.interf;

import java.util.List;

import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
public interface PersonManager {
	
	/**
	 * @param departmentId
	 * @return
	 */
	List<Personne> getConsultantsByDepartmentId(long departmentId);
	
	List<Personne> getConsultantsForDD(String login);

}
