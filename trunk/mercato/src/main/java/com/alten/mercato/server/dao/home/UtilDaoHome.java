package com.alten.mercato.server.dao.home;
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.alten.mercato.server.model.Util;


import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Util.
 * @see .Util
 * @author Hibernate Tools
 */
public class UtilDaoHome extends HibernateDaoSupport{

	private static final Log log = LogFactory.getLog(UtilDaoHome.class);



	public void persist(Util transientInstance) {
		log.debug("persisting Util instance");
		try {
			getHibernateTemplate().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Util instance) {
		log.debug("attaching dirty Util instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Util instance) {
		log.debug("attaching clean Util instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Util persistentInstance) {
		log.debug("deleting Util instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Util merge(Util detachedInstance) {
		log.debug("merging Util instance");
		try {
			Util result = (Util) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Util findById(long id) {
		log.debug("getting Util instance with id: " + id);
		try {
			Util instance = (Util) getHibernateTemplate().get(
					"Util", id);
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

	public List<Util> findByExample(Util instance) {
		log.debug("finding Util instance by example");
		try {
			List<Util> results = (List<Util>) getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
