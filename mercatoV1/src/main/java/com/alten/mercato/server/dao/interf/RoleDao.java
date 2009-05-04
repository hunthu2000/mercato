/**
 * 
 */
package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.Role;

/**
 * @author Huage Chen
 *
 */
public interface RoleDao {
	public void persist(Role transientInstance);

	public void attachDirty(Role instance);

	public void attachClean(Role instance);

	public void delete(Role persistentInstance);

	public Role merge(Role detachedInstance);

	public Role findById(long id);

	public List<Role> findByExample(Role instance);
}
