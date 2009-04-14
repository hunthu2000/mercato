/**
 * 
 */
package com.alten.mercato.jbpm.internal.spring;

import org.jbpm.cmd.Command;
import org.jbpm.cmd.CommandService;
import org.jbpm.env.EnvironmentFactory;
import org.jbpm.pvm.internal.spring.CommandTransactionCallback;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Huage Chen
 *
 */
public class CustomSpringCommandService implements CommandService {

	  TransactionTemplate transactionTemplate;
	  EnvironmentFactory environmentFactory;
	  
	  public void setEnvironmentFactory(EnvironmentFactory environmentFactory) {
	    this.environmentFactory = environmentFactory;
	  }

	  public void setTransactionTemplate(HibernateTransactionManager transactionManager) {
	    this.transactionTemplate = new TransactionTemplate(transactionManager);
	  }
	  
	  public <T> T execute(Command<T> command) {
	    return (T) transactionTemplate.execute(
	        new CommandTransactionCallback(command, environmentFactory)
	    );
	  }

}
