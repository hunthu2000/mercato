/**
 * 
 */
package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.TypePersonne;

/**
 * @author Huage Chen
 *
 */
public interface TypePersonneDao {
	public void persist(TypePersonne transientInstance);

	public void attachDirty(TypePersonne instance);

	public void attachClean(TypePersonne instance);

	public void delete(TypePersonne persistentInstance);

	public TypePersonne merge(TypePersonne detachedInstance);

	public TypePersonne findById(long id);

	public List<TypePersonne> findByExample(TypePersonne instance);
}
