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

import com.alten.mercato.server.dao.interf.TransfertDao;
import com.alten.mercato.server.manager.interf.TransferManager;
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

	@Autowired
	@Qualifier("transferDao")
	TransfertDao transferDao = null;

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
	public void startTransferProcess(long transDepEntrId, long transDepConsulId) {

		try {

			//create the new transfert object save it to let the database generate automatically the transfert id
			Transfert transfert = new Transfert(transDepEntrId, transDepConsulId);
			transferDao.attachDirty(transfert);

			long transfertId = transfert.getTransId();

			Map<String, Object> processContextVariables = new HashMap<String, Object>();

			// put the transfertId to the process contextVariable map
			processContextVariables.put(KEY_PROCESS_CONTEXT_TRANSFERT_ID, transfertId);

			Execution execution = executionService.startProcessInstanceByKey(KEY_TRANSFER_REQUEST_PROCESS_DEFINITION, processContextVariables);

			//associate the execution id to the transfert object and update the database, from now on we have established the virtual association
			//between the execution and the transfert object
			transfert.setTransExecId(execution.getDbid());
			transferDao.attachDirty(transfert);
		}
		catch (JbpmException e ){
			logger.error(e.toString());
			return;
		}
	}

	/* (non-Javadoc)
	 * @see com.alten.mercato.server.manager.interf.TransferManager#signalAskConsultant(com.alten.mercato.server.model.Transfert, java.lang.String)
	 */
	public void signalAskConsultant(Transfert transfert, String assignee) {

		try {
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			Map<String, Object> taskVariables;
			Set<String> variableNames = new HashSet<String>();
			variableNames.add(KEY_PROCESS_CONTEXT_TRANSFERT_ID);

			Task taskToComplete = null;

			if (taskList.size() > 0) {
				long transfertId;
				for (Task task:taskList) {
					taskVariables = taskService.getVariables(task.getDbid(), variableNames);
					transfertId = new Long(taskVariables.get(KEY_PROCESS_CONTEXT_TRANSFERT_ID).toString()).longValue();

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
	public void signalValidateTransfer(Transfert transfert, String assignee, boolean validation) {
		try {
			List<Task> taskList = taskService.findAssignedTasks(assignee);

			Map<String, Object> taskVariables;
			Set<String> variableNames = new HashSet<String>();
			variableNames.add(KEY_PROCESS_CONTEXT_TRANSFERT_ID);

			Task taskToComplete = null;

			if (taskList.size() > 0) {
				long transfertId;
				for (Task task:taskList) {
					taskVariables = taskService.getVariables(task.getDbid(), variableNames);
					transfertId = new Long(taskVariables.get(KEY_PROCESS_CONTEXT_TRANSFERT_ID).toString()).longValue();

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
