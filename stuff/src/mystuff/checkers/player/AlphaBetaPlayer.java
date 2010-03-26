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
public class AlphaBetaPlayer extends MinimaxPlayer {


	public AlphaBetaPlayer(PlayerSide side) {
		super(side);
	}
	protected double doMinimaxForMove(TableMove move) {
		double minmaxValue;
		minmaxValue=AlphaBeta(move,getSide(),m_maxDepth, 0, 10000);
		return minmaxValue;
	}
	
	protected double AlphaBeta(TableMove tableMove, PlayerSide playerSide, int depth, double alpha, double beta){
		if(depth == 1){
			m_depth = depth;
			doMove(tableMove);
			alpha =  getHeuristicValueForBoardState(getSide());
			undoLastMove();
			return alpha;
		}
		doMove(tableMove);
		if(playerSide.equals(getSide())){
			if ( getClonedTable().isFasterWinConditionReachedForSide(getSide())){
				isWinConditionReached = true;
				undoLastMove();
				m_depth = depth;
				return getHeuristicValueForBoardState(getSide());
			}
		}
		TableMove[] validMoves = getValidMoves(playerSide.getNextPlayerSide());// get all children
//		if(playerSide.getNextPlayerSide().equals(getSide())){
//			sortMoves(validMoves,TableMove.AscValueMoveComparator);
//		}
//		else
//		{
//			sortMoves(validMoves,TableMove.DescValueMoveComparator);
//		}
		for (int i = 0; i < validMoves.length; i++) {			
			alpha = Math.max(alpha,-AlphaBeta(validMoves[i],playerSide.getNextPlayerSide(),depth-1, -beta, -alpha));
			if (alpha >= beta) {
				break;			
			}
		}
		undoLastMove();
		return alpha;
	}
	
	/**
	 * @param side
	 * @param depth
	 */
	public AlphaBetaPlayer(PlayerSide side, int depth) {
		super(side, depth);
	}

}
