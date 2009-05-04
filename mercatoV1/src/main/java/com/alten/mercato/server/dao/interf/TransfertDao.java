package com.alten.mercato.server.dao.interf;

import java.util.List;

import com.alten.mercato.server.model.Transfert;




/**
 * @author Huage Chen
 */
public interface TransfertDao {


	public void persist(Transfert transientInstance);
	
	public void attachDirty(Transfert instance);
	
	public void attachClean(Transfert instance);
	
	public void delete(Transfert persistentInstance);
	
	public Transfert merge(Transfert detachedInstance);
	
	public Transfert findById(long id);
	
	public List<Transfert> findByExample(Transfert instance);

}
