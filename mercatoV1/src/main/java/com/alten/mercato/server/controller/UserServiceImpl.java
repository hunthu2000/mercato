/**
 * 
 */
package com.alten.mercato.server.controller;

import org.gwtwidgets.server.spring.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alten.mercato.client.service.UserService;
import com.alten.mercato.server.manager.interf.UserManager;
import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
@Service("userController")
public class UserServiceImpl implements UserService {
	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	
	@Autowired
	@Qualifier("userManager")
	private UserManager userManager = null;
	
	
	public Personne getCurrentUser() throws Exception{
		try {
			String login = ServletUtils.getRequest().getUserPrincipal().getName();
			logger.info("retrieving the current user login information " + login);
			Personne user = userManager.getUser(ServletUtils.getRequest().getUserPrincipal().getName());
			
			//lazy loading
			user.getPerNom();
			user.getDepartement().getDepLib();
			logger.info("user information successfully retrieved, returning to the client side of user:" + user.getPerNom());
			
			return user;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} 
	}
	
}
