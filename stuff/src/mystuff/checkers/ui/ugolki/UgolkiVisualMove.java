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
package mystuff.checkers.ui.ugolki;

import mystuff.checkers.table.TableMove;
import mystuff.checkers.ui.plaincheckers.CheckersMove;

/**
 * @author Vega
 *
 */
public class UgolkiVisualMove extends CheckersMove {

	public UgolkiVisualMove(int r1, int c1, int r2, int c2) {
		super(r1, c1, r2, c2);
	}
	public UgolkiVisualMove(TableMove tableMove) {
		super(tableMove.getFrom().getXCoord(), tableMove.getFrom().getYCoord(), 
				tableMove.getTo().getXCoord(), tableMove.getTo().getYCoord());
	}
	
	protected boolean isJump() {
	     return false;
	  }
	
	public String toString() {
		return "("+getFromRow()+","+ getFromCol()+")"+"->" + "("+getToRow()+","+ getToCol()+")";
	}
	
	

}
