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

import org.jboss.soa.esb.client.ServiceInvoker;
import org.jboss.soa.esb.message.Body;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.format.MessageFactory;
import org.jbpm.activity.ActivityExecution;
import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.model.ExpressionEvaluator;
import org.jbpm.pvm.internal.wire.Descriptor;
import org.jbpm.pvm.internal.wire.WireContext;
import org.jbpm.pvm.internal.wire.descriptor.ListDescriptor;


/**
 * @author Tom Baeyens
 */
public class EsbActivity extends JpdlActivity {

  private static final long serialVersionUID = 1L;
  
  private static Log log = Log.getLog(EsbActivity.class.getName());

  protected String category = null;
  protected String service = null;
  protected ListDescriptor partsListDescriptor = null;

  public void execute(ActivityExecution execution) throws Exception {
    Message message = MessageFactory.getInstance().getMessage();
    Body body = message.getBody();
    
    if (partsListDescriptor!=null) {
      WireContext wireContext = new WireContext();
      for (Descriptor descriptor: partsListDescriptor.getValueDescriptors()) {
        String name = descriptor.getName();
        
        Object object = wireContext.create(descriptor, true);
        if (object instanceof ExpressionEvaluator) {
          ExpressionEvaluator expressionEvaluator = (ExpressionEvaluator) object;
          object = expressionEvaluator.evaluateExpression(execution);
        }
        body.add(name, object);
      }
    }
    
    ServiceInvoker invoker = new ServiceInvoker(category, service);
    log.debug("sending "+message.getBody()+" to service "+service+" in category "+category+" over the esb");
    invoker.deliverAsync(message);
  }

  public void setCategory(String category) {
    this.category = category;
  }
  public void setService(String service) {
    this.service = service;
  }
  public void setPartsListDescriptor(ListDescriptor partsListDescriptor) {
    this.partsListDescriptor = partsListDescriptor;
  }
}
