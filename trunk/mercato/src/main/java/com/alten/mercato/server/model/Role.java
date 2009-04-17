package com.alten.mercato.server.model;

// Generated 2009-4-17 15:31:30 by Hibernate Tools 3.2.4.CR1

import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.pojo.java5.LightEntity;

/**
 * Role generated by hbm2java
 */
public class Role extends LightEntity implements java.io.Serializable {

	private long roleId;
	private String roleLib;
	private Set<UtilRole> utilRoles = new HashSet<UtilRole>(0);

	public Role() {
	}

	public Role(long roleId, String roleLib) {
		this.roleId = roleId;
		this.roleLib = roleLib;
	}

	public Role(long roleId, String roleLib, Set<UtilRole> utilRoles) {
		this.roleId = roleId;
		this.roleLib = roleLib;
		this.utilRoles = utilRoles;
	}

	public long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public String getRoleLib() {
		return this.roleLib;
	}

	public void setRoleLib(String roleLib) {
		this.roleLib = roleLib;
	}

	public Set<UtilRole> getUtilRoles() {
		return this.utilRoles;
	}

	public void setUtilRoles(Set<UtilRole> utilRoles) {
		this.utilRoles = utilRoles;
	}

}
