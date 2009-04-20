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

import com.alten.mercato.server.dao.home.PersonneDaoHome;
import com.alten.mercato.server.dao.interf.PersonneDao;
import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
@Repository("personneDao")
public class PersonneDaoImpl extends PersonneDaoHome implements
		PersonneDao {
	private static final Log log = LogFactory.getLog(PersonneDaoImpl.class);

	private static final long ID_TYPE_CONSULTANT = 1000L;
	
	@Autowired
	public PersonneDaoImpl(@Qualifier("mercatoSF")
	SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
	
	
	/* (non-Javadoc)
	 * @see com.alten.mercato.server.dao.interf.PersonneDao#findConsultantsByDepartmentId(long)
	 */
	public List<Personne> findConsultantsByDepartmentId(long departmentId) {
		if (log.isDebugEnabled()) {
			log.debug("finding all consultants in the department " + departmentId);
		}
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(Personne.class);
			criteria.add(Restrictions.eq("departement.depId", departmentId));
			// get only consultants, omit department director and human resources
			criteria.createAlias("typePersonne", "tp").add(Restrictions.eq("tp.tpersCode","CONSULT"));
			//criteria.add(Restrictions.eq("typePersonne.tpersId", ID_TYPE_CONSULTANT));
			criteria.addOrder(Order.asc("perNom"));
			List<Personne> lst = getHibernateTemplate().findByCriteria(criteria);
			if (null==lst||0==lst.size()) {
				if (log.isDebugEnabled()) {
					log.debug("No consultant in the department " + departmentId + " is found" );
				}
				return null;
			}
			if (log.isDebugEnabled()) {
				log.debug(""+lst.size()+" person(consultants) instances founded");
			}
			return lst;
		} catch (RuntimeException re) {
			log.error("finding consultants by department id failed", re);
			throw re;
		}
	}

}
