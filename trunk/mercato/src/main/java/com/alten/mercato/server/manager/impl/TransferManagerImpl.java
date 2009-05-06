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
import com.alten.mercato.server.dao.interf.UtilDao;
import com.alten.mercato.server.exception.MercatoWorkflowException;
import com.alten.mercato.server.manager.interf.TransferManager;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;
import com.alten.mercato.server.model.Util;

/**
 * This class manages the execution and the information of the transfer process
 * @author Huage Chen
 *
 */

@Service("transferManager")
public class TransferManagerImpl implements TransferManager {
	public static final String KEY_TRANSFER_REQUEST_PROCESS_DEFINITION = "TransferRequest";
	public static final String KEY_TRANSFER_PROPOSAL_PROCESS_DEFINITION = "TransferProposal";
	public static final String KEY_TRANSFER_PROPOSAL_PROCESS_V2_DEFINITION = "TransferProposalV2";
	
	public static final String CONTEXT_TRANSFERT_ID = "TransfertId";
	public static final String CONTEXT_DD1 = "dd1";
	public static final String CONTEXT_DD2 = "dd2";
	public static final String CONTEXT_COMMENT_HR1 = "comment1";
	public static final String CONTEXT_COMMENT_HR2 = "comment2";
	
	public static final String STATUS_ENDED = "ended";
	public static final String STATUS_CANCELLED = "cancel";

	public static final String STATE_COMMNENT_HR1 = "HR1 comment";
	public static final String STATE_COMMNENT_HR2 = "HR2 comment";
	public static final String STATE_VALIDATE_TRANSFER_PROPOSAL = "validateTransferProposal";
	
	public static final String TRANSITION_COMMENT_HR1 = "to HR1 comment";
	public static final String TRANSITION_COMMENT_HR2 = "to HR2 comment";
	public static final String TRANSITION_DIRECT_CANCEL = "to cancel";
	public static final String TRANSITION_PROPOSAL_VALIDATION = "to validateTransferProposal";
	public static final String TRANSITION_VALIDATE_TRANSFER = "validateTransfer";
	public static final String TRANSITION_CANCEL_TRANSFER = "cancelTransfer";
	
	
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

	@Autowired
	@Qualifier("utilDao")
	UtilDao utilDao = null;

	private ProcessEngine processEngine;
	private ExecutionService executionService;
	private TaskService taskService;

	Logger logger = LoggerFactory.getLogger(TransferManagerImpl.class);

