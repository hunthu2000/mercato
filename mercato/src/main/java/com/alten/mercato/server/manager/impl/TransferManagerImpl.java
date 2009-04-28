/**
 * 
 */
package com.alten.mercato.server.manager.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.Configuration;
import org.jbpm.Execution;
import org.jbpm.ExecutionService;
import org.jbpm.HistoryService;
import org.jbpm.JbpmException;
import org.jbpm.ProcessEngine;
import org.jbpm.TaskService;
import org.jbpm.history.HistoryProcessInstance;
import org.jbpm.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.server.dao.interf.DepartementDao;
import com.alten.mercato.server.dao.interf.PersonneDao;
import com.alten.mercato.server.dao.interf.TransfertDao;
import com.alten.mercato.server.exception.MercatoWorkflowException;
import com.alten.mercato.server.manager.interf.TransferManager;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;

/**
 * This class manages the execution and the information of the transfer process
 * @author Huage Chen
 *
 */

@Service("transferManager")
public class TransferManagerImpl implements TransferManager {
	public static final String KEY_TRANSFER_REQUEST_PROCESS_DEFINITION = "TransferRequest";
	public static final String KEY_TRANSFER_PROPOSAL_PROCESS_DEFINITION = "TransferProposal";
	public static final String KEY_PROCESS_CONTEXT_TRANSFERT_ID = "TransfertId";
	public static final String VALIDATE_TRANSFER_TRANSITION = "validateTransfer";
	public static final String CANCEL_TRANSFER_TRANSITION = "cancelTransfer";
	public static final String KEY_CONTEXT_DD1 = "dd1";
	public static final String KEY_CONTEXT_DD2 = "dd2";
	public static final String KEY_ENDED_STATUS = "ended";
	public static final String KEY_CANCELLED_STATUS = "cancel";

	public static final String JBPM_CONFIG_FILE = "jbpm.cfg.xml"; 

	@Autowired
	@Qualifier("transfertDao")
	TransfertDao transfertDao = null;

	@Autowired
	@Qualifier("personneDao")
	PersonneDao personneDao = null;

	@Autowired
	@Qualifier("departementDao")
	DepartementDao departementDao = null;
	
	private ProcessEngine processEngine;

	Logger logger = LoggerFactory.getLogger(TransferManagerImpl.class);

