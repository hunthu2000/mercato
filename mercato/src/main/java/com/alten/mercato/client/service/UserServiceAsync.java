/**
 * 
 */
package com.alten.mercato.client.service;

import com.alten.mercato.server.model.Personne;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Huage Chen
 *
 */
public interface UserServiceAsync {
	
	public void getCurrentUser(AsyncCallback<Personne> callback);

}
