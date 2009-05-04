/**
 * 
 */
package com.alten.mercato.client.service;

import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Huage Chen
 *
 */
@RemoteServiceRelativePath("userService.rpc")
public interface UserService extends RemoteService{
	public static class Util {

		public static UserServiceAsync getInstance() {

			return GWT.create(UserService.class);
		}
	}

	public Personne getCurrentUser() throws Exception;
}
