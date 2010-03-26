/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package mystuff.checkers.exceptions;

import mystuff.checkers.table.TableMove;

/**
 * @author Vega
 *
 */
public class InvalidMoveException extends Exception {

	/**
	 * 
	 */
	public InvalidMoveException() {
		super();
		// TODO Auto-generated constructor stub
	}
	public InvalidMoveException(TableMove tableMove) {
		super("Invalid Move" + tableMove.toString());
		
	}
	/**
	 * @param arg0
	 */
	public InvalidMoveException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidMoveException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidMoveException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

}
