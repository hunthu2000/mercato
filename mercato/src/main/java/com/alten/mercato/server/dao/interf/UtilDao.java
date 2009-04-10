/**
 * 
 */
package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.Util;

/**
 * @author Huage Chen
 *
 */
public interface UtilDao {
	public void persist(Util transientInstance);

	public void attachDirty(Util instance);

	public void attachClean(Util instance);

	public void delete(Util persistentInstance);

	public Util merge(Util detachedInstance);

	public Util findById(long id);

	public List<Util> findByExample(Util instance);
}
