/**
 * 
 */
package com.alten.mercato.server.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.server.dao.interf.DepartementDao;
import com.alten.mercato.server.dao.interf.PersonneDao;
import com.alten.mercato.server.dao.interf.UtilDao;
import com.alten.mercato.server.manager.interf.PersonManager;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Util;

/**
 * @author Huage Chen
 *
 */
@Service("personManager")
public class PersonManagerImpl implements PersonManager {

	// log4j
	Logger logger = LoggerFactory.getLogger(PersonManagerImpl.class);
	
	@Autowired
	@Qualifier("personneDao")
	private PersonneDao personneDao = null;
	
	
	@Autowired
	@Qualifier("departementDao")
	private DepartementDao departementDao = null;
	
	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.PersonManager#getConsultantsByDepartmentId(long)
	 */
	public List<Personne> getConsultantsByDepartmentId(long departmentId) {
		List<Personne> res = personneDao.findConsultantsByDepartmentId(departmentId);
		return res;
	}


	public List<Personne> getOtherDepartmentsConsultants(long depId) {
		List<Personne> res = personneDao.findConsultantsOfOtherDepartmentByDepartmentId(depId);
		return res;
	}
	
	public List<Departement> getOtherDepartments(long depId) {
		List<Departement> res = departementDao.findOtherDepartmentsByDepartmentID(depId);
		return res;
	}

}
