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

	public List<Personne> getConsultantsForDD() throws Exception {
		try {

			String login = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("getting consultants in the department of the director " + login);
			List<Personne> res = personManager.getConsultantsForDD(login);

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

	public List<Departement> getOtherDepartements() throws Exception {
		try {

			String login = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("getting consultants in the department of the director " + login);
			List<Departement> res = personManager.getOtherDepartments(login);


			logger.info("returning the result to server");

			return res;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public List<Personne> getOtherDepartmentConsultantsForDD() throws Exception {
		try {

			String login = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("getting consultants in the department of the director " + login);
			List<Personne> res = personManager.getOtherDepartmentsConsultants(login);

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
