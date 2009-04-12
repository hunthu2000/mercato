/**
 * 
 */
package com.alten.mercato.server.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alten.mercato.server.dao.home.PersonneDaoHome;
import com.alten.mercato.server.dao.interf.PersonneDao;

/**
 * @author Huage Chen
 *
 */
@Repository("PersonneMercatoDao")
public class PersonneDaoImpl extends PersonneDaoHome implements
		PersonneDao {
	private static final Log log = LogFactory.getLog(PersonneDaoImpl.class);

	@Autowired
	public PersonneDaoImpl(@Qualifier("mercatoSF")
	SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
