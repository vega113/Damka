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

public class CellColor  {
	private int m_color;
	public CellColor(int color){
		m_color = color;
	}
	public CellColor(){
		m_color = 0;
	}
	protected Object clone() throws CloneNotSupportedException {
		
		return new CellColor(m_color);
	}
	public boolean equals(Object arg0) {
		
		return ((CellColor)arg0).getColor() == m_color;
	}
	public String toString() {
		
		return ""+m_color;
	}
	public int getColor() {
		return m_color;
	}
	public void setColor(int color) {
		this.m_color = color;
	}

}
