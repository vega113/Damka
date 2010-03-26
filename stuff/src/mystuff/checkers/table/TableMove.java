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
package mystuff.checkers.table;

import java.io.Serializable;
import java.util.Comparator;

import mystuff.checkers.base.BaseTableMove;


public class TableMove extends BaseTableMove {
	
	public static MaxMoveComparator DescValueMoveComparator = new MaxMoveComparator();
	public static MaxMoveComparator AscValueMoveComparator = new MaxMoveComparator();
	
	
	private double  m_value=0;
	public TableMove(CellIndex from, CellIndex to) {
		super(from, to);
	}
	public TableMove() {
		super();
	}

	public boolean equals(Object arg0) {
		BaseTableMove move = (BaseTableMove)arg0;
		return this.getFrom().equals(move.getFrom()) && this.getTo().equals(move.getTo());
	}

	

	/**
	 * @return Returns the value.
	 */
	public double getValue() {
		return m_value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(double value) {
		this.m_value = value;
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BaseTableMove#toString()
	 */
	public String toString() {
		String result = super.toString();
		return "<"+result+","+(float)getValue()+">";
	}
	
	public static class MinMoveComparator implements Comparator, Serializable {

		public int compare(Object arg0, Object arg1) {
			if(((TableMove)arg0).getValue()  > ((TableMove)arg1).getValue() ){
				return 1;
			}
			if(((TableMove)arg0).getValue()  < ((TableMove)arg1).getValue() ){
				return -1;
			}
			return 0;
		}
		
	}
	public static class MaxMoveComparator implements Comparator, Serializable {

		public int compare(Object arg0, Object arg1) {
			if(((TableMove)arg0).getValue()  > ((TableMove)arg1).getValue() ){
				return -1;
			}
			if(((TableMove)arg0).getValue()  < ((TableMove)arg1).getValue() ){
				return 1;
			}
			return 0;
		}
		
	}
	
}
