package com.alten.mercato.server.dao.home;
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alten.mercato.server.model.Departement;

/**
 * Home object for domain model class Departement.
 * @see .Departement
 * @author Hibernate Tools
 */
public class DepartementDaoHome extends HibernateDaoSupport{

	private static final Log log = LogFactory.getLog(DepartementDaoHome.class);



	public void persist(Departement transientInstance) {
		log.debug("persisting Departement instance");
		try {
			getHibernateTemplate().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Departement instance) {
		log.debug("attaching dirty Departement instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Departement instance) {
		log.debug("attaching clean Departement instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Departement persistentInstance) {
		log.debug("deleting Departement instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Departement merge(Departement detachedInstance) {
		log.debug("merging Departement instance");
		try {
			Departement result = (Departement) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Departement findById(long id) {
		log.debug("getting Departement instance with id: " + id);
		try {
			Departement instance = (Departement) getHibernateTemplate().get("Departement", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Departement> findByExample(Departement instance) {
		log.debug("finding Departement instance by example");
		try {
			List<Departement> results = (List<Departement>) getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
