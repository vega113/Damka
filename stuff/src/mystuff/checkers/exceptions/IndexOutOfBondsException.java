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
package mystuff.checkers.exceptions;

import mystuff.checkers.table.CellIndex;
import mystuff.checkers.table.PlayableTableCell;

public class IndexOutOfBondsException extends Exception {
	public IndexOutOfBondsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IndexOutOfBondsException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public IndexOutOfBondsException(CellIndex cellIndex){
		super( "The cell at index " + cellIndex.toString() +  " is out of bonds");
	}
}
