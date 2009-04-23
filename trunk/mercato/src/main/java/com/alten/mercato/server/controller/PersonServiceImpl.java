/**
 * 
 */
package com.alten.mercato.server.controller;

import java.util.List;

import org.gwtwidgets.server.spring.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.server.manager.interf.PersonManager;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
@Service("personController")
public class PersonServiceImpl implements PersonService {

	// log4j
	Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);


	@Autowired
	@Qualifier("personManager")
	private PersonManager personManager = null;


	
	public List<Personne> getConsultantsForDD(long depId) throws Exception {
		try {

			logger.info("getting consultants in the department " + depId);
			List<Personne> res = personManager.getConsultantsByDepartmentId(depId);

			logger.info("lazy loading in the list of persons...");

			// lazy loading
			for (Personne person: res) {
				person.getDepartement().getDepLib();
				person.getTypePersonne().getTpersLib();
			}

			logger.info("lazy loading finished, returning the result to server");

			return res;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

	}
	
	
	/**
	 * @param depId
	 * @return
	 * @throws Exception
	 */
	public List<Departement> getOtherDepartements(long depId) throws Exception {
		try {

			logger.info("getting consultants in the department " + depId);
			List<Departement> res = personManager.getOtherDepartments(depId);
			
			
			logger.info("lazy loading in the department list");
			
			for (Departement dpt:res) {
				dpt.getDepLib();
			}

			logger.info("returning the result to server");

			return res;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.alten.mercato.client.service.PersonService#getOtherDepartmentConsultantsForDD(long)
	 */
	public List<Personne> getOtherDepartmentConsultantsForDD(long depId) throws Exception {
		try {

			logger.info("getting consultants in the department " + depId);
			List<Personne> res = personManager.getOtherDepartmentsConsultants(depId);

			logger.info("lazy loading...");

			// lazy loading
			for (Personne person: res) {
				person.getDepartement().getDepLib();
				person.getTypePersonne().getTpersLib();
			}

			logger.info("lazy loading finished, returning the result to server");

			return res;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	

}
