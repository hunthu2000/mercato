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
package org.jbpm.jpdl.internal.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.jbpm.JbpmException;
import org.jbpm.env.Environment;
import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.script.ScriptManager;
import org.jbpm.pvm.internal.task.AssignableDefinitionImpl;
import org.jbpm.pvm.internal.task.ParticipationImpl;
import org.jbpm.pvm.internal.task.SwimlaneDefinitionImpl;
import org.jbpm.pvm.internal.task.SwimlaneImpl;
import org.jbpm.pvm.internal.task.TaskDefinitionImpl;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.jbpm.pvm.internal.wire.Descriptor;
import org.jbpm.pvm.internal.wire.WireContext;
import org.jbpm.session.TaskDbSession;
import org.jbpm.task.Assignable;
import org.jbpm.task.AssignmentHandler;

/**
 * @author Tom Baeyens
 */
public class JpdlExecution extends ExecutionImpl implements TaskExtension {

  private static final long serialVersionUID = 1L;
  
  private static Log log = Log.getLog(JpdlExecution.class.getName());
  
  protected Map<String, SwimlaneImpl> swimlanes;

  public <T> T getExtension(Class<T> extensionClass) {
    if (extensionClass==null) {
      throw new JbpmException("extensionClass is null");
    }
    if (extensionClass.equals(JpdlExecution.class)) {
      Session session = Environment.getFromCurrent(Session.class);
      return (T) session.load(JpdlExecution.class, dbid);
    }
    throw new JbpmException("unsuppported extension "+extensionClass.getName());
  }
  
  // tasks ////////////////////////////////////////////////////////////////////

  public TaskImpl createTask(TaskDefinitionImpl taskDefinition) {
    TaskDbSession taskDbSession = Environment.getFromCurrent(TaskDbSession.class);
    TaskImpl task = (TaskImpl) taskDbSession.createTask();
    task.setTaskDefinition(taskDefinition);
    task.setExecution(this);
    task.setProcessInstance(processInstance);
    task.setSignalling(true);
    
    // initialize the name
    if (taskDefinition.getName()!=null) {
      task.setName(taskDefinition.getName());
    } else {
      task.setName(getActivityName());
    }

    task.setDescription(taskDefinition.getDescription());
    task.setPriority(taskDefinition.getPriority());
    
    SwimlaneDefinitionImpl swimlaneDefinition = taskDefinition.getSwimlaneDefinition();
    if (swimlaneDefinition!=null) {
      JpdlExecution jpdlProcessInstance = processInstance.getExtension(JpdlExecution.class); 
      SwimlaneImpl swimlane = jpdlProcessInstance.getInitializedSwimlane(swimlaneDefinition);
      task.setSwimlane(swimlane);
      
      // copy the swimlane assignments to the task
      task.setAssignee(swimlane.getAssignee());
      for (ParticipationImpl participant: swimlane.getParticipations()) {
        task.addParticipation(participant.getUserId(), participant.getGroupId(), participant.getType());
      }
    }

    initializeAssignments(taskDefinition, task);
    
    return task;
  }

  /** tasks and swimlane assignment.
   * SwimlaneDefinitionImpl is base class for TaskDefinitionImpl.
   * Both Task and Swimlane implement Assignable. */
  public void initializeAssignments(AssignableDefinitionImpl assignableDefinition, Assignable assignable) {
    String assigneeExpression = assignableDefinition.getAssigneeExpression();
    if (assigneeExpression!=null) {
      String assignee = resolveAssignmentExpression(assigneeExpression, 
                                                    assignableDefinition.getAssigneeExpressionLanguage());
      assignable.setAssignee(assignee);
      
      if (log.isTraceEnabled()) log.trace("task "+name+" assigned to "+assignee+" using expression "+assigneeExpression);
    }
    
    String candidateUsersExpression = assignableDefinition.getCandidateUsersExpression();
    if (candidateUsersExpression!=null) {
      String candidateUsers = 
          resolveAssignmentExpression(candidateUsersExpression, 
                                      assignableDefinition.getCandidateUsersExpressionLanguage());
      StringTokenizer tokenizer = new StringTokenizer(candidateUsers, ",");
      while (tokenizer.hasMoreTokens()) {
        String candidateUser = tokenizer.nextToken().trim();
        assignable.addCandidateUser(candidateUser);
      }
    }
  
    String candidateGroupsExpression = assignableDefinition.getCandidateGroupsExpression();
    if (candidateGroupsExpression!=null) {
      String candidateGroups = 
            resolveAssignmentExpression(candidateGroupsExpression, 
                                        assignableDefinition.getCandidateGroupsExpressionLanguage());
      StringTokenizer tokenizer = new StringTokenizer(candidateGroups, ",");
      while (tokenizer.hasMoreTokens()) {
        String candidateGroup = tokenizer.nextToken();
        assignable.addCandidateGroup(candidateGroup);
      }
    }
    
    Descriptor assignmentHandlerDescriptor = assignableDefinition.getAssignmentHandlerDescriptor();
    if (assignmentHandlerDescriptor!=null) {
      AssignmentHandler assignmentHandler = (AssignmentHandler) WireContext.create(assignmentHandlerDescriptor);
      try {
        assignmentHandler.assign(assignable, this);
      } catch (Exception e) {
        throw new JbpmException("assignment handler threw exception: "+e, e);
      }
    }
  }

  protected String resolveAssignmentExpression(String expression, String expressionLanguage) {
    ScriptManager scriptManager = Environment.getFromCurrent(ScriptManager.class);
    Object result = scriptManager.evaluateExpression(expression, this, expressionLanguage);
    if ( (result ==null)
         || (result instanceof String)
       ) {
      return (String) result;
    }
    throw new JbpmException("result of assignment expression "+expression+" is "+result+" ("+result.getClass().getName()+") instead of String");
  }
  
  // swimlanes ////////////////////////////////////////////////////////////////
  
  public void addSwimlane(SwimlaneImpl swimlane) {
    if (swimlanes==null) {
      swimlanes = new HashMap<String, SwimlaneImpl>();
    }
    swimlanes.put(swimlane.getName(), swimlane);
    swimlane.setExecution(this);
  }
  
  public void removeSwimlane(SwimlaneImpl swimlane) {
    swimlanes.remove(swimlane.getName());
    swimlane.setExecution(null);
  }

  public SwimlaneImpl getInitializedSwimlane(SwimlaneDefinitionImpl swimlaneDefinition) {
    String swimlaneName = swimlaneDefinition.getName();

    if (swimlanes==null) {
      swimlanes = new HashMap<String, SwimlaneImpl>();
    }

    SwimlaneImpl swimlane = swimlanes.get(swimlaneName);
    if (swimlane==null) {
      // initialize swimlane
      swimlane = new SwimlaneImpl();
      swimlane.setName(swimlaneName);
      swimlane.setExecution(this);
      swimlanes.put(swimlaneName, swimlane);
      initializeAssignments(swimlaneDefinition, swimlane);
    }

    return swimlane;
  }
}