	public TransferManagerImpl() {
		processEngine = new Configuration().setResource(JBPM_CONFIG_FILE).buildProcessEngine();
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#startAndAskTransferProcess(long, long, java.lang.String)
	 */
	public Personne startAndAskTransferRequestProcess(long transDepEntrId, long transDepConsulId, String assignee) throws MercatoWorkflowException {
		try {
			
			
			
			ExecutionService executionService = processEngine.getExecutionService();

			logger.info("retrieving consultant object");

			// retrieve the consultant to be transfered
			Personne consultant = personneDao.findById(transDepConsulId);

			logger.info("retrieving department object");
			// retrieve the new department that the consultant is to be entered
			Departement departement = departementDao.findById(transDepEntrId);

			logger.info("verifying if there are already transfers assigned to the consultant");
			// if one of the two objects is null, the transfer can not be started
			if ((consultant == null) || (departement == null)) {
				throw new MercatoWorkflowException("Constant id or department id is not correct");
			}

			if (consultant.getTransferCourant() != null) {
				logger.info("transfer in process");
				throw new MercatoWorkflowException("Transfer for this consultant is still in progress");
			}
			//find the existing transfer assigned to the consultant, the list should either be empty 
			//or contain only past transfers to allow the execution to be started

			logger.info("creating the transfer object");

			//create the new transfert object save it to let the database generate automatically the transfert id
			Transfert transfert = new Transfert(consultant, departement);
			transfertDao.attachDirty(transfert);

			long transfertId = transfert.getTransId();

			Map<String, Object> processContextVariables = new HashMap<String, Object>();

			// put the transfertId to the process contextVariable map
			processContextVariables.put(KEY_PROCESS_CONTEXT_TRANSFERT_ID, new Long(transfertId));

			// assign dynamically the actors of the workflow
			String loginDD1;
			String loginDD2;
			Iterator<com.alten.mercato.server.model.Util> it = transfert.getDepEntr().getPersonne().getUtils().iterator();
			if (it.hasNext()) {
				loginDD1 = it.next().getUtilLogin();
			} else {
				throw new MercatoWorkflowException("no login found for the department director 1");
			}

			it = transfert.getConsultant().getDepartement().getPersonne().getUtils().iterator();

			if (it.hasNext()) {
				loginDD2 = it.next().getUtilLogin();
			} else {
				throw new MercatoWorkflowException("no login found for the department director 2");
			}


			processContextVariables.put(KEY_CONTEXT_DD1, loginDD1);
			processContextVariables.put(KEY_CONTEXT_DD2, loginDD2);

			logger.info("Starting the process instance");
			Execution execution = executionService.startProcessInstanceByKey(KEY_TRANSFER_REQUEST_PROCESS_DEFINITION, processContextVariables);
			logger.info("process started");

			transfert.setTransExecId(execution.getId());

			// update the transfert with the generated execution id
			transfertDao.attachDirty(transfert);

			// update the consultant(PERSONNE TABLE) with the current transfer
			consultant.setTransferCourant(transfert);
			personneDao.attachDirty(consultant);

			// complete the ask consultant task
			signalAskConsultant(transfert, assignee);
			return consultant;
		}
		catch (JbpmException e ){
			e.printStackTrace();
			logger.error(e.toString());
			return null;
		}
	}

	public void signalAskConsultant(Transfert transfert, String assignee) {
		try {
			
			TaskService taskService = processEngine.getTaskService();
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), KEY_PROCESS_CONTEXT_TRANSFERT_ID);
					if (transferId != null) {
						if (transferId.longValue() == transfert.getTransId() ) {
							logger.info("task found");
							taskToComplete = task;
							break;
						}
					}
				}
			}

			if (taskToComplete == null) {
				logger.info("no task found for " + assignee + " for the current transfer " + transfert.getTransId());
				return;
			}
			logger.info("completing task");
			taskService.completeTask(taskToComplete.getDbid());
			return;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}

	}

	

	public Personne signalValidateTransferRequest(Transfert transfert,
			String assignee, String validation) throws MercatoWorkflowException{
		try {
			TaskService taskService = processEngine.getTaskService();
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), KEY_PROCESS_CONTEXT_TRANSFERT_ID);
					if (transferId != null) {
						if (transferId.longValue() == transfert.getTransId() ) {
							logger.info("task found");
							taskToComplete = task;
							break;
						}
					}
				}
			}

			if (taskToComplete == null) {
				logger.info("no task found for " + assignee + " for the current transfer " + transfert.getTransId());
				throw new MercatoWorkflowException("no task found for this person");
			}
			logger.info("completing task...");
			taskService.completeTask(taskToComplete.getDbid(), validation);
			HistoryService historyService = processEngine.getHistoryService();

			HistoryProcessInstance historyProcessInstance = historyService.createHistoryProcessInstanceQuery().processInstanceId(transfert.getTransExecId()).executeUniqueResult();
			Personne personne = transfert.getConsultant();
			// if the execution is ended normally, the transfer is validated
			if (historyProcessInstance.getState().equals(KEY_ENDED_STATUS)) {
				logger.info("transfer completed");

				//transfer the consultant


				personne.setDepartement(transfert.getDepEntr());
				//make the consultant available to transfer
				personne.setTransferCourant(null);

				personneDao.attachDirty(personne);

			}
			// the transfer is cancelled
			else {
				logger.info("transfer cancelled");
				personne.setTransferCourant(null);
				personneDao.attachDirty(personne);
			}
			return personne;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return null;
		}

	}
	
	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalValidateTransferProposal(com.alten.mercato.server.model.Transfert, java.lang.String, java.lang.String)
	 */
	public Personne signalValidateTransferProposal(Transfert transfert,
			String assignee, String validation) throws MercatoWorkflowException {
		try {
			TaskService taskService = processEngine.getTaskService();
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), KEY_PROCESS_CONTEXT_TRANSFERT_ID);
					if (transferId != null) {
						if (transferId.longValue() == transfert.getTransId() ) {
							logger.info("task found");
							taskToComplete = task;
							break;
						}
					}
				}
			}

			if (taskToComplete == null) {
				logger.info("no task found for " + assignee + " for the current transfer " + transfert.getTransId());
				throw new MercatoWorkflowException("no task found for this person");
			}
			logger.info("completing task...");
			taskService.completeTask(taskToComplete.getDbid(), validation);
			HistoryService historyService = processEngine.getHistoryService();

			HistoryProcessInstance historyProcessInstance = historyService.createHistoryProcessInstanceQuery().processInstanceId(transfert.getTransExecId()).executeUniqueResult();
			Personne personne = transfert.getConsultant();
			// if the execution is ended normally, the transfer is validated
			if (historyProcessInstance.getState().equals(KEY_ENDED_STATUS)) {
				logger.info("transfer completed");

				//transfer the consultant


				personne.setDepartement(transfert.getDepEntr());
				//make the consultant available to transfer
				personne.setTransferCourant(null);

				personneDao.attachDirty(personne);

			}
			// the transfer is cancelled
			else {
				logger.info("transfer cancelled");
				personne.setTransferCourant(null);
				personneDao.attachDirty(personne);
			}
			return personne;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return null;
		}
	}

	public Personne startAndAskTransferProposalProcess(long transDepEntrId,
			long transDepConsulId, String assignee)
			throws MercatoWorkflowException {
		try {
			ExecutionService executionService = processEngine.getExecutionService();

			logger.info("retrieving consultant object");

			// retrieve the consultant to be transfered
			Personne consultant = personneDao.findById(transDepConsulId);

			logger.info("retrieving department object");
			// retrieve the new department that the consultant is to be entered
			Departement departement = departementDao.findById(transDepEntrId);

			logger.info("verifying if there are already transfers assigned to the consultant");
			// if one of the two objects is null, the transfer can not be started
			if ((consultant == null) || (departement == null)) {
				throw new MercatoWorkflowException("Constant id or department id is not correct");
			}

			if (consultant.getTransferCourant() != null) {
				logger.info("transfer in process");
				throw new MercatoWorkflowException("Transfer for this consultant is still in progress");
			}
			//find the existing transfer assigned to the consultant, the list should either be empty 
			//or contain only past transfers to allow the execution to be started

			logger.info("creating the transfer object");

			//create the new transfert object save it to let the database generate automatically the transfert id
			Transfert transfert = new Transfert(consultant, departement);
			transfertDao.attachDirty(transfert);

			long transfertId = transfert.getTransId();

			Map<String, Object> processContextVariables = new HashMap<String, Object>();

			// put the transfertId to the process contextVariable map
			processContextVariables.put(KEY_PROCESS_CONTEXT_TRANSFERT_ID, new Long(transfertId));

			// assign dynamically the actors of the workflow
			String loginDD1;
			String loginDD2;
			Iterator<com.alten.mercato.server.model.Util> it = transfert.getDepEntr().getPersonne().getUtils().iterator();
			if (it.hasNext()) {
				loginDD2 = it.next().getUtilLogin();
			} else {
				throw new MercatoWorkflowException("no login found for the department director 2");
			}

			it = transfert.getConsultant().getDepartement().getPersonne().getUtils().iterator();

			if (it.hasNext()) {
				loginDD1 = it.next().getUtilLogin();
			} else {
				throw new MercatoWorkflowException("no login found for the department director 1");
			}


			processContextVariables.put(KEY_CONTEXT_DD1, loginDD1);
			processContextVariables.put(KEY_CONTEXT_DD2, loginDD2);

			logger.info("Starting the process instance");
			Execution execution = executionService.startProcessInstanceByKey(KEY_TRANSFER_PROPOSAL_PROCESS_DEFINITION, processContextVariables);
			logger.info("process started");

			transfert.setTransExecId(execution.getId());

			// update the transfert with the generated execution id
			transfertDao.attachDirty(transfert);

			// update the consultant(PERSONNE TABLE) with the current transfer
			consultant.setTransferCourant(transfert);
			personneDao.attachDirty(consultant);

			// complete the ask consultant task
			signalProposeConsultant(transfert, assignee);
			return consultant;
		}
		catch (JbpmException e ){
			e.printStackTrace();
			logger.error(e.toString());
			return null;
		}
	}
	
	public void signalProposeConsultant(Transfert transfert, String assignee) {
		try {
			TaskService taskService = processEngine.getTaskService();
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), KEY_PROCESS_CONTEXT_TRANSFERT_ID);

					if (transferId != null) {
						if (transferId.longValue() == transfert.getTransId() ) {
							logger.info("task found");
							taskToComplete = task;
							break;
						}
					}
				}
			}

			if (taskToComplete == null) {
				logger.info("no task found for " + assignee + " for the current transfer " + transfert.getTransId());
				return;
			}
			logger.info("completing task");
			taskService.completeTask(taskToComplete.getDbid());
			return;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}
		
	}
}
