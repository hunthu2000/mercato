/**
 * 
 */
package com.alten.mercato.jbpm.internal.repository;

import java.io.InputStream;
import java.util.ArrayList;

import org.hibernate.Session;
import org.jbpm.Deployment;
import org.jbpm.JbpmException;
import org.jbpm.Problem;
import org.jbpm.ProcessDefinitionQuery;
import org.jbpm.client.ClientProcessDefinition;
import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.query.ProcessDefinitionQueryImpl;
import org.jbpm.pvm.internal.repository.DeploymentImpl;
import org.jbpm.pvm.internal.repository.DeploymentProperty;
import org.jbpm.pvm.internal.repository.RepositoryCache;
import org.jbpm.pvm.internal.repository.RepositorySessionImpl;
import org.jbpm.session.RepositorySession;

/**
 * @author Huage Chen
 *
 */
public class CustomRepositoySessionImpl implements RepositorySession {

	private static Log log = Log.getLog(RepositorySessionImpl.class.getName());

	protected Session session;
	protected RepositoryCache repositoryCache;
	protected CustomDeployerManager deployerManager;
	public void setSession(Session session) {
		this.session = session;
	}

	public void setRepositoryCache(RepositoryCache repositoryCache) {
		this.repositoryCache = repositoryCache;
	}

	public void setDeployerManager(CustomDeployerManager deployerManager) {
		this.deployerManager = deployerManager;
	}

	public long deploy(Deployment deployment) {
		DeploymentImpl deploymentImpl = (DeploymentImpl) deployment;
		session.save(deploymentImpl);

		deploymentImpl.setProblems(new ArrayList<Problem>());

		deployerManager.deploy(deploymentImpl);

		if (deploymentImpl.hasProblems()) {
			for (Problem problem: deploymentImpl.getProblems()) {
				Throwable cause = problem.getCause();
				if (cause!=null) {
					log.debug("deployment exception", cause);
				}
			}
			throw new JbpmException("problems during deployment: "+deploymentImpl.getProblemsText());
		} else {

			repositoryCache.set(deploymentImpl.getDbid(), deploymentImpl.getObjects());
		}

		return deploymentImpl.getDbid();
	}

	public DeploymentImpl getDeployment(long deploymentDbid) {
		return (DeploymentImpl) session.get(DeploymentImpl.class, deploymentDbid);
	}

	public Object getObject(long deploymentDbid, String objectName) {
		Object object = repositoryCache.get(deploymentDbid, objectName);
		if (object!=null) {
			log.trace("repository cache hit");

		} else {
			log.trace("loading deployment "+deploymentDbid+" from db");
			DeploymentImpl deployment = (DeploymentImpl) session.load(DeploymentImpl.class, deploymentDbid);
			deploy(deployment);

			object = repositoryCache.get(deploymentDbid, objectName);

			if (object==null) {
				throw new JbpmException("deployment "+deploymentDbid+" doesn't contain object "+objectName);
			}
		}
		return object;
	}

	public InputStream getResourceAsStream(long deploymentDbid, String resourceName) {
		DeploymentImpl deployment = getDeployment(deploymentDbid);
		if (deployment==null) {
			throw new JbpmException("deployment "+deploymentDbid+" doesn't exist");
		}
		return deployment.getResourceAsStream(resourceName);
	}

	// queries //////////////////////////////////////////////////////////////////

	public ProcessDefinitionQuery createProcessDefinitionQuery() {
		return new ProcessDefinitionQueryImpl(session);
	}

	public ClientProcessDefinition loadProcessDefinitionById(String processDefinitionId) {
		DeploymentProperty deploymentProperty = (DeploymentProperty) session.createQuery(
				"select deploymentProperty " +
				"from "+DeploymentProperty.class.getName()+" as deploymentProperty " +
				"where deploymentProperty.key = 'id' " +
				"  and deploymentProperty.stringValue = '"+processDefinitionId+"' "
		).setMaxResults(1).uniqueResult();

		if (deploymentProperty!=null) {
			long deploymentDbid = deploymentProperty.getDeployment().getDbid();
			String objectName = deploymentProperty.getObjectName();

			return (ClientProcessDefinition) getObject(deploymentDbid, objectName);
		}

		return null;
	}

}
