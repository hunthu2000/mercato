package com.alten.mercato.server.manager.interf;

import java.util.List;

import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.model.Departement;
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
	public List<Personne> getConsultantsByDepartmentId(long departmentId);

	/**
	 * @param depId
	 * @return
	 */
	public List<Personne> getOtherDepartmentsConsultants(long depId);
	
	/**
	 * @param depId
	 * @return
	 */
	public List<Departement> getOtherDepartments(long depId);
	
	/**
	 * @return
	 */
	public List<InfoTransfer> getConsultantsWithTransferInfo();
}
