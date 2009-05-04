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

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.jbpm.activity.ActivityBehaviour;
import org.jbpm.internal.log.Log;
import org.jbpm.jpdl.internal.activity.JpdlActivityBinding;
import org.jbpm.jpdl.internal.model.JpdlProcessDefinition;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.task.AssignableDefinitionImpl;
import org.jbpm.pvm.internal.task.SwimlaneDefinitionImpl;
import org.jbpm.pvm.internal.task.TaskDefinitionImpl;
import org.jbpm.pvm.internal.util.ReflectUtil;
import org.jbpm.pvm.internal.util.XmlUtil;
import org.jbpm.pvm.internal.wire.descriptor.ObjectDescriptor;
import org.jbpm.pvm.internal.wire.operation.Operation;
import org.jbpm.pvm.internal.wire.xml.WireParser;
import org.jbpm.pvm.internal.xml.Parse;
import org.jbpm.pvm.internal.xml.Parser;
import org.w3c.dom.Element;

/**
 * @author Tom Baeyens
 */
public class JpdlParser extends Parser {
  
  private static final Log log = Log.getLog(JpdlParser.class.getName());
  
  public static final WireParser wireParser = WireParser.getInstance();

  public static final String[] DEFAULT_ACTIVITIES_RESOURCES = new String[]{
    "jbpm.jpdl.activities.xml",
    "jbpm.user.activities.xml"
  }; 

  static ActivitiesParser activityParser = new ActivitiesParser();

  public JpdlParser() {
    initialize(); 
    
    Activities activitiesConfiguration = parseActivitiesConfiguration();
    setBindings(activitiesConfiguration.bindings);
    setSchemaResources(activitiesConfiguration.schemaResources);
  }

  protected Activities parseActivitiesConfiguration() {
    Activities activities = new Activities();

    for (String activityResource: DEFAULT_ACTIVITIES_RESOURCES) {
      Enumeration<URL> resourceUrls = ReflectUtil.getResources(null, activityResource);
      if (resourceUrls.hasMoreElements()) {
        while (resourceUrls.hasMoreElements()) {
          URL resourceUrl = resourceUrls.nextElement();
          log.trace("loading jpdl activities from resource: "+resourceUrl);
          activityParser.createParse()
            .setUrl(resourceUrl)
            .pushObject(activities)
            .execute()
            .checkProblems("jpdl activities from "+resourceUrl.toString());
        }
      } else {
        log.trace("skipping unavailable jpdl activities resource: "+activityResource);
      }
    }
    
    return activities;
  }

  public Object parseDocumentElement(Element documentElement, Parse parse) {
    JpdlProcessDefinition processDefinition = (JpdlProcessDefinition) parse.getDocumentObject();
    if (processDefinition==null) {
      processDefinition = new JpdlProcessDefinition();
      parse.setDocumentObject(processDefinition);
    }
    
    parse.pushObject(processDefinition);
    try {
      // process attribues
      String name = XmlUtil.attribute(documentElement, "name", true, parse);
      processDefinition.setName(name);
      
      String packageName = XmlUtil.attribute(documentElement, "package");
      processDefinition.setPackageName(packageName);

      Integer version = XmlUtil.attributeInteger(documentElement, "version", false, parse);
      if (version!=null) {
        processDefinition.setVersion(version);
      }

      String key = XmlUtil.attribute(documentElement, "key", false, parse);
      if (key!=null) {
        processDefinition.setKey(key);
      }

      Element descriptionElement = XmlUtil.element(documentElement, "description");
      if (descriptionElement!=null) {
        String description = XmlUtil.getContentText(descriptionElement);
        processDefinition.setDescription(description);
      }
      
      UnresolvedTransitions unresolvedTransitions = new UnresolvedTransitions();
      parse.pushObject(unresolvedTransitions);
      
      // swimlanes
      List<Element> swimlaneElements = XmlUtil.elements(documentElement, "swimlane");
      for (Element swimlaneElement: swimlaneElements) {
        String swimlaneName = XmlUtil.attribute(swimlaneElement, "name", true, parse);
        if (swimlaneName!=null) {
          SwimlaneDefinitionImpl swimlaneDefinition = 
              processDefinition.createSwimlaneDefinition(swimlaneName);
          JpdlParser.parseAssignmentAttributes(swimlaneElement, swimlaneDefinition, parse);
        }
      }
      
      // activities
      List<Element> elements = XmlUtil.elements(documentElement);
      for (Element element: elements) {
        JpdlActivityBinding activityBinding = (JpdlActivityBinding) getBinding(element, "activity");
        if (activityBinding!=null) {
          ActivityImpl activity = (ActivityImpl) processDefinition.createActivity();
          parse.pushObject(activity);
          try {
            activity.setType(activityBinding.getTagName());
            activityBinding.parseName(element, activity, parse);
            activityBinding.parseFlows(element, activity, parse);
            ActivityBehaviour activityBehaviour = (ActivityBehaviour) activityBinding.parse(element, parse, this);
            activity.setBehaviour(activityBehaviour);
          } finally {
            parse.popObject();
          }
        } else {
          log.debug("unrecognized activity: "+XmlUtil.getTagLocalName(element));
        }
      }
      
      for (UnresolvedTransition unresolvedTransition: unresolvedTransitions.list) {
        unresolvedTransition.resolve(processDefinition, parse);
      }

    } finally {
      parse.popObject();
    }
    

    if (processDefinition.getInitial()==null) {
      parse.addProblem("no start activity in process");
    }
    
    return processDefinition;
  }

