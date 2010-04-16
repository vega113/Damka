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
package mystuff.checkers.player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableMove.MaxMoveComparator;

/**
 * @author Vega
 *
 */
public class AlphaBetaPlayer extends MinimaxPlayerV2 {


	public AlphaBetaPlayer(PlayerSide side) {
		super(side);
	}
	/**
	 * @param side
	 * @param depth
	 */
	public AlphaBetaPlayer(PlayerSide side, int depth) {
		super(side, depth);
	}
	
	protected double alphaBeta(TableMove tableMove, PlayerSide playerSide, int depth, double alpha, double beta){
		if(depth == 1){
			m_depth = depth;
			doMove(tableMove);
			alpha =  getHeuristicValueForBoardState(getSide());
			undoLastMove();
			return alpha;
		}
		if(getClonedTable().isWinConditionReachedForSide(getSide())){
			isWinConditionReached = true;
			return getHeuristicValueForBoardState(getSide());
		}
		doMove(tableMove);
		TableMove[] validMoves = getValidMoves(playerSide);// get all children
		for (int i = 0; i < validMoves.length; i++) {			
			alpha = Math.max(alpha,-alphaBeta(validMoves[i],playerSide.getNextPlayerSide(),depth-1, -beta, -alpha));
			if (alpha >= beta) {
				break;			
			}
		}
		undoLastMove();
		return alpha;
	}
	/* (non-Javadoc)
	 * @see mystuff.checkers.player.MinimaxPlayer#MaxValue(mystuff.checkers.table.TableMove, int)
	 */
	@Override
	protected double MaxValue(TableMove tableMove, int depth) {
		return alphaBeta(tableMove,getSide(),depth, 0, 10000);
	}
	
	

}
