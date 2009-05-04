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
package org.jbpm.jpdl.internal.activity;

import java.util.Map;

import org.jbpm.JbpmException;
import org.jbpm.activity.ActivityExecution;
import org.jbpm.env.Environment;
import org.jbpm.jpdl.internal.model.JpdlExecution;
import org.jbpm.model.Activity;
import org.jbpm.model.Transition;
import org.jbpm.pvm.internal.task.TaskDefinitionImpl;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.jbpm.session.TaskDbSession;
import org.jbpm.task.TaskHandler;


/**
 * @author Tom Baeyens
 */
public class TaskActivity extends JpdlExternalActivity {

  private static final long serialVersionUID = 1L;

  protected TaskDefinitionImpl taskDefinition;
  
  public void execute(ActivityExecution execution) {
    JpdlExecution jpdlExecution = execution.getExtension(JpdlExecution.class);
    TaskImpl task = jpdlExecution.createTask(taskDefinition);

    TaskHandler taskHandler = task.getTaskHandler();
    boolean wait = taskHandler.executionCreateTask(task);

    TaskDbSession taskDbSession = Environment.getFromCurrent(TaskDbSession.class);
    taskDbSession.save(task);
    
    if (wait) {
      execution.waitForSignal();
    }
  }
  
  public void signal(ActivityExecution execution, String signalName, Map<String, Object> parameters) {
    Activity activity = execution.getActivity();
    
    if (parameters!=null) {
      execution.setVariables(parameters);
    }
    
    execution.fire(signalName, activity);

    TaskDbSession taskDbSession = Environment.getFromCurrent(TaskDbSession.class);
    TaskImpl task = (TaskImpl) taskDbSession.findTaskByExecution(execution);
    TaskHandler taskHandler = task.getTaskHandler();
    taskHandler.executionSignal(task);
    
    Transition transition = activity.findOutgoingTransition(signalName);
    if (transition!=null) {
      execution.take(transition);
    } else {
      throw new JbpmException("task outcome '"+signalName+"' doesn't match with the an outgoing transition "+activity.getOutgoingTransitions());
    }
  }

  public TaskDefinitionImpl getTaskDefinition() {
    return taskDefinition;
  }
  public void setTaskDefinition(TaskDefinitionImpl taskDefinition) {
    this.taskDefinition = taskDefinition;
  }
}