/**
 * 
 */
package com.alten.mercato.server.manager.interf;

import java.util.List;

import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.exception.MercatoWorkflowException;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;


/**
 * @author Huage Chen
 *
 */

public interface TransferManager {
	
	
	
	public Personne startAndAskTransferProcess(long transDepEntrId, long transDepConsulId, String assignee) throws MercatoWorkflowException;

	public void signalAskConsultant(Transfert transfert, String assignee);
	
	public List<InfoTransfer> getConsultantsWithTransferInfo();
}
