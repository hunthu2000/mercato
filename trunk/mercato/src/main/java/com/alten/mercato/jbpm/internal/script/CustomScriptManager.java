package com.alten.mercato.jbpm.internal.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jbpm.Execution;
import org.jbpm.JbpmException;
import org.jbpm.env.Environment;
import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.env.ExecutionContext;
import org.jbpm.pvm.internal.env.ExecutionEnvironment;
import org.jbpm.pvm.internal.script.EnvironmentBindings;
import org.jbpm.pvm.internal.wire.WireContext;
import org.jbpm.pvm.internal.wire.WireDefinition;
import org.jbpm.pvm.internal.wire.xml.WireParser;

/**
 * @author Tom Baeyens
 */
public class CustomScriptManager {

  private static Log log = Log.getLog(CustomScriptManager.class.getName());

  private static CustomScriptManager defaultScriptManager = null;

  protected String defaultExpressionLanguage = "juel";
  protected String defaultScriptLanguage= "juel";
  protected ScriptEngineManager scriptEngineManager;
  protected String[] readContextNames = {"execution", "environment", "process-engine"};
  protected String writeContextName = "";
  
  public static synchronized CustomScriptManager getDefaultScriptManager() {
    if (defaultScriptManager==null) {
      WireDefinition wireDefinition = (WireDefinition) new WireParser()
        .createParse()
        .setString(
          "<objects>" +
          "  <script-manager default-expression-language='juel'" +
          "                  default-script-language='beanshell' " +
          "                  read-contexts='execution, environment, process-engine' " +
          "                  write-context=''>" +
          "    <script-language name='juel' factory='org.jbpm.pvm.internal.script.JuelScriptEngineFactory' />" +
          "  </script-manager>" +
          "</objects>"
        )
        .execute()
        .getDocumentObject();
  
      WireContext wireContext = new WireContext(wireDefinition);
      defaultScriptManager = wireContext.get(CustomScriptManager.class);
    }
    return defaultScriptManager;
  }
  
  /** {@link #evaluate(String, Execution, String) evaluates} the expression 
   * with the given language or with the defaultExpressionLanguage if the 
   * given language is null. */
  public Object evaluateExpression(String expression, Execution execution, String language) {
    return evaluate(expression, execution, (language!=null ? language : defaultExpressionLanguage));
  }

  /** {@link #evaluate(String, Execution, String) evaluates} the script 
   * with the given language or with the defaultScriptLanguage if the 
   * given language is null. */
  public Object evaluateScript(String script, Execution execution, String language) {
    return evaluate(script, execution, (language!=null ? language : defaultScriptLanguage));
  }

  /** evaluates the script with the given language.
   * If script is null, then this method will return null.
   * @throws JbpmException if language is null.
   */
  public Object evaluate(String script, Execution execution, String language) {
    if (script==null) {
      return null;
    }
    if (language==null) {
      throw new JbpmException("no language specified");
    }
    ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
    if (scriptEngine==null) {
      throw new JbpmException("no scripting engine configured for language "+language);
    }
    
    if (log.isTraceEnabled()) log.trace("evaluating "+language+" script "+script);
    
    if (execution==null) {
      return evaluate(scriptEngine, script);
    }

    Environment environment = Environment.getCurrent();
    if (environment==null) {
      environment = new ExecutionEnvironment(execution);
      try {
        return evaluate(scriptEngine, script);
      } finally {
        environment.close();
      }
    }

    ExecutionContext executionContext = new ExecutionContext(execution);
    environment.addContext(executionContext);
    try {
      return evaluate(scriptEngine, script);
    } finally {
      environment.removeContext(executionContext);
    }
  }
  
  protected Object evaluate(ScriptEngine scriptEngine, String script) {
    Bindings bindings = new EnvironmentBindings(readContextNames, writeContextName);
    scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    
    try {
      Object result = scriptEngine.eval(script);
      if (log.isTraceEnabled()) log.trace("script evaluated to "+result);
      return result;
    } catch (ScriptException e) {
      throw new JbpmException("script evaluation error: "+e.getMessage(), e);
    }
  }

  public String getDefaultExpressionLanguage() {
    return defaultExpressionLanguage;
  }
  public String getDefaultScriptLanguage() {
    return defaultScriptLanguage;
  }
}
