package com.alten.mercato.server.model;

import net.sf.gilead.pojo.java5.LightEntity;

// Generated 2009-4-17 15:31:30 by Hibernate Tools 3.2.4.CR1

/**
 * Transfert generated by hbm2java
 */
public class Transfert extends LightEntity implements java.io.Serializable {

	private long transId;
	private Long transExecId;
	private long transDepEntrId;
	private long transDepConsulId;

	public Transfert() {
	}

	public Transfert(long transId, long transDepEntrId, long transDepConsulId) {
		this.transId = transId;
		this.transDepEntrId = transDepEntrId;
		this.transDepConsulId = transDepConsulId;
	}

	public Transfert(long transId, Long transExecId, long transDepEntrId,
			long transDepConsulId) {
		this.transId = transId;
		this.transExecId = transExecId;
		this.transDepEntrId = transDepEntrId;
		this.transDepConsulId = transDepConsulId;
	}

	public long getTransId() {
		return this.transId;
	}

	public void setTransId(long transId) {
		this.transId = transId;
	}

	public Long getTransExecId() {
		return this.transExecId;
	}

	public void setTransExecId(Long transExecId) {
		this.transExecId = transExecId;
	}

	public long getTransDepEntrId() {
		return this.transDepEntrId;
	}

	public void setTransDepEntrId(long transDepEntrId) {
		this.transDepEntrId = transDepEntrId;
	}

	public long getTransDepConsulId() {
		return this.transDepConsulId;
	}

	public void setTransDepConsulId(long transDepConsulId) {
		this.transDepConsulId = transDepConsulId;
	}

}
