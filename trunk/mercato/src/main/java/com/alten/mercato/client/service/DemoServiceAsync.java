/**
 * 
 */
package com.alten.mercato.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author huagechen
 *
 */
public interface DemoServiceAsync {

	public void getString(AsyncCallback<String> callback);

	public void logout(AsyncCallback<Boolean> callback);

}
