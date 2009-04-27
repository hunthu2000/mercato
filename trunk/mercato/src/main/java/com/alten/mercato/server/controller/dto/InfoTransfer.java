/**
 * 
 */
package com.alten.mercato.server.controller.dto;

import net.sf.gilead.pojo.java5.LightEntity;

import com.alten.mercato.server.model.Personne;

/**
 * @author Huage Chen
 *
 */
public class InfoTransfer extends LightEntity implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3525775135941537520L;
	
	private Personne personne;
	private String transferStatus;
	
	public InfoTransfer() {
		super();
	}
	
	/**
	 * @param personne
	 * @param transferStatus
	 */
	public InfoTransfer(Personne personne, String transferStatus) {
		super();
		this.personne = personne;
		this.transferStatus = transferStatus;
	}
	
	/**
	 * @return the personne
	 */
	public Personne getPersonne() {
		return personne;
	}

	/**
	 * @param personne the personne to set
	 */
	public void setPersonne(Personne personne) {
		this.personne = personne;
	}

	/**
	 * @return the transferStatus
	 */
	public String getTransferStatus() {
		return transferStatus;
	}

	/**
	 * @param transferStatus the transferStatus to set
	 */
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}

	

	
}
