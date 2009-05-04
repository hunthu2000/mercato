package com.alten.mercato.server.dao.home;
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alten.mercato.server.model.Transfert;

/**
 * Home object for domain model class Departement.
 * @see .Departement
 * @author Hibernate Tools
 */
public class TransfertDaoHome extends HibernateDaoSupport{

	private static final Log log = LogFactory.getLog(TransfertDaoHome.class);



	public void persist(Transfert transientInstance) {
		log.debug("persisting Transfert instance");
		try {
			getHibernateTemplate().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Transfert instance) {
		log.debug("attaching dirty Transfert instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Transfert instance) {
		log.debug("attaching clean Transfert instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Transfert persistentInstance) {
		log.debug("deleting Transfert instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Transfert merge(Transfert detachedInstance) {
		log.debug("merging Transfert instance");
		try {
			Transfert result = (Transfert) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Transfert findById(long id) {
		log.debug("getting Transfert instance with id: " + id);
		try {
			Transfert instance = (Transfert) getHibernateTemplate().get(Transfert.class, id);
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
	public List<Transfert> findByExample(Transfert instance) {
		log.debug("finding Departement instance by example");
		try {
			List<Transfert> results = (List<Transfert>) getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
