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
package org.jbpm.jpdl.internal.xml;

import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.util.ReflectUtil;
import org.jbpm.pvm.internal.util.XmlUtil;
import org.jbpm.pvm.internal.xml.Binding;
import org.jbpm.pvm.internal.xml.Parse;
import org.jbpm.pvm.internal.xml.Parser;
import org.w3c.dom.Element;

/**
 * @author Tom Baeyens
 */
public class ActivitiesParser extends Parser {

  private static final Log log = Log.getLog(ActivitiesParser.class.getName());
  
  public Object parseDocumentElement(Element documentElement, Parse parse) {
    Activities activities = parse.findObject(Activities.class);
    if (activities==null) {
      activities = new Activities();
    }
    parse.setDocumentObject(activities);
    
    for (Element bindingElement : XmlUtil.elements(documentElement, "activity")) {
      Binding binding = instantiateBinding(bindingElement, parse);
      activities.bindings.addBinding(binding);
    }
    
    for (Element schemaElement : XmlUtil.elements(documentElement, "schema")) {
      String schemaResource = XmlUtil.attribute(schemaElement, "resource", true, parse);
      activities.schemaResources.add(schemaResource);
    }

    return activities;
  }

  protected Binding instantiateBinding(Element bindingElement, Parse parse) {
    String bindingClassName = XmlUtil.attribute(bindingElement, "binding", true, parse);
    
    log.trace("adding activity binding "+bindingClassName);
    
    if (bindingClassName!=null) {
      try {
        Class<?> bindingClass = ReflectUtil.loadClass(classLoader, bindingClassName);
        return (Binding) bindingClass.newInstance();
      } catch (Exception e) {
        parse.addProblem("couldn't instantiate activity binding "+bindingClassName, e);
      }
    }
    return null;
  }
}
