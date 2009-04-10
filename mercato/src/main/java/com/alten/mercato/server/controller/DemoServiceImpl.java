package com.alten.mercato.server.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtwidgets.server.spring.ServletUtils;

import com.alten.mercato.client.service.DemoService;

/**
 * @author Huage Chen
 */

@Service("demoController")
public class DemoServiceImpl  implements DemoService  {


	
	// log4j
	private Log log  = LogFactory.getLog(getClass());
	
	public String getString() {
		
		
		return "Mercato GWT user: " + ServletUtils.getRequest().getUserPrincipal().getName();
	}

	public boolean logout() throws Exception{
		try {
			HttpSession session = ServletUtils.getRequest().getSession(false);
			log.debug("Logout request user : session id " + session.getId() + " user " + session.getAttribute("User") );
			
			ServletUtils.getRequest().getSession(false).removeAttribute("User");
			ServletUtils.getRequest().getSession(false).invalidate();
			
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);	
			throw e;
		}
	}
	
	

}
