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
	
	/**
	 * @param transDepEntrId
	 * @param transDepConsulId
	 * @param assignee
	 * @return
	 * @throws MercatoWorkflowException
	 */
	public Personne startAndAskTransferRequestProcess(long transDepEntrId, long transDepConsulId, String assignee) throws MercatoWorkflowException;

	/**
	 * @param transfert
	 * @param assignee
	 * @param validation
	 * @return
	 * @throws MercatoWorkflowException
	 */
	public Personne signalValidateTransferRequest(Transfert transfert, String assignee,  String validation)  throws MercatoWorkflowException;
	
	/**
	 * @param transfert
	 * @param assignee
	 */
	public void signalAskConsultant(Transfert transfert, String assignee);
	
	
	
	/**
	 * @param transDepEntrId
	 * @param transDepConsulId
	 * @param assignee
	 * @return
	 * @throws MercatoWorkflowException
	 */
	public Personne startAndAskTransferProposalProcess(long transDepEntrId, long transDepConsulId, String assignee) throws MercatoWorkflowException;

	/**
	 * @param transfert
	 * @param assignee
	 */
	public void signalProposeConsultant(Transfert transfert, String assignee);
	
	/**
	 * @param transfert
	 * @param assignee
	 * @param validation
	 * @return
	 * @throws MercatoWorkflowException
	 */
	public Personne signalValidateTransferProposal(Transfert transfert, String assignee,  String validation)  throws MercatoWorkflowException;
	
}
