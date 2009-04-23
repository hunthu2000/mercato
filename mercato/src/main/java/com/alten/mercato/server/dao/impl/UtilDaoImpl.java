/**
 * 
 */
package com.alten.mercato.server.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alten.mercato.server.dao.home.UtilDaoHome;
import com.alten.mercato.server.dao.interf.UtilDao;
import com.alten.mercato.server.model.Util;

/**
 * @author Huage Chen
 *
 */
@Repository("utilDao")
public class UtilDaoImpl extends UtilDaoHome implements
UtilDao {

	private static final Log log = LogFactory.getLog(UtilDaoImpl.class);

	@Autowired
	public UtilDaoImpl(@Qualifier("mercatoSF")
			SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Util> getUserByLogin(String login) {
		if (log.isDebugEnabled()) {
			log.debug("finding all root Categories instance");
		}
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(Util.class);			
			criteria.add(Restrictions.eq("utilLogin", login));
			List<Util> lst = getHibernateTemplate().findByCriteria(criteria);
			if (null==lst||0==lst.size()) {
				if (log.isDebugEnabled()) {
					log.debug("No user instance founded");
				}
				return null;
			}
			if (log.isDebugEnabled()) {
				log.debug(""+lst.size()+" user instance founded");
			}
			
			return lst;
		} catch (RuntimeException re) {
			log.error("finding all root Categories failed", re);
			throw re;
		}
	}
}
