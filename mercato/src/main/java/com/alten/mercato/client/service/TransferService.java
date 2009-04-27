package com.alten.mercato.client.service;

import java.util.List;

import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Huage Chen
 *
 */
@RemoteServiceRelativePath("transferService.rpc")
public interface TransferService extends RemoteService{
	
	public static class Util {

		public static TransferServiceAsync getInstance() {

			return GWT.create(TransferService.class);
		}
	}
	
	/**
	 * @param transDepEntrId
	 * @param transDepConsulId
	 * @return
	 */
	public Personne startAndAskTransferProcess(long transDepEntrId, long transDepConsulId) throws Exception;

	public List<InfoTransfer> getConsultantWithTransferInfo() throws Exception;
}
