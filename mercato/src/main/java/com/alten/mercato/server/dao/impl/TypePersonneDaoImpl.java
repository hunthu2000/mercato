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

import com.alten.mercato.server.dao.home.TypePersonneDaoHome;
import com.alten.mercato.server.dao.interf.TypePersonneDao;

/**
 * @author Huage Chen
 *
 */
@Repository("typePersonneDao")
public class TypePersonneDaoImpl extends TypePersonneDaoHome implements
		TypePersonneDao {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(TypePersonneDaoImpl.class);

	@Autowired
	public TypePersonneDaoImpl(@Qualifier("mercatoSF")
	SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
