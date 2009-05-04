/**
 * 
 */
package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.UtilRole;

/**
 * @author Huage Chen
 *
 */
public interface UtilRoleDao {
	public void persist(UtilRole transientInstance);

	public void attachDirty(UtilRole instance);

	public void attachClean(UtilRole instance);

	public void delete(UtilRole persistentInstance);

	public UtilRole merge(UtilRole detachedInstance);

	public UtilRole findById(long id);

	public List<UtilRole> findByExample(UtilRole instance);
}
