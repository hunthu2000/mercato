/**
 * 
 */
package com.alten.mercato.server.exception;

/**
 * @author Huage Chen
 *
 */
public class MercatoWorkflowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3561557729182985115L;

	/**
	 * 
	 */
	public MercatoWorkflowException() {
		super();
	}
	
	/**
	 * @param message
	 */
	public MercatoWorkflowException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public MercatoWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}
}
