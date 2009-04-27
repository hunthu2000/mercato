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

import org.jbpm.jpdl.internal.xml.JpdlParser;
import org.jbpm.jpdl.internal.xml.UnresolvedTransitions;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;
import org.jbpm.pvm.internal.util.TagBinding;
import org.jbpm.pvm.internal.util.XmlUtil;
import org.jbpm.pvm.internal.wire.xml.WireParser;
import org.jbpm.pvm.internal.xml.Parse;
import org.w3c.dom.Element;


/**
 * @author Tom Baeyens
 */
public abstract class JpdlActivityBinding extends TagBinding {
  
  protected static final WireParser wireParser = JpdlParser.wireParser;

  public JpdlActivityBinding(String tagName) {
    super(tagName, "http://jbpm.org/4/jpdl", "activity");
  }

  public void parseName(Element element, ActivityImpl activity, Parse parse) {
    String name = XmlUtil.attribute(element, "name", isNameRequired(), parse);
    
    if (name!=null) {
      // basic name validation
      if ("".equals(name)) {
        parse.addProblem(XmlUtil.errorMessageAttribute(element, "name", name, "is empty"));
      } else if (name.indexOf('/')!=-1) {
        parse.addProblem(XmlUtil.errorMessageAttribute(element, "name", name, "contains slash (/)"));
      }
      activity.setName(name);
    }
  }

  public boolean isNameRequired() {
    return true;
  }

  public void parseFlows(Element element, ActivityImpl activity, Parse parse) {
    List<Element> transitionElements = XmlUtil.elements(element, "transition");
    UnresolvedTransitions unresolvedTransitions = parse.findObject(UnresolvedTransitions.class);
    for (Element transitionElement: transitionElements) {
      String transitionName = XmlUtil.attribute(transitionElement, "name", false, parse);
      TransitionImpl transition = activity.createOutgoingTransition(transitionName);
      unresolvedTransitions.add(transition, transitionElement);
    }
  }
}
