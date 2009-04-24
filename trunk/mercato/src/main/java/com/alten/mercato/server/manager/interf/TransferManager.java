/**
 * 
 */
package com.alten.mercato.server.manager.interf;

import com.alten.mercato.server.exception.MercatoWorkflowException;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;


/**
 * @author Huage Chen
 *
 */

public interface TransferManager {
	
	/**
	 * 
	 * Create the transfer object and start the corresponding process instance
	 * @param transDepEntrId
	 * @param transDepConsulId
	 * @throws MercatoWorkflowException
	 */
	public Personne startTransferProcess(long transDepEntrId, long transDepConsulId) throws MercatoWorkflowException;
	
	/**
	 * complete the task "ask consultant" of the workflow request transfer
	 * @param transfert
	 * @param assignee
	 * @throws MercatoWorkflowException
	 */
	public void signalAskConsultant(Transfert transfert, String assignee) throws MercatoWorkflowException;
	
	
	/**
	 * 
	 * complete the task "validation transfer" of the workflow request transfer
	 * @param transfert
	 * @param assignee
	 * @param validation
	 * @throws MercatoWorkflowException
	 */
	public void signalValidateTransfer(Transfert transfert, String assignee, boolean validation) throws MercatoWorkflowException;

}
