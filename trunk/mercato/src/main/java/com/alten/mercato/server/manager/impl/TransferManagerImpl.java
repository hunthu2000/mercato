/**
 * 
 */
package com.alten.mercato.server.manager.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.Execution;
import org.jbpm.ExecutionService;
import org.jbpm.HistoryService;
import org.jbpm.JbpmException;
import org.jbpm.RepositoryService;
import org.jbpm.TaskService;
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

	private static final String KEY_TRANSFER_REQUEST_PROCESS_DEFINITION = "TransferRequest";
	private static final String KEY_PROCESS_CONTEXT_TRANSFERT_ID = "TransfertId";
	private static final String VALIDATE_TRANSFER_TRANSITION = "validateTransfer";
	private static final String CANCEL_TRANSFER_TRANSITION = "cancelTransfer";
	private static final String KEY_CONTEXT_DD1 = "dd1";
	private static final String KEY_CONTEXT_DD2 = "dd2";

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
	@Qualifier("repositoryService")
	RepositoryService repositoryService = null;

	@Autowired
	@Qualifier("executionService")
	ExecutionService executionService = null;

	@Autowired
	@Qualifier("taskService")
	TaskService taskService = null;

	@Autowired
	@Qualifier("historyService")
	HistoryService historyService = null;

	Logger logger = LoggerFactory.getLogger(TransferManagerImpl.class);

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#startTransferProcess(long, long)
	 */
	public Personne startTransferProcess(long transDepEntrId, long transDepConsulId)  throws MercatoWorkflowException{

		try {
			
			// retrieve the consultant to be transfered
			Personne consultant = personneDao.findById(transDepConsulId);
			
			// retrieve the new department that the consultant is to be entered
			Departement departement = departementDao.findById(transDepEntrId);
			
			// if one of the two objects is null, the transfer can not be started
			if ((consultant == null) || (departement == null)) {
				throw new MercatoWorkflowException("Constant id or department id is not correct");
			}
			
			//find the existing transfer assigned to the consultant, the list should either be empty 
			//or contain only past transfers to allow the execution to be started
			Set<Transfert> setTransfert = consultant.getTransferts();
			
			if (setTransfert != null) {
				for (Transfert trans: setTransfert) {
					String exeId = trans.getTransExecId();
					Execution execution = executionService.findExecution(exeId);
					if (execution != null) {
						throw new MercatoWorkflowException("Transfer for this consultant is still in progress");
					}
				}
			}

			//create the new transfert object save it to let the database generate automatically the transfert id
			Transfert transfert = new Transfert(consultant, departement);
			transfertDao.attachDirty(transfert);

			long transfertId = transfert.getTransId();

			Map<String, Object> processContextVariables = new HashMap<String, Object>();

			// put the transfertId to the process contextVariable map
			processContextVariables.put(KEY_PROCESS_CONTEXT_TRANSFERT_ID, transfertId);
			
			// assign dynamically the actors of the workflow
			processContextVariables.put(KEY_CONTEXT_DD1, departement.getPersonne().getPerId());
			processContextVariables.put(KEY_CONTEXT_DD2, consultant.getDepartement().getPersonne().getPerId());
			
			Execution execution = executionService.startProcessInstanceByKey(KEY_TRANSFER_REQUEST_PROCESS_DEFINITION, processContextVariables);

			//associate the execution id to the transfert object and update the database, from now on we have established the virtual association
			//between the execution and the transfert object
			transfert.setTransExecId(execution.getId());
			transfertDao.attachDirty(transfert);
			
			
			return consultant;
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalAskConsultant(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public void signalAskConsultant(Transfert transfert, String assignee) throws MercatoWorkflowException {

		try {
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			Map<String, Object> taskVariables;
			Set<String> variableNames = new HashSet<String>();
			variableNames.add(KEY_PROCESS_CONTEXT_TRANSFERT_ID);

			Task taskToComplete = null;

			if (taskList.size() > 0) {
				String transfertId;
				for (Task task:taskList) {
					taskVariables = taskService.getVariables(task.getDbid(), variableNames);
					transfertId = taskVariables.get(KEY_PROCESS_CONTEXT_TRANSFERT_ID).toString();

					if ( transfertId == transfert.getTransExecId()) {
						taskToComplete = task;
						break;
					}
				}
			}

			if (taskToComplete == null) {
				logger.info("no task found for " + assignee + " for the current transfer " + transfert.getTransId());
				return;
			}

			taskService.completeTask(taskToComplete.getDbid());
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalValidateTransfer(com.alten.mercato.server.model.Transfert, java.lang.String, boolean)
	 */
	public void signalValidateTransfer(Transfert transfert, String assignee, boolean validation) throws MercatoWorkflowException{
		try {
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			Map<String, Object> taskVariables;
			Set<String> variableNames = new HashSet<String>();
			variableNames.add(KEY_PROCESS_CONTEXT_TRANSFERT_ID);

			Task taskToComplete = null;

			if (taskList.size() > 0) {
				String transfertId;
				for (Task task:taskList) {
					taskVariables = taskService.getVariables(task.getDbid(), variableNames);
					transfertId = taskVariables.get(KEY_PROCESS_CONTEXT_TRANSFERT_ID).toString();

					if ( transfertId == transfert.getTransExecId()) {
						taskToComplete = task;
						break;
					}
				}
			}

			if (taskToComplete == null) {
				logger.info("no task found for " + assignee + " for the current transfer " + transfert.getTransId());
				return;
			}
			if (validation) {
				taskService.completeTask(taskToComplete.getDbid(), VALIDATE_TRANSFER_TRANSITION);
			} else {
				taskService.completeTask(taskToComplete.getDbid(), CANCEL_TRANSFER_TRANSITION);
			}
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}
	}


}
