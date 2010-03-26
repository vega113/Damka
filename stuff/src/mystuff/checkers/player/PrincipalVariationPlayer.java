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

import java.util.Arrays;

import mystuff.checkers.table.TableMove;

public class PrincipalVariationPlayer extends MinimaxPlayer {

	public PrincipalVariationPlayer(PlayerSide side) {
		super(side);
		// TODO Auto-generated constructor stub
	}
	
	public PrincipalVariationPlayer(PlayerSide side, int depth) {
		super(side,depth);
	}

	protected double doMinimaxForMove(TableMove move) {
		doMove(move);
		double minmaxValue=PrincipalVariation(move,getSide(),m_maxDepth, -100, 100);
		undoLastMove();
		move.setValue(minmaxValue);
		return minmaxValue;
		
	}
	
	protected double PrincipalVariation(TableMove tableMove, PlayerSide playerSide, int depth, double alpha, double beta){
		double best= 0;
		double value = 0;
		TableMove[] validMoves = getClonedTable().getAllValidMovesForPlayerSide(playerSide);// get all children
		if(validMoves.length == 0 || depth == 0 || getClonedTable().isWinConditionReachedForSide(playerSide)){
			m_depth = depth;
			return getHeuristicValueForBoardState(playerSide);
		}
		doMove(tableMove);
		best = -PrincipalVariation(validMoves[0],getSide(),depth-1, -beta, -alpha);
		for (int i = 1; i < validMoves.length; i++) {
			if(best < beta)
				break;
			if(best > alpha) 
				alpha = best;
			doMove(validMoves[i]);
			value = -PrincipalVariation(validMoves[i],playerSide.getNextPlayerSide(),depth-1, -alpha-1, -alpha);
			if (value > alpha && value < beta) 
	            best = -PrincipalVariation(validMoves[i],playerSide.getNextPlayerSide(),depth-1, -beta, -value);
	        else if (value > best)
	            best = value;
			undoLastMove();
		}
		undoLastMove();
		return best;
	}
	/* (non-Javadoc)
	 * @see mystuff.checkers.player.MinimaxPlayer#AnalyzeMoves()
	 */
	protected void AnalyzeMoves() {
		super.AnalyzeMoves();
	}
	

}
