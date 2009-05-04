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

import java.lang.reflect.Method;
import java.util.List;

import org.jbpm.activity.ActivityExecution;
import org.jbpm.pvm.internal.util.ReflectUtil;
import org.jbpm.pvm.internal.wire.Descriptor;
import org.jbpm.pvm.internal.wire.WireContext;
import org.jbpm.pvm.internal.wire.WireException;
import org.jbpm.pvm.internal.wire.descriptor.ArgDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.ObjectDescriptor;
import org.jbpm.pvm.internal.wire.operation.InvokeOperation;


/**
 * @author Tom Baeyens
 */
public class JavaActivity extends JpdlActivity {

  private static final long serialVersionUID = 1L;
  
  protected Descriptor descriptor;
  protected String methodName;
  protected String variableName;
  protected InvokeOperation invokeOperation;
  
  public void execute(ActivityExecution execution) throws Exception {
    WireContext wireContext = new WireContext();
    Object target = wireContext.create(descriptor, false);

    try {
      List<ArgDescriptor> argDescriptors = null;
      Object[] args = null;
      if (invokeOperation!=null) {
        argDescriptors = invokeOperation.getArgDescriptors();
        args = ObjectDescriptor.getArgs(wireContext, argDescriptors);
      }
      
      Class<?> clazz = target.getClass();
      Method method = ReflectUtil.findMethod(clazz, methodName, argDescriptors, args);
      if (method==null) {
        throw new WireException("method "+ReflectUtil.getSignature(methodName, argDescriptors, args)+" unavailable");
      }

      Object returnValue = ReflectUtil.invoke(method, target, args);
      
      if (variableName!=null) {
        execution.setVariable(variableName, returnValue);
      }
      
      execution.historyAutomatic();

    } catch (WireException e) {
      throw e;
    } catch (Exception e) {
      throw new WireException("couldn't invoke method "+methodName+": "+e.getMessage(), e);
    }
  }

  public void setDescriptor(Descriptor descriptor) {
    this.descriptor = descriptor;
  }
  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }
  public void setInvokeOperation(InvokeOperation invokeOperation) {
    this.invokeOperation = invokeOperation;
  }
}
