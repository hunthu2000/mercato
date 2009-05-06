/**
 * 
 */
package com.alten.mercato.server.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.Configuration;
import org.jbpm.Execution;
import org.jbpm.ExecutionService;
import org.jbpm.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.dao.interf.DepartementDao;
import com.alten.mercato.server.dao.interf.PersonneDao;
import com.alten.mercato.server.manager.interf.PersonManager;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
@Service("personManager")
public class PersonManagerImpl implements PersonManager {
	
	public static final String JBPM_CONFIG_FILE = "jbpm.cfg.xml"; 

	private ProcessEngine processEngine;
	private ExecutionService executionService;
	public PersonManagerImpl() {
		processEngine = new Configuration().setResource(JBPM_CONFIG_FILE).buildProcessEngine();
		executionService = processEngine.getExecutionService();
	}
	
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


	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.PersonManager#getOtherDepartmentsConsultants(long)
	 */
	public List<Personne> getOtherDepartmentsConsultants(long depId) {
		List<Personne> res = personneDao.findConsultantsOfOtherDepartmentByDepartmentId(depId);
		return res;
	}
	
	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.PersonManager#getOtherDepartments(long)
	 */
	public List<Departement> getOtherDepartments(long depId) {
		List<Departement> res = departementDao.findOtherDepartmentsByDepartmentID(depId);
		return res;
	}
	
	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#getConsultantsWithTransferInfo()
	 */
	public List<InfoTransfer> getConsultantsWithTransferInfo()   {
		List<InfoTransfer> res = new ArrayList<InfoTransfer>();

		logger.info("getting available consultants");
		List<Personne> lstAvailableConsultants = personneDao.findAllAvailableConsultant();
		for (Personne personne:lstAvailableConsultants) {
			res.add(new InfoTransfer(personne, ""));
		}

		logger.info("getting consultants who have a current transfer");
		List<Personne> lstInTransferConsultants = personneDao.findAllConsultantInTransfer();
		if (lstInTransferConsultants != null) {
			if (lstInTransferConsultants.size() > 0 ) {
				Execution execution;
				for (Personne personne:lstInTransferConsultants) {
					
					//find the execution status for those who have a transfer in process
					
					execution = executionService.findExecution(personne.getTransferCourant().getTransExecId());
					if (execution != null) {
						// find the execution status, the comments from HR which are stored in the process variables
						res.add(new InfoTransfer(personne, execution.getActivityName(),
										(String) executionService.getVariable(execution.getId(), TransferManagerImpl.CONTEXT_COMMENT_HR1),
										(String) executionService.getVariable(execution.getId(), TransferManagerImpl.CONTEXT_COMMENT_HR2)));
					} else {
						res.add(new InfoTransfer(personne,"","",""));
					}
				}
			}
		}
		logger.info("returning the prepared list to the controller");
		return res;
	}

}
