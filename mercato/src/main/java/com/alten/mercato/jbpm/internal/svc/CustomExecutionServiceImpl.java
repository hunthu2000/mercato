package com.alten.mercato.jbpm.internal.svc;

import org.jbpm.pvm.internal.svc.ExecutionServiceImpl;
import org.jbpm.cmd.CommandService;

public class CustomExecutionServiceImpl extends ExecutionServiceImpl {

	public void setCommandService(CommandService commandService) {
		this.commandService = commandService;
	}
}
