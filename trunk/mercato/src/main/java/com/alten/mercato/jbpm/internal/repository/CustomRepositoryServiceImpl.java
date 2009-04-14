/**
 * 
 */
package com.alten.mercato.jbpm.internal.repository;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.io.InputStream;

import org.jbpm.Deployment;
import org.jbpm.ProcessDefinitionQuery;
import org.jbpm.RepositoryService;
import org.jbpm.cmd.CommandService;
import org.jbpm.pvm.internal.cmd.DeleteDeploymentCmd;
import org.jbpm.pvm.internal.cmd.GetResourceAsStreamCmd;
import org.jbpm.pvm.internal.query.ProcessDefinitionQueryImpl;
import org.jbpm.pvm.internal.repository.DeploymentImpl;

/**
 * @author Tom Baeyens
 */
public class CustomRepositoryServiceImpl implements RepositoryService {
  
  CommandService commandService;
  
  public Deployment createDeployment() {
    return new DeploymentImpl(commandService);
  }
  
  public void deleteDeployment(long deploymentDbid) {
    commandService.execute(new DeleteDeploymentCmd(deploymentDbid));
  }

  public void deleteDeploymentCascade(long deploymentDbid) {
    commandService.execute(new DeleteDeploymentCmd(deploymentDbid, true));
  }

  public InputStream getResourceAsStream(long deploymentDbid, String resource) {
    return commandService.execute(new GetResourceAsStreamCmd(deploymentDbid, resource));
  }

  public ProcessDefinitionQuery createProcessDefinitionQuery() {
    return new ProcessDefinitionQueryImpl(commandService);
  }
  
  public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}

}