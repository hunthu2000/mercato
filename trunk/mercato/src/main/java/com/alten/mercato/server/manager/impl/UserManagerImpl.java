package com.alten.mercato.server.manager.impl;

import java.util.List;

import org.jbpm.ExecutionService;
import org.jbpm.JbpmException;
import org.jbpm.RepositoryService;
import org.jbpm.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.server.dao.interf.UtilDao;
import com.alten.mercato.server.manager.interf.UserManager;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Util;

/**
 * @author Huage Chen
 *
 */
@Service("userManager")
public class UserManagerImpl implements UserManager {
	
	@Autowired
	@Qualifier("utilDao")
	private UtilDao utilDao = null;
	
	@Autowired
	@Qualifier("repositoryService")
	RepositoryService repositoryService = null;
	
	
	@Autowired
	@Qualifier("executionService")
	ExecutionService executionService = null;
	
	@Autowired
	@Qualifier("taskService")
	TaskService taskService = null;
	
	Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);
	

	public String getUserName(String login) {
		
			logger.info("Retrieving user information");
			List<Util> lstUtil = utilDao.getUserByLogin(login);
		
			if (lstUtil!=null) {
				if (lstUtil.size()>0) {
				
					Personne user = lstUtil.get(0).getPersonne();
					return user.getPerNom() + " " + user.getPerPrenom(); 
				}
			}
			return "";
	}


	public Personne getUser(String login) {
		logger.info("Retrieving user information by login " + login);
		List<Util> lstUtil = utilDao.getUserByLogin(login);
		
		if (lstUtil!=null) {
			if (lstUtil.size()>0) {
			
				Personne user = lstUtil.get(0).getPersonne();
				logger.info("returning user information by login " + login);
				return user; 
			}
		}
		return null;
	}
	
	

}
