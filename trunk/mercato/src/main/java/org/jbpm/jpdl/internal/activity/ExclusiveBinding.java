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

import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;
import org.jbpm.pvm.internal.util.XmlUtil;
import org.jbpm.pvm.internal.wire.binding.ObjectBinding;
import org.jbpm.pvm.internal.wire.descriptor.ExpressionDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.ObjectDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.ReferenceDescriptor;
import org.jbpm.pvm.internal.wire.xml.WireParser;
import org.jbpm.pvm.internal.xml.Parse;
import org.jbpm.pvm.internal.xml.Parser;
import org.w3c.dom.Element;


/**
 * @author Tom Baeyens
 */
public class ExclusiveBinding extends JpdlActivityBinding {

  static ObjectBinding objectBinding = new ObjectBinding();
  static WireParser wireParser = WireParser.getInstance();

  public ExclusiveBinding() {
    super("exclusive");
  }

  @SuppressWarnings("unchecked")
public Object parse(Element element, Parse parse, Parser parser) {
    if (element.hasAttribute("expr")) {
      ExclusiveExpressionActivity exclusiveExpressionActivity = new ExclusiveExpressionActivity();
      String expr = element.getAttribute("expr");
      exclusiveExpressionActivity.setExpr(expr);
      return exclusiveExpressionActivity;
    }

    if (element.hasAttribute("handler-ref")) {
      String exclusiveHandlerName = element.getAttribute("handler-ref");
      ExclusiveHandlerActivity exclusiveHandlerActivity = new ExclusiveHandlerActivity();
      exclusiveHandlerActivity.setExclusiveHandlerName(exclusiveHandlerName);
      return exclusiveHandlerActivity;
    }

    Element handlerElement = XmlUtil.element(element, "handler");
    if (handlerElement!=null) {
      ExclusiveHandlerActivity exclusiveHandlerActivity = new ExclusiveHandlerActivity();
      ObjectDescriptor exclusiveHandlerDescriptor = (ObjectDescriptor) 
          objectBinding.parse(handlerElement, parse, wireParser);
      exclusiveHandlerActivity.setExclusiveHandlerDescriptor(exclusiveHandlerDescriptor);
      return exclusiveHandlerActivity;
    }
    
    boolean hasConditions = false;
    List<Element> transitionElements = XmlUtil.elements(element, "transition");
    ActivityImpl activity = parse.findObject(ActivityImpl.class);
    List<TransitionImpl> transitions = (List) activity.getOutgoingTransitions();
    
    for (int i=0; i<transitionElements.size(); i++) {
      TransitionImpl transition = transitions.get(i);
      Element transitionElement = transitionElements.get(i);

      Element conditionElement = XmlUtil.element(transitionElement, "condition");
      if (conditionElement!=null) {
        hasConditions = true;
        
        if (conditionElement.hasAttribute("expr")) {
          String expr = conditionElement.getAttribute("expr");
          String lang = XmlUtil.attribute(conditionElement, "expr-lang");
          ExpressionDescriptor expressionDescriptor = new ExpressionDescriptor(expr, lang);
          transition.setConditionDescriptor(expressionDescriptor);
          
        } else if (conditionElement.hasAttribute("ref")) {
          String expr = conditionElement.getAttribute("ref");
          ReferenceDescriptor refDescriptor = new ReferenceDescriptor(expr);
          transition.setConditionDescriptor(refDescriptor);
          
        } else if (ObjectBinding.isObjectDescriptor(conditionElement)) {
          ObjectDescriptor conditionDescriptor = (ObjectDescriptor) objectBinding.parse(conditionElement, parse, parser);
          transition.setConditionDescriptor(conditionDescriptor);
        }
      }
    }
    
    if (hasConditions) {
      return new ExclusiveConditionActivity();
    } else {
      parse.addProblem("exclusive '"+element.getAttribute("name")+"' must have one of: expr attribute, handler attribute, handler element or condition expressions");
    }
    
    return null;
  }
}
