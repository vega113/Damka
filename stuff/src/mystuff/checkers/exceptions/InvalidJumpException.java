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

import mystuff.checkers.table.CellIndex;

/**
 * @author Vega
 *
 */
public class InvalidJumpException extends Exception {

	/**
	 * 
	 */
	public InvalidJumpException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidJumpException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidJumpException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public InvalidJumpException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public InvalidJumpException(CellIndex currentCellIndex) {
		super("The cell at index " + currentCellIndex.toString() + " is not valid for Jump");
	}

}
