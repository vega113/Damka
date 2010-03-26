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
package mystuff.checkers.player;

import mystuff.checkers.base.BaseAutomatePlayer;
import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.table.TableMove;

public class MaxPlayer extends BaseAutomatePlayer {

	protected double m_minDistance;

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BaseAutomatePlayer#AnalyzeMoves()
	 */
	protected void AnalyzeMoves() {
		preAnalyzeMoves();
		m_minDistance = -10000.00;
		int indexOfBestMove = 0;
		if(m_validNextMoves != null){
			int size = m_validNextMoves.length;
			for(int i =0; i< size;i++){
				doMove(m_validNextMoves[i]);
				double distance = getHeuristicValueForBoardState(getSide());
				if(getDistance() <= distance){
					setDistance(distance);
					indexOfBestMove = i;
				}
				// mabe back move
				undoLastMove();
			}
			m_nextBestMove = m_validNextMoves[indexOfBestMove];
			m_isReady = true;
		}
	}

	public MaxPlayer(PlayerSide playerSide) {
		super(playerSide);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BaseAutomatePlayer#getStateValue()
	 */
	public double getStateValue() {
		return getDistance();
	}

	protected TableMove[] getValidMoves(PlayerSide side) {
		 TableMove[] outputMoves = getClonedTable().getAllValidMovesForPlayerSide(side);
		 m_totalConsideredMoves+=outputMoves.length;
		 return outputMoves;
	}

}
