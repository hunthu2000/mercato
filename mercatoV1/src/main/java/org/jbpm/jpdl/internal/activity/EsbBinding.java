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
import java.util.List;

import org.jbpm.pvm.internal.util.XmlUtil;
import org.jbpm.pvm.internal.wire.Descriptor;
import org.jbpm.pvm.internal.wire.descriptor.AbstractDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.ExpressionDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.ListDescriptor;
import org.jbpm.pvm.internal.wire.xml.WireParser;
import org.jbpm.pvm.internal.xml.Parse;
import org.jbpm.pvm.internal.xml.Parser;
import org.w3c.dom.Element;


/**
 * @author Tom Baeyens
 */
public class EsbBinding extends JpdlActivityBinding {

  public static final String TAG = "esb";
  private static final WireParser wireParser = WireParser.getInstance(); 

  public EsbBinding() {
    super(TAG);
  }

  public Object parse(Element element, Parse parse, Parser parser) {
    EsbActivity esbActivity = new EsbActivity();
    
    String category = XmlUtil.attribute(element, "category", true, parse);
    esbActivity.setCategory(category);
    
    String service = XmlUtil.attribute(element, "service", true, parse);
    esbActivity.setService(service);
    
    List<Descriptor> partDescriptors = new ArrayList<Descriptor>();
    List<Element> partElements = XmlUtil.elements(element, "part");
    for (Element partElement: partElements) {
      String name = XmlUtil.attribute(partElement, "name", true, parse);
      AbstractDescriptor partDescriptor = getPartDescriptor(partElement, parse);
      partDescriptor.setName(name);
      partDescriptors.add(partDescriptor);
    }
    ListDescriptor partsListDescriptor = new ListDescriptor();
    if (!partDescriptors.isEmpty()) {
      partsListDescriptor.setValueDescriptors(partDescriptors);
    }
    esbActivity.setPartsListDescriptor(partsListDescriptor);

    return esbActivity;
  }

  public AbstractDescriptor getPartDescriptor(Element partElement, Parse parse) {
    String expression = XmlUtil.attribute(partElement, "expr");
    Element descriptorElement = XmlUtil.element(partElement);
    
    if ( ( (expression==null) && (descriptorElement==null) ) 
         || 
         ( (expression!=null) && (descriptorElement!=null) )
       ) {
      parse.addProblem("in <"+TAG+"...> an expr or exactly one child element is expected");
    }

    if (expression!=null) {
      return new ExpressionDescriptor(expression, null);
    }

    AbstractDescriptor descriptor = (AbstractDescriptor) wireParser.parseElement(descriptorElement, parse, WireParser.CATEGORY_DESCRIPTOR);
    return descriptor;
  }

}