	public TransferManagerImpl() {
		processEngine = new Configuration().setResource(JBPM_CONFIG_FILE).buildProcessEngine();
		executionService = processEngine.getExecutionService();
		taskService = processEngine.getTaskService();
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#startAndAskTransferProcess(long, long, java.lang.String)
	 */
	public Personne startAndAskTransferRequestProcess(long transDepEntrId, long transDepConsulId, String assignee) throws MercatoWorkflowException {
		try {
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
			processContextVariables.put(CONTEXT_TRANSFERT_ID, new Long(transfertId));

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


			processContextVariables.put(CONTEXT_DD1, loginDD1);
			processContextVariables.put(CONTEXT_DD2, loginDD2);

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

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalAskConsultant(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public void signalAskConsultant(Transfert transfert, String assignee) {
		try {


			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), CONTEXT_TRANSFERT_ID);
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



	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalValidateTransferRequest(com.alten.mercato.server.model.Transfert, java.lang.String, java.lang.String)
	 */
	public Personne signalValidateTransferRequest(Transfert transfert,
			String assignee, String validation) throws MercatoWorkflowException{
		try {
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), CONTEXT_TRANSFERT_ID);
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
			if (historyProcessInstance.getState().equals(STATUS_ENDED)) {
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
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), CONTEXT_TRANSFERT_ID);
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
			if (historyProcessInstance.getState().equals(STATUS_ENDED)) {
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
	 * @see com.alten.mercato.server.manager.interf.TransferManager#startAndAskTransferProposalProcess(long, long, java.lang.String)
	 */
	public Personne startAndAskTransferProposalProcess(long transDepEntrId,
			long transDepConsulId, String assignee)
	throws MercatoWorkflowException {
		try {

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
			processContextVariables.put(CONTEXT_TRANSFERT_ID, new Long(transfertId));

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


			processContextVariables.put(CONTEXT_DD1, loginDD1);
			processContextVariables.put(CONTEXT_DD2, loginDD2);

			logger.info("Starting the process instance");
			Execution execution = executionService.startProcessInstanceByKey(KEY_TRANSFER_PROPOSAL_PROCESS_V2_DEFINITION, processContextVariables);
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

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalProposeConsultant(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public void signalProposeConsultant(Transfert transfert, String assignee) {
		try {
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			// retrieve the task assigned to the assignee for the current transfer
			Task taskToComplete = null;

			if (taskList.size() > 0) {
				for (Task task:taskList) {

					Long transferId = (Long) taskService.getVariable(task.getDbid(), CONTEXT_TRANSFERT_ID);

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

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#startAndProposeTransferProcessV2(long, long, java.lang.String)
	 */
	public Personne startAndProposeTransferProcessV2(long transDepEntrId,
			long transDepConsulId, String assignee)
	throws MercatoWorkflowException {
		try {

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
			processContextVariables.put(CONTEXT_TRANSFERT_ID, new Long(transfertId));

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


			processContextVariables.put(CONTEXT_DD1, loginDD1);
			processContextVariables.put(CONTEXT_DD2, loginDD2);
			processContextVariables.put(CONTEXT_COMMENT_HR1, "");
			processContextVariables.put(CONTEXT_COMMENT_HR2, "");

			logger.info("Starting the process instance");
			Execution execution = executionService.startProcessInstanceByKey(KEY_TRANSFER_PROPOSAL_PROCESS_DEFINITION, processContextVariables);
			logger.info("process started");

			transfert.setTransExecId(execution.getId());

			logger.info("Persisting transfert with execution id");
			// update the transfert with the generated execution id
			transfertDao.attachDirty(transfert);
			logger.info("transfert persisted");

			// update the consultant(PERSONNE TABLE) with the current transfer
			consultant.setTransferCourant(transfert);
			logger.info("Persisting personne");
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

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalACommentHR1(com.alten.mercato.server.model.Transfert, java.lang.String, java.lang.String)
	 */
	public void signalCommentHR1(Transfert transfert, String assignee, String comment) throws MercatoWorkflowException{
		try {

			Execution mainExecution = executionService.findExecution(transfert.getTransExecId());
			//check if the execution is still alive
			if ( mainExecution == null) {
				throw new MercatoWorkflowException("the execution is finished");
			}

			Util user = utilDao.getUtilByLogin(assignee);
			// HR1 acces right
			if (!user.getPersonne().getTypePersonne().getTpersCode().equals("RH")) {
				throw new MercatoWorkflowException("must be a human ressource role to make a commatary");
			}


			if (!(user.getPersonne().getDepartement().getDepId() == transfert.getConsultant().getDepartement().getDepId())) {
				throw new MercatoWorkflowException("the consultant is not in your department");
			}


			// validate the transfer
			executionService.signalExecutionById(mainExecution.getId(), TRANSITION_COMMENT_HR1);

			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(CONTEXT_COMMENT_HR1, comment);

			String commentHR1 = findExecution(mainExecution.getId(), STATE_COMMNENT_HR1).getId();
			executionService.signalExecutionById(commentHR1,variables);

			logger.info("completing comment");
			return;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}


	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalACommentHR2(com.alten.mercato.server.model.Transfert, java.lang.String, java.lang.String)
	 */
	public void signalCommentHR2(Transfert transfert, String assignee, String comment) throws MercatoWorkflowException {
		try {

			Execution mainExecution = executionService.findExecution(transfert.getTransExecId());
			//check if the execution is still alive
			if ( mainExecution == null) {
				throw new MercatoWorkflowException("the execution is finished");
			}

			Util user = utilDao.getUtilByLogin(assignee);
			// HR1 acces right
			if (!user.getPersonne().getTypePersonne().getTpersCode().equals("RH")) {
				throw new MercatoWorkflowException("must be a human ressource role to make a commatary");
			}


			if (!(user.getPersonne().getDepartement().getDepId() == transfert.getDepEntr().getDepId())) {
				throw new MercatoWorkflowException("the consultant is not to be transfered to your department");
			}


			// validate the transfer
			executionService.signalExecutionById(mainExecution.getId(), TRANSITION_COMMENT_HR2);

			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(CONTEXT_COMMENT_HR2, comment);

			String commentHR2 = findExecution(mainExecution.getId(), STATE_COMMNENT_HR2).getId();
			executionService.signalExecutionById(commentHR2,variables);

			logger.info("completing comment");
			return;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}

	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalCancelTransfer(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public Personne signalCancelTransfer(Transfert transfert, String assignee) throws MercatoWorkflowException {
		try {

			Execution mainExecution = executionService.findExecution(transfert.getTransExecId());
			//check if the execution is still alive
			if ( mainExecution == null) {
				throw new MercatoWorkflowException("the execution is finished");
			}
			
			Util user = utilDao.getUtilByLogin(assignee);
			// HR1 acces right
			if (!user.getPersonne().getTypePersonne().getTpersCode().equals("DD")) {
				throw new MercatoWorkflowException("must be department director to cancel the transfer");
			}


			if (!(user.getPersonne().getDepartement().getDepId() == transfert.getConsultant().getDepartement().getDepId())) {
				throw new MercatoWorkflowException("the consultant is not in your department");
			}

			logger.info("signalling cancel ...");
			// validate the transfer
			executionService.signalExecutionById(mainExecution.getId(), TRANSITION_CANCEL_TRANSFER);

			Personne personne = transfert.getConsultant();
			logger.info("transfer cancelled");
			personne.setTransferCourant(null);
			personneDao.attachDirty(personne);
			return personne;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalValidateTransferProposalV2(com.alten.mercato.server.model.Transfert, java.lang.String, java.lang.String)
	 */
	public Personne signalValidateTransferProposalV2(Transfert transfert,
			String assignee, String validation) throws MercatoWorkflowException {
		try {


			Util user = utilDao.getUtilByLogin(assignee);
			// HR1 acces right
			if (!user.getPersonne().getTypePersonne().getTpersCode().equals("DD")) {
				throw new MercatoWorkflowException("must be department director to validate or cancel the transfer");
			}
			

			if (!(user.getPersonne().getDepartement().getDepId() == transfert.getDepEntr().getDepId())) {
				throw new MercatoWorkflowException("the consultant is not to be transfered to your department");
			}
			logger.info("signalling transfer ...");
			
			executionService.signalExecutionById(transfert.getTransExecId(), TRANSITION_PROPOSAL_VALIDATION);

			String validateTransfer = findExecution(transfert.getTransExecId(), STATE_VALIDATE_TRANSFER_PROPOSAL).getId();
			executionService.signalExecutionById(validateTransfer, validation);
			
			HistoryService historyService = processEngine.getHistoryService();

			HistoryProcessInstance historyProcessInstance = historyService.createHistoryProcessInstanceQuery().processInstanceId(transfert.getTransExecId()).executeUniqueResult();
			Personne personne = transfert.getConsultant();
			// if the execution is ended normally, the transfer is validated
			if (historyProcessInstance.getState().equals(STATUS_ENDED)) {
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

	private Execution findExecution(String processInstanceId, String activityName) {

		if (executionService.findExecution(processInstanceId) == null) {
			return null;
		}
		List<Execution> executions = executionService.findExecutions(processInstanceId);
		if (executions != null) {
			if (executions.size() > 0) {
				for (Execution execution: executions) {
					if (activityName.equals(execution.getActivityName())) {
						return execution;
					}
				}
			}
		}

		return null;
	}

}