  public static void parseAssignmentAttributes(Element element, AssignableDefinitionImpl assignableDefinition, Parse parse) {
    Element descriptionElement = XmlUtil.element(element, "description");
    if (descriptionElement!=null) {
      String description = XmlUtil.getContentText(descriptionElement);
      assignableDefinition.setDescription(description);
    }
  
    Element assignmentHandlerElement = XmlUtil.element(element, "assignment-handler");
    if (assignmentHandlerElement!=null) {
      ObjectDescriptor objectDescriptor = parseObjectDescriptor(assignmentHandlerElement, parse);
      assignableDefinition.setAssignmentHandlerDescriptor(objectDescriptor);
    }
  
    String assigneeExpression = XmlUtil.attribute(element, "assignee");
    assignableDefinition.setAssigneeExpression(assigneeExpression);
    
    String assigneeExpressionLanguage = XmlUtil.attribute(element, "assignee-lang");
    assignableDefinition.setAssigneeExpressionLanguage(assigneeExpressionLanguage);
    
    String candidateUsersExpression = XmlUtil.attribute(element, "candidate-users");
    assignableDefinition.setCandidateUsersExpression(candidateUsersExpression);
    
    String candidateUsersExpressionLanguage = XmlUtil.attribute(element, "candidate-users-lang");
    assignableDefinition.setCandidateUsersExpressionLanguage(candidateUsersExpressionLanguage);
    
    String candidateGroupsExpression = XmlUtil.attribute(element, "candidate-groups");
    assignableDefinition.setCandidateGroupsExpression(candidateGroupsExpression);
    
    String candidateGroupsExpressionLanguage = XmlUtil.attribute(element, "candidate-groups-lang");
    assignableDefinition.setCandidateGroupsExpressionLanguage(candidateGroupsExpressionLanguage);
  }

  public static TaskDefinitionImpl parseTaskDefinition(Element element, Parse parse, Parser parser) {
    TaskDefinitionImpl taskDefinition = new TaskDefinitionImpl();
  
    String taskName = XmlUtil.attribute(element, "name");
    taskDefinition.setName(taskName);
    
    ProcessDefinitionImpl processDefinition = parse.findObject(ProcessDefinitionImpl.class);
    if (processDefinition.getTaskDefinition(taskName)!=null) {
      parse.addProblem("duplicate task name "+taskName);
    } else {
      processDefinition.addTaskDefinitionImpl(taskDefinition);
    }

    String swimlaneName = XmlUtil.attribute(element, "swimlane");
    if (swimlaneName!=null) {
      JpdlProcessDefinition jpdlProcessDefinition = parse.findObject(JpdlProcessDefinition.class);
      SwimlaneDefinitionImpl swimlaneDefinition = jpdlProcessDefinition.getSwimlaneDefinition(swimlaneName);
      if (swimlaneDefinition!=null) {
        taskDefinition.setSwimlaneDefinition(swimlaneDefinition);
      } else {
        parse.addProblem("swimlane "+swimlaneName+" not declared");
      }
    }
    
    Element taskHandlerElement = XmlUtil.element(element, "task-handler");
    if (taskHandlerElement!=null) {
      ObjectDescriptor objectDescriptor = parseObjectDescriptor(taskHandlerElement, parse);
      taskDefinition.setTaskHandlerDescriptor(objectDescriptor);
    }
  
    JpdlParser.parseAssignmentAttributes(element, taskDefinition, parse);
    
    return taskDefinition;
  }

  public static ObjectDescriptor parseObjectDescriptor(Element element, Parse parse) {
    ObjectDescriptor objectDescriptor = new ObjectDescriptor();
  
    String className = XmlUtil.attribute(element, "class");
    if (className!=null) {
      objectDescriptor.setClassName(className);
  
      // read the operations elements
      List<Operation> operations = null;
      List<Element> elements = XmlUtil.elements(element);
      
      Set<String> operationTagNames = wireParser.getBindings().getTagNames(WireParser.CATEGORY_OPERATION);
      for (Element childElement: elements) {
        if (operationTagNames.contains(childElement.getTagName())) {
          Operation operation = (Operation) wireParser.parseElement(childElement, parse, WireParser.CATEGORY_OPERATION);
          if (operations==null) {
            operations = new ArrayList<Operation>();
          }
          operations.add(operation);
        }
      }
      objectDescriptor.setOperations(operations);
  
      // autowiring
      Boolean isAutoWireEnabled = XmlUtil.attributeBoolean(element, "auto-wire", false, parse);
      if (isAutoWireEnabled!=null) {
        objectDescriptor.setAutoWireEnabled(isAutoWireEnabled.booleanValue());
      }
    }
    return objectDescriptor;
  }
}
