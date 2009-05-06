package com.alten.mercato.client.service;


import java.util.List;

import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.model.Personne;
import com.alten.mercato.server.model.Transfert;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Huage Chen
 *
 */
public interface TransferServiceAsync {

	
	/**
	 * @param transDepEntrId
	 * @param transDepConsulId
	 * @param callback
	 */
	public void startAndAskTransferProcess(long transDepEntrId, long transDepConsulId, AsyncCallback<Personne> callback);

	/**
	 * @param transfert
	 * @param callback
	 */
	public void validateTransferRequestProcess(Transfert transfert, String validation, AsyncCallback<Personne> callback);
	
	public void startAndProposeTransferProcess(long transDepEntrId, long transDepConsulId, AsyncCallback<Personne> callback);

	/**
	 * @param transfert
	 * @param callback
	 */
	public void validateTransferProposalProcess(Transfert transfert, String validation, AsyncCallback<Personne> callback);
	
	
	/**
	 * @param callback
	 */
	public void  getConsultantWithTransferInfo(AsyncCallback<List<InfoTransfer>> callback) ;
	
	/**
	 * @param transDepEntrId
	 * @param transDepConsulId
	 * @param callback
	 */
	public void startAndProposeTransferProcessV2(long transDepEntrId, long transDepConsulId, AsyncCallback<Personne> callback);
	
	/**
	 * @param transfert
	 * @param comment
	 * @param callback
	 */
	public void signalCommentHR1(Transfert transfert, String comment, AsyncCallback<Boolean> callback);
	
	/**
	 * @param transfert
	 * @param comment
	 * @param callback
	 */
	public void signalCommentHR2(Transfert transfert, String comment, AsyncCallback<Boolean> callback);
	
	/**
	 * @param transfert
	 * @param callback
	 */
	public void signalCancelTransfer(Transfert transfert, AsyncCallback<Personne> callback);
	
	public void signalCancelTransfer(List<Transfert> transfert, AsyncCallback<List<Personne>> callback);
	
	/**
	 * @param transfert
	 * @param validation
	 * @param callback
	 */
	public void signalValidateTransferProposalV2(Transfert transfert,  String validation, AsyncCallback<Personne> callback);
	
}
