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
	private String commentHR1;
	private String commentHR2;
	


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
	
	public InfoTransfer(Personne personne, String transferStatus,
			String commentHR1, String commentHR2) {
		super();
		this.personne = personne;
		this.transferStatus = transferStatus;
		this.commentHR1 = commentHR1;
		this.commentHR2 = commentHR2;
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

	/**
	 * @return the commentHR1
	 */
	public String getCommentHR1() {
		return commentHR1;
	}

	/**
	 * @param commentHR1 the commentHR1 to set
	 */
	public void setCommentHR1(String commentHR1) {
		this.commentHR1 = commentHR1;
	}

	/**
	 * @return the commentHR2
	 */
	public String getCommentHR2() {
		return commentHR2;
	}

	/**
	 * @param commentHR2 the commentHR2 to set
	 */
	public void setCommentHR2(String commentHR2) {
		this.commentHR2 = commentHR2;
	}	

}
