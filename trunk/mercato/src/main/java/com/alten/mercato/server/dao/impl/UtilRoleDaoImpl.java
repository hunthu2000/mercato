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

import com.alten.mercato.server.dao.home.UtilRoleDaoHome;
import com.alten.mercato.server.dao.interf.UtilRoleDao;


/**
 * @author Huage Chen
 *
 */
@Repository("UtilRoleMercatoDao")
public class UtilRoleDaoImpl extends UtilRoleDaoHome implements
	UtilRoleDao {
	private static final Log log = LogFactory.getLog(UtilRoleDaoImpl.class);

	@Autowired
	public UtilRoleDaoImpl(@Qualifier("mercatoSF")
			SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
}
