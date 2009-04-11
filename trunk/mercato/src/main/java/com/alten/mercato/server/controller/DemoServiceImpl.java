package com.alten.mercato.server.controller;


import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.gwtwidgets.server.spring.ServletUtils;

import com.alten.mercato.client.service.DemoService;

/**
 * @author Huage Chen
 */

@Service("demoController")
public class DemoServiceImpl  implements DemoService  {
	
	// log4j
	Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);
	
	public String getString() {
		logger.info("Getting string in the controller" );
		
		return "Mercato GWT user: " + ServletUtils.getRequest().getUserPrincipal().getName();
	}

	public boolean logout() throws Exception{
		try {
			HttpSession session = ServletUtils.getRequest().getSession(false);
			logger.info("Logout request user : session id " + session.getId() + " user " + session.getAttribute("User") );
			
			ServletUtils.getRequest().getSession(false).removeAttribute("User");
			ServletUtils.getRequest().getSession(false).invalidate();
			
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);	
			throw e;
		}
	}
	
	

}
