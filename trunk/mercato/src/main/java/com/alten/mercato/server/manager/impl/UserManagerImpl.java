package com.alten.mercato.server.manager.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.jbpm.Deployment;
import org.jbpm.Execution;
import org.jbpm.ExecutionService;
import org.jbpm.JbpmException;
import org.jbpm.ProcessService;
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
	@Qualifier("UtilMercatoDao")
	private UtilDao utilDao = null;
	
	@Autowired
	@Qualifier("processService")
	ProcessService processService = null;
	
	
	@Autowired
	@Qualifier("executionService")
	ExecutionService executionService = null;
	
	Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);
	

	public String getUserName(String login) {
		
		try {
			List<Util> lstUtil = utilDao.getUserByLogin(login);
		
			
			logger.info("Deploying the sample process");
			Deployment deployment = processService.createDeployment();
			//deployment.addResource("process.jpdl.xml");
			File file = new File("/Users/huagechen/Documents/workspace/mercato/src/main/resources/com/alten/mercato/jpdl/process.jpdl.xml");
			deployment.addFile(file);
			logger.debug(file.getPath());
			deployment.setFileType("*.jpdl.xml", "jpdl");
			deployment.deploy();
			
			logger.info("Starting process...");
			Execution execution = executionService.startProcessInstanceByKey("StateChoice");
			if (lstUtil!=null) {
				if (lstUtil.size()>0) {
				
					Personne user = lstUtil.get(0).getPersonne();
					return user.getPerNom() + " " + user.getPerPrenom(); 
				}
			}
			return "";
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return null;
		} /*catch (IOException e) {
			logger.error(e.toString());
			return null;
		}*/
		
	}

}
