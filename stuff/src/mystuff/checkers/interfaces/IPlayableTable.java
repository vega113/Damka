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
package mystuff.checkers.interfaces;

import java.io.StringReader;
import java.util.List;

import mystuff.checkers.base.BasePlayerSide;
import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;

/**
 * 
 * @author Vega
 * The play table that contains 
 */
public interface IPlayableTable {
	/**
	 * gets the current state
	 * @return TableState
	 */
	public void setCurrentState(TableState tableState);
	public void doMove(TableMove tableMove) throws InvalidMoveException, IndexOutOfBondsException;
	public void undoLastMove() throws InvalidMoveException, IndexOutOfBondsException;
	public boolean isMoveValid(TableMove tableMove);
	public TableMove[] getAllValidMovesForPlayerSide(BasePlayerSide player);
	public List getHistoryMoves();
	public void Rewind(int movesToRewind);
	public StringReader toXmlString();
	public void restoreFromXml(String uri);
	public boolean isWinConditionReachedForSide(BasePlayerSide player);
	public double getDistanceFromWinConditionForSide(PlayerSide side);
	public IPlayableTable cloneMe() throws CloneNotSupportedException;
}
