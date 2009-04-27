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

import org.jbpm.JbpmException;
import org.jbpm.activity.ActivityExecution;
import org.jbpm.env.Environment;
import org.jbpm.jpdl.ExclusiveHandler;
import org.jbpm.model.Activity;
import org.jbpm.model.Transition;
import org.jbpm.pvm.internal.wire.Descriptor;
import org.jbpm.pvm.internal.wire.WireContext;

/**
 * @author Tom Baeyens
 */
public class ExclusiveHandlerActivity extends JpdlActivity {

  private static final long serialVersionUID = 1L;
  
  protected String exclusiveHandlerName;
  protected Descriptor exclusiveHandlerDescriptor;

  public void execute(ActivityExecution execution) throws Exception {
    Activity activity = execution.getActivity();
    String transitionName = null;

    Object object = null; 
    if (exclusiveHandlerDescriptor!=null) {
      object = WireContext.create(exclusiveHandlerDescriptor);
    } else if (exclusiveHandlerName!=null) {
      Environment environment = Environment.getCurrent();
      object = environment.get(exclusiveHandlerName);
    }
    
    if (object==null) {
      throw new JbpmException("exclusive handler for "+activity+" is null");
    }
    if (! (object instanceof ExclusiveHandler)) {
      throw new JbpmException("handler for exclusive is not a "+ExclusiveHandler.class.getName()+": "+object.getClass().getName());
    }
    ExclusiveHandler exclusiveHandler = (ExclusiveHandler) object;
    transitionName = exclusiveHandler.select(execution);

    Transition transition = activity.getOutgoingTransition(transitionName);
    if (transition==null) {
      throw new JbpmException("handler in exclusive '"+activity.getName()+"' returned unexisting outgoing transition name: "+transitionName);
    }
    
    execution.historyExclusive(transitionName);

    execution.take(transition);
  }

  public void setExclusiveHandlerName(String exclusiveHandlerName) {
    this.exclusiveHandlerName = exclusiveHandlerName;
  }
  public void setExclusiveHandlerDescriptor(Descriptor exclusiveHandlerDescriptor) {
    this.exclusiveHandlerDescriptor = exclusiveHandlerDescriptor;
  }
}
