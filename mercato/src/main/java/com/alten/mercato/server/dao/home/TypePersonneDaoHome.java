package com.alten.mercato.server.dao.home;
// default package
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alten.mercato.server.model.TypePersonne;

/**
 * Home object for domain model class TypePersonne.
 * @see .TypePersonne
 * @author Hibernate Tools
 */
public class TypePersonneDaoHome extends HibernateDaoSupport{

	private static final Log log = LogFactory.getLog(TypePersonneDaoHome.class);


	public void persist(TypePersonne transientInstance) {
		log.debug("persisting TypePersonne instance");
		try {
			getHibernateTemplate().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TypePersonne instance) {
		log.debug("attaching dirty TypePersonne instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TypePersonne instance) {
		log.debug("attaching clean TypePersonne instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TypePersonne persistentInstance) {
		log.debug("deleting TypePersonne instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TypePersonne merge(TypePersonne detachedInstance) {
		log.debug("merging TypePersonne instance");
		try {
			TypePersonne result = (TypePersonne) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TypePersonne findById(long id) {
		log.debug("getting TypePersonne instance with id: " + id);
		try {
			TypePersonne instance = (TypePersonne) getHibernateTemplate().get("TypePersonne", id);
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

	public List<TypePersonne> findByExample(TypePersonne instance) {
		log.debug("finding TypePersonne instance by example");
		try {
			List<TypePersonne> results = (List<TypePersonne>) getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
