package com.alten.mercato.client.service;


import com.alten.mercato.server.model.Personne;
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
}
