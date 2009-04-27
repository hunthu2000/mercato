package com.alten.mercato.client.service;


import java.util.List;

import com.alten.mercato.server.controller.dto.InfoTransfer;
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

	public void  getConsultantWithTransferInfo(AsyncCallback<List<InfoTransfer>> callback) ;
}
