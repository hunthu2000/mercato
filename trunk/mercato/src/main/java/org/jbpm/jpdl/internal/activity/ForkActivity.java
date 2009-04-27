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

import java.util.List;

import org.jbpm.Execution;
import org.jbpm.activity.ActivityExecution;
import org.jbpm.model.Activity;
import org.jbpm.model.OpenExecution;
import org.jbpm.model.Transition;


/**
 * @author Tom Baeyens
 */
public class ForkActivity extends JpdlActivity {

  private static final long serialVersionUID = 1L;
  
  public void execute(ActivityExecution execution) throws Exception {
    OpenExecution processInstance = execution.getProcessInstance();

    Activity activity = execution.getActivity();
    List<Transition> outgoingTransitions = activity.getOutgoingTransitions();

    // for each outgoing transition
    for (Transition outgoingTransition: outgoingTransitions) {
      // launch a concurrent path of execution
      String childExecutionName = outgoingTransition.getName();
      // creating the execution will cause the execution to become inactive
      Execution childExecution = execution.createExecution(childExecutionName, processInstance);
      execution.take(outgoingTransition, childExecution);
    }

    // if this was the first fork
    if (execution.isProcessInstance()) {
      execution.setActivity(null);
    }
  }
}
