package com.alten.mercato.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Huage Chen
 *
 */
@RemoteServiceRelativePath("transfer.rpc")
public interface TransferService extends RemoteService{
	
	public static class Util {

		public static TransferServiceAsync getInstance() {

			return GWT.create(TransferService.class);
		}
	}

}
