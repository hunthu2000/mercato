/**
 * 
 */
package com.alten.mercato.server.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alten.mercato.server.dao.home.DepartementDaoHome;
import com.alten.mercato.server.dao.home.TransfertDaoHome;
import com.alten.mercato.server.dao.interf.DepartementDao;
import com.alten.mercato.server.dao.interf.TransfertDao;
import com.alten.mercato.server.model.Transfert;

/**
 * @author Huage Chen
 *
 */
@Repository("DepartementMercatoDao")
public class TransfertDaoImpl extends TransfertDaoHome implements
		TransfertDao {
	private static final Log log = LogFactory.getLog(TransfertDaoImpl.class);

	@Autowired
	public TransfertDaoImpl(@Qualifier("mercatoSF")
	SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
	
}
