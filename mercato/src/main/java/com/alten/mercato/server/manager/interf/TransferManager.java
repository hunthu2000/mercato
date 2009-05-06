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
	
	public Personne startAndProposeTransferProcessV2(long transDepEntrId, long transDepConsulId, String assignee) throws MercatoWorkflowException;
	
	public void signalCommentHR1(Transfert transfert, String assignee, String comment) throws MercatoWorkflowException;
	public void signalCommentHR2(Transfert transfert, String assignee, String comment) throws MercatoWorkflowException;
	
	/**
	 * the transfer is cancelled before the dd2 validates it
	 * @param transfert
	 * @param assignee
	 * @return
	 * @throws MercatoWorkflowException
	 */
	public Personne signalCancelTransfer(Transfert transfert, String assignee) throws MercatoWorkflowException;

	/**
	 * the transfer is being validated or cancelled
	 * @param transfert
	 * @param assignee
	 * @param validation
	 * @return
	 * @throws MercatoWorkflowException
	 */
	public Personne signalValidateTransferProposalV2(Transfert transfert, String assignee,  String validation) throws MercatoWorkflowException;
	
}
