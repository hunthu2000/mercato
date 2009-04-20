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
	
	private static final String CODE_DEPARTMENT_DIRECTOR = "DD";
	@Autowired
	@Qualifier("personneDao")
	private PersonneDao personneDao = null;
	
	@Autowired
	@Qualifier("utilDao")
	private UtilDao utilDao = null;
	
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

	public List<Personne> getConsultantsForDD(String login) {
		
		List<Personne> res = null;
		// retrieving user information 
		logger.info("Retrieving user information");
		List<Util> lstUtil = utilDao.getUserByLogin(login);
		
		if (lstUtil!=null) {
			if (lstUtil.size()>0) {
				Personne user = lstUtil.get(0).getPersonne();
				
				if (user.getTypePersonne().getTpersCode().equals(CODE_DEPARTMENT_DIRECTOR)) {
					res = personneDao.findConsultantsByDepartmentId(user.getDepartement().getDepId());
				}
				
			}
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.PersonManager#getOtherDepartmentsConsultants(java.lang.String)
	 */
	public List<Personne> getOtherDepartmentsConsultants(String login) {
		List<Personne> res = null;
		logger.info("Retrieving user information");
		List<Util> lstUtil = utilDao.getUserByLogin(login);
		
		if (lstUtil != null) {
			if (lstUtil.size()>0) {
				Personne user = lstUtil.get(0).getPersonne();
				if (user.getTypePersonne().getTpersCode().equals(CODE_DEPARTMENT_DIRECTOR)) {
					res = personneDao.findConsultantsOfOtherDepartmentByDepartmentId(user.getDepartement().getDepId());
				}
			}
		}
		return res;
	}

	public List<Departement> getOtherDepartments(String login) {
		// TODO Auto-generated method stub
		List<Departement> res = null;
		logger.info("Retrieving user information");
		List<Util> lstUtil = utilDao.getUserByLogin(login);
		
		if (lstUtil != null) {
			if (lstUtil.size()>0) {
				Personne user = lstUtil.get(0).getPersonne();
				if (user.getTypePersonne().getTpersCode().equals(CODE_DEPARTMENT_DIRECTOR)) {
					res = departementDao.findOtherDepartmentsByDepartmentID(user.getDepartement().getDepId());
				}
			}
		}
		return res;
	}

}
