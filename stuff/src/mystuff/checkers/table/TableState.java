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

import mystuff.checkers.base.BaseTableState;
import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.interfaces.IPlayableTable;
import mystuff.checkers.player.PlayerSide;

public class TableState extends BaseTableState{
	
	
	protected double m_startingDistanceFromWin4Blue;
	
	
	protected double m_startingDistanceFromWin4Red;
	
	
	private double m_finalDistanceFromWin4Red;
	
	
	private double m_finalDistanceFromWin4Blue;

	/**
	 * 
	 */
	public TableState() {
		super();
		m_startingDistanceFromWin4Blue = getDistanceFromWinConditionForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorBlueIndex)));
		m_startingDistanceFromWin4Red = getDistanceFromWinConditionForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorRedIndex)));
		m_finalDistanceFromWin4Blue = 0;
		m_finalDistanceFromWin4Red = 0;
		for (int i = 0; i < m_winIndexesForBlueSide.length; i++) {
			m_finalDistanceFromWin4Blue += m_winIndexesForBlueSide[i].compareTo(m_centerIndexForWin4Blue);
		}
		for (int i = 0; i < m_winIndexesForRedSide.length; i++) {
			m_finalDistanceFromWin4Red += m_winIndexesForRedSide[i].compareTo(m_centerIndexForWin4Red);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public IPlayableTable cloneMe() throws CloneNotSupportedException {
		TableState newTableState = new TableState();
		int xSize = newTableState.getCurrentStateContainer().getXSize();
		int ySize = newTableState.getCurrentStateContainer().getYSize();
		for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					CellIndex cellIndex = new CellIndex(i,j);
					CellColor cellColor = null;
					try {
						cellColor = new CellColor(this.getCellByIndex(cellIndex).getCellColor().getColor());
					} catch (IndexOutOfBondsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						newTableState.getCellByIndex(cellIndex).setCellColor(cellColor);
					} catch (IndexOutOfBondsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		return newTableState;
	}
	
	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayableTable#getDistanceFromWinConditionForSide(mystuff.checkers.player.PlayerSide)
	 */
	public double getDistanceFromWinConditionForSide(PlayerSide side) {
		CellIndex[] cellIndexs = getAllCellIndexesForPlayerSide(side);
		double distance = 0;
		if (side.getColor() == UgolkiConstants.CellColorBlueIndex) {
			for (int i = 0; i < cellIndexs.length; i++) {
				distance += cellIndexs[i].compareTo(m_centerIndexForWin4Blue);
			}
			return distance;
		}
		else {
			for (int i = 0; i < cellIndexs.length; i++) {
				distance += cellIndexs[i].compareTo(m_centerIndexForWin4Red);
			}
			return distance;
		}
	}

	public double getStartingDistanceFromWin4Blue() {
		return m_startingDistanceFromWin4Blue;
	}

	public void setStartingDistanceFromWin4Blue(double startingDistanceFromWin4Blue) {
		this.m_startingDistanceFromWin4Blue = startingDistanceFromWin4Blue;
	}

	public double getStartingDistanceFromWin4Red() {
		return m_startingDistanceFromWin4Red;
	}

	public void setStartingDistanceFromWin4Red(double startingDistanceFromWin4Red) {
		this.m_startingDistanceFromWin4Red = startingDistanceFromWin4Red;
	}

	/**
	 * @return Returns the finalDistanceFromWin4Blue.
	 */
	public double getFinalDistanceFromWin4Blue() {
		return m_finalDistanceFromWin4Blue;
	}

	/**
	 * @param finalDistanceFromWin4Blue The finalDistanceFromWin4Blue to set.
	 */
	public void setFinalDistanceFromWin4Blue(double finalDistanceFromWin4Blue) {
		this.m_finalDistanceFromWin4Blue = finalDistanceFromWin4Blue;
	}

	/**
	 * @return Returns the finalDistanceFromWin4Red.
	 */
	public double getFinalDistanceFromWin4Red() {
		return m_finalDistanceFromWin4Red;
	}

	/**
	 * @param finalDistanceFromWin4Red The finalDistanceFromWin4Red to set.
	 */
	public void setFinalDistanceFromWin4Red(double finalDistanceFromWin4Red) {
		this.m_finalDistanceFromWin4Red = finalDistanceFromWin4Red;
	}

	public PlayableTableCell getCellByIndex(int row, int col) throws IndexOutOfBondsException {
		return getCellByIndex(new CellIndex(row,col));
	}

	
}
