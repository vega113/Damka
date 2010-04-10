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



public class CellIndex  {
	private static String[] abc = {"A","B","C","D","E","F","G","H"};
	
	private int m_xCoord;
	
	
	private int m_yCoord;
	
	public CellIndex(int x, int y ){
		m_xCoord = x;
		m_yCoord = y;
	}
	
	public CellIndex(int x, String y ){
		m_xCoord = x;
		for(int i=0; i < abc.length; i++){
			if(y.equals(abc[i]))
				m_yCoord = i;
		}
	}
	public boolean equals(Object arg0) {
		
		return ((CellIndex)arg0).getXCoord() == m_xCoord && ((CellIndex)arg0).getYCoord() == m_yCoord;
	}

	public double compareTo(Object arg0) {
		
		double diffX = ((CellIndex)arg0).getXCoord() - m_xCoord;
		double diffY = ((CellIndex)arg0).getYCoord() - m_yCoord;
		diffY = diffY * diffY;
		diffX =  diffX * diffX;
		return diffX + diffY;
	}

	public int getXCoord() {
		return m_xCoord;
	}

	public void setXCoord(int coord) {
		m_xCoord = coord;
	}

	public int getYCoord() {
		return m_yCoord;
	}

	public void setYCoord(int coord) {
		m_yCoord = coord;
	}

	public CellIndex getLeft(){
		return new CellIndex(m_xCoord-1,m_yCoord);
	}
	public CellIndex getJumpLeft(){
		return new CellIndex(m_xCoord-2,m_yCoord);
	}
	public CellIndex getRight(){
		return new CellIndex(m_xCoord+1,m_yCoord);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "("+getXCoord()+","+ abc[getYCoord()]+")";
	}

	public CellIndex getJumpRight(){
		return new CellIndex(m_xCoord+2,m_yCoord);
	}
	public CellIndex getUp(){
		return new CellIndex(m_xCoord,m_yCoord+1);
	}
	public CellIndex getJumpUp(){
		return new CellIndex(m_xCoord,m_yCoord+2);
	}
	public CellIndex getDown(){
		return new CellIndex(m_xCoord,m_yCoord-1);
	}
	public CellIndex getJumpDown(){
		return new CellIndex(m_xCoord,m_yCoord-2);
	}
}
