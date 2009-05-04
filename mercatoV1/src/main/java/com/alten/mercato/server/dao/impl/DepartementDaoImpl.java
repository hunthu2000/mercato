/**
 * 
 */
package com.alten.mercato.server.dao.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alten.mercato.server.dao.home.DepartementDaoHome;
import com.alten.mercato.server.dao.interf.DepartementDao;
import com.alten.mercato.server.model.Departement;

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

	@SuppressWarnings("unchecked")
	public List<Departement> findOtherDepartmentsByDepartmentID(
			long departmentId) {
		if (log.isDebugEnabled()) {
			log.debug("finding all departments except the department " + departmentId);
		}
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(Departement.class);
			criteria.add(Restrictions.ne("depId", departmentId));
			criteria.addOrder(Order.asc("depLib"));
			List<Departement> lst = getHibernateTemplate().findByCriteria(criteria);
			if (null==lst||0==lst.size()) {
				if (log.isDebugEnabled()) {
					log.debug("No departments found" );
				}
				return null;
			}
			if (log.isDebugEnabled()) {
				log.debug(""+lst.size()+" departments instances founded");
			}
			return lst;
		} catch (RuntimeException re) {
			log.error("finding other departments failed", re);
			throw re;
		}
	}
}
