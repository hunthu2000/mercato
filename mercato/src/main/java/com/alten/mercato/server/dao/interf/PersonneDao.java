package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.Personne;


/**
 * @author Huage Chen
 *
 */
public interface PersonneDao {
	
	public void persist(Personne transientInstance);
	
	public void attachDirty(Personne instance);
	
	public void attachClean(Personne instance);
	
	public void delete(Personne persistentInstance);
	
	public Personne merge(Personne detachedInstance);
	
	public Personne findById(long id);
	
	public List<Personne> findByExample(Personne instance);
}
