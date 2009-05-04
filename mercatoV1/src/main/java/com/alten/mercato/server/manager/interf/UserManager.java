package com.alten.mercato.server.manager.interf;

import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
public interface UserManager {
	public String getUserName(String login);
	
	public Personne getUser(String login);
}
