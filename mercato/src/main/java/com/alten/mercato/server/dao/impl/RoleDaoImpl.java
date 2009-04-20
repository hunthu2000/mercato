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

import com.alten.mercato.server.dao.home.RoleDaoHome;
import com.alten.mercato.server.dao.interf.RoleDao;

/**
 * @author Huage Chen
 *
 */
@Repository("roleDao")
public class RoleDaoImpl extends RoleDaoHome implements
		RoleDao {
	private static final Log log = LogFactory.getLog(RoleDaoImpl.class);

	@Autowired
	public RoleDaoImpl(@Qualifier("mercatoSF")
	SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
