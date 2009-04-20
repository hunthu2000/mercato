/**
 * 
 */
package com.alten.mercato.server.manager.interf;

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
	public void startTransferProcess(long transDepEntrId, long transDepConsulId);
	
	
	/**
	 * @param transfert
	 * @param assignee
	 */
	public void signalAskConsultant(Transfert transfert, String assignee);
	
	
	public void signalValidateTransfer(Transfert transfert, String assignee, boolean validation);

}
