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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.Execution;
import org.jbpm.activity.ActivityExecution;
import org.jbpm.model.Activity;
import org.jbpm.model.OpenExecution;
import org.jbpm.model.Transition;


/**
 * @author Tom Baeyens
 */
public class JoinActivity extends JpdlActivity {

  private static final long serialVersionUID = 1L;

  public void execute(ActivityExecution execution) throws Exception {
    // end the child execution execution
    // this will also remove the execution from it's parent
    execution.setState(Execution.STATE_INACTIVE);
    execution.waitForSignal();
    
    Activity join = execution.getActivity();
    List<OpenExecution> joinedExecutions = findJoinedExecutions(execution, join);
    
    if (isComplete(joinedExecutions, join)) {
      endJoinedExecutions(joinedExecutions, execution);

      OpenExecution processInstance = execution.getProcessInstance();
      
      Execution outgoingExecution = null;
      if ( processInstance.getExecutions()==null
           || processInstance.getExecutions().isEmpty() 
         ) {
        outgoingExecution = processInstance;
      } else {
        outgoingExecution = execution.createExecution(processInstance);
      }
      
      execution.setActivity(join, outgoingExecution);
      Transition transition = join.getDefaultTransition();
      execution.take(transition, outgoingExecution);
    }
  }
  
  List<OpenExecution> findJoinedExecutions(OpenExecution execution, Activity join) {
    List<OpenExecution> joinedExecutions = new ArrayList<OpenExecution>();
    scanRecursive(execution.getProcessInstance(), join, joinedExecutions);
    return joinedExecutions;
  }

  void scanRecursive(OpenExecution execution, Activity join, List<OpenExecution> joinedExecutions) {
    // if the execution is positioned in the join
    if (join.equals(execution.getActivity())) {
      joinedExecutions.add(execution);
    }
    Collection<OpenExecution> childExecutions = execution.getExecutions();
    if (childExecutions!=null) {
      for (OpenExecution childExecution: childExecutions) {
        scanRecursive(childExecution, join, joinedExecutions);
      }
    }
  }

  boolean isComplete(List<OpenExecution> joinedExecutions, Activity join) {
    int executionsToJoin = join.getIncomingTransitions().size();
    return (executionsToJoin==joinedExecutions.size());
  }

  void endJoinedExecutions(List<OpenExecution> joinedExecutions, ActivityExecution execution) {
    for (OpenExecution joinedExecution: joinedExecutions) {
      execution.end(joinedExecution);
    }
  }

}
