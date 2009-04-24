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
	 */
	public Personne startTransferProcess(long transDepEntrId, long transDepConsulId) throws MercatoWorkflowException;
	
	/**
	 * @param transfert
	 * @param assignee
	 */
	public void signalAskConsultant(Transfert transfert, String assignee) throws MercatoWorkflowException;
	
	
	public void signalValidateTransfer(Transfert transfert, String assignee, boolean validation) throws MercatoWorkflowException;

}
