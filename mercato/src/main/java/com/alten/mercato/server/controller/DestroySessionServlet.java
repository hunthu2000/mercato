/**
 * 
 */
package com.alten.mercato.server.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

/**
 * Servlet implementation that logs the current user out  to upload 

 * @author huagechen
 *
 */
@Service("DestroySessionController")
public class DestroySessionServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		res.setContentType( "text/html" );
		PrintWriter pw = res.getWriter();
		pw.println( "<HTML><BODY>" );

		// get current session, and don't create one if it doesn't exist
		HttpSession theSession = req.getSession( false );

		// print out the session id
		if( theSession != null ) {
			
			String userName = (String) theSession.getAttribute("User");
			
			synchronized( theSession ) {
				// invalidating a session destroys it
				theSession.removeAttribute("User");
				theSession.invalidate();
				pw.println( "<BR>User" + userName + "logged out." );
			}
		}

		pw.println( "</BODY></HTML>" );
		pw.close();
	}
}
