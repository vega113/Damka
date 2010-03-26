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

import mystuff.checkers.table.CellColor;
import mystuff.checkers.table.CellIndex;

public abstract class BasePlayableTableCell {
	private CellColor m_cellColor;
	private CellIndex m_cellIndex;
	
	public BasePlayableTableCell (CellIndex index) {
		m_cellColor = new CellColor( UgolkiConstants.CellColorUnoccupied);
		m_cellIndex = index;
	}
	public BasePlayableTableCell(CellColor color, CellIndex index){
		m_cellColor = color;
		m_cellIndex = index;
	}
	
	public CellColor getCellColor() {
		return m_cellColor;
	}
	public void setCellColor(CellColor cellColor) {
		this.m_cellColor = cellColor;
	}
	public CellIndex getCellIndex() {
		return m_cellIndex;
	}
	public void setCellIndex(CellIndex cellIndex) {
		this.m_cellIndex = cellIndex;
	}
	public boolean equals(Object arg0) {
		
		CellColor cellColor = ((BasePlayableTableCell)arg0).getCellColor();
		CellIndex cellIndex = ((BasePlayableTableCell)arg0).getCellIndex();
		return m_cellColor.equals(cellColor) && m_cellIndex.equals(cellIndex);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "("+getCellIndex().toString() + "," + getCellColor().getColor() + ")";
	}

}
