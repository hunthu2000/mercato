package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.Departement;




/**
 * @author Huage Chen
 */
public interface DepartementDao {


	public void persist(Departement transientInstance);
	
	public void attachDirty(Departement instance);
	
	public void attachClean(Departement instance);
	
	public void delete(Departement persistentInstance);
	
	public Departement merge(Departement detachedInstance);
	
	public Departement findById(long id);
	
	public List<Departement> findByExample(Departement instance);

}
