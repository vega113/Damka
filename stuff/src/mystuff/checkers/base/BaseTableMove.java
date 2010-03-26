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
package mystuff.checkers.base;

import mystuff.checkers.table.CellIndex;

public abstract class BaseTableMove {
	
	private CellIndex from;
	
	
	private CellIndex to;
	
	public BaseTableMove(CellIndex from, CellIndex to) {
		this.from = from;
		this.to = to;
	}
	public BaseTableMove() {
		this.from = null;
		this.to = null;
	}
	public CellIndex getFrom() {
		return from;
	}
	public void setFrom(CellIndex from) {
		this.from = from;
	}
	public CellIndex getTo() {
		return to;
	}
	public void setTo(CellIndex to) {
		this.to = to;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return from.toString()+"->" + to.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		
		
		CellIndex from = ((BaseTableMove)arg0).getFrom();
		CellIndex to = ((BaseTableMove)arg0).getTo();
		return this.from.equals(from) && this.to.equals(to);
	}
}
