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

import com.alten.mercato.server.dao.home.DepartementDaoHome;
import com.alten.mercato.server.dao.interf.DepartementDao;

/**
 * @author Huage Chen
 *
 */
@Repository("departementDao")
public class DepartementDaoImpl extends DepartementDaoHome implements
		DepartementDao {
	private static final Log log = LogFactory.getLog(DepartementDaoImpl.class);

	@Autowired
	public DepartementDaoImpl(@Qualifier("mercatoSF")
	SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
