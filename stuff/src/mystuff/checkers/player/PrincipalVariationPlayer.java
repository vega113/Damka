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

public class PrincipalVariationPlayer extends MinimaxPlayerV2 {

	public PrincipalVariationPlayer(PlayerSide side) {
		super(side);
	}
	
	public PrincipalVariationPlayer(PlayerSide side, int depth) {
		super(side,depth);
	}

	
	protected double PrincipalVariation(TableMove tableMove, PlayerSide playerSide, int depth, double alpha, double beta){
		double best= 0;
		double value = 0;
		if (depth == 1 ){
			m_depth = depth;
			doMove(tableMove);
			alpha = getHeuristicValueForBoardState(getSide());
			undoLastMove();
			return alpha;
		}
		if(getClonedTable().isWinConditionReachedForSide(getSide())){
			isWinConditionReached = true;
			return getHeuristicValueForBoardState(getSide());
		}
		doMove(tableMove);
		TableMove[] validMoves = getClonedTable().getAllValidMovesForPlayerSide(playerSide);// get all children
		best = -PrincipalVariation(validMoves[0],getSide(),depth-1, -beta, -alpha);
		for (int i = 1; i < validMoves.length; i++) {
			if(best < beta)
				break;
			if(best > alpha) 
				alpha = best;
			doMove(validMoves[i]);
			value = -PrincipalVariation(validMoves[i],playerSide,depth-1, -alpha-1, -alpha);
			if (value > alpha && value < beta) 
	            best = -PrincipalVariation(validMoves[i],playerSide.getNextPlayerSide(),depth-1, -beta, -value);
	        else if (value > best)
	            best = value;
			undoLastMove();
		}
		undoLastMove();
		return best;
	}
	
	@Override
	protected double MaxValue(TableMove tableMove, int depth) {
		return PrincipalVariation(tableMove,getSide(),depth, -100, 100);
	}
	

}
