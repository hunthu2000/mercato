package com.alten.mercato.server.dao.home;
// default package
// Generated 2009-4-10 15:33:20 by Hibernate Tools 3.2.4.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

import com.alten.mercato.server.model.TypePersonne;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class TypePersonne.
 * @see .TypePersonne
 * @author Hibernate Tools
 */
public class TypePersonneDaoHome {

	private static final Log log = LogFactory.getLog(TypePersonneDaoHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(TypePersonne transientInstance) {
		log.debug("persisting TypePersonne instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TypePersonne instance) {
		log.debug("attaching dirty TypePersonne instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TypePersonne instance) {
		log.debug("attaching clean TypePersonne instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TypePersonne persistentInstance) {
		log.debug("deleting TypePersonne instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TypePersonne merge(TypePersonne detachedInstance) {
		log.debug("merging TypePersonne instance");
		try {
			TypePersonne result = (TypePersonne) sessionFactory
					.getCurrentSession().merge(detachedInstance);
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
			TypePersonne instance = (TypePersonne) sessionFactory
					.getCurrentSession().get("TypePersonne", id);
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
			List<TypePersonne> results = (List<TypePersonne>) sessionFactory
					.getCurrentSession().createCriteria("TypePersonne").add(
							create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
