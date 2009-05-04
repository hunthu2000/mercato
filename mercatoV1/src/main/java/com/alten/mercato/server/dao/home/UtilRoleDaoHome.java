package com.alten.mercato.server.dao.home;
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alten.mercato.server.model.UtilRole;

/**
 * Home object for domain model class UtilRole.
 * @see .UtilRole
 * @author Hibernate Tools
 */
public class UtilRoleDaoHome extends HibernateDaoSupport{

	private static final Log log = LogFactory.getLog(UtilRoleDaoHome.class);


	public void persist(UtilRole transientInstance) {
		log.debug("persisting UtilRole instance");
		try {
			getHibernateTemplate().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UtilRole instance) {
		log.debug("attaching dirty UtilRole instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(UtilRole instance) {
		log.debug("attaching clean UtilRole instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(UtilRole persistentInstance) {
		log.debug("deleting UtilRole instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UtilRole merge(UtilRole detachedInstance) {
		log.debug("merging UtilRole instance");
		try {
			UtilRole result = (UtilRole) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UtilRole findById(long id) {
		log.debug("getting UtilRole instance with id: " + id);
		try {
			UtilRole instance = (UtilRole) getHibernateTemplate().get(UtilRole.class, id);
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
	public List<UtilRole> findByExample(UtilRole instance) {
		log.debug("finding UtilRole instance by example");
		try {
			List<UtilRole> results = (List<UtilRole>) getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
