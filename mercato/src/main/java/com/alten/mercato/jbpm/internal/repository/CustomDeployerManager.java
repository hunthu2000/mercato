/**
 * 
 */
package com.alten.mercato.jbpm.internal.repository;

import java.util.List;

import org.jbpm.pvm.internal.repository.Deployer;
import org.jbpm.pvm.internal.repository.DeploymentImpl;

/**
 * @author Huage Chen
 *
 */
public class CustomDeployerManager {
	private List<Deployer> deployers;

	public void deploy(DeploymentImpl deployment) {
		for (Deployer deployer: deployers) {
			deployer.deploy(deployment);
		}
	}
	
	public void setDeployers(List<Deployer> deployers) {
	    this.deployers = deployers;
	 }
}
