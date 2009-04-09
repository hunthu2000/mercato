/**
 * 
 */
package com.alten.mercato.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author huagechen
 *
 */
@RemoteServiceRelativePath("demoService.rpc")
public interface DemoService extends RemoteService {

	public static class Util {

		public static DemoServiceAsync getInstance() {

			return GWT.create(DemoService.class);
		}
	}
	
	public String getString();

	public boolean logout() throws Exception;
}
