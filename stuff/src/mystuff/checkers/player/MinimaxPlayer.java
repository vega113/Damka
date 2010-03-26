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

import mystuff.checkers.base.BaseAutomatePlayer;
import mystuff.checkers.base.BasePlayerSide;
import mystuff.checkers.table.TableMove;

/**
 * @author Vega
 *
 */
public class MinimaxPlayer extends BaseAutomatePlayer {

	private int m_depthOfBestMove;
	public MinimaxPlayer(PlayerSide side) {
		super(side);
	}
	public MinimaxPlayer(PlayerSide side, int depth) {
		super(side);
		m_maxDepth = depth;
	}

	protected void AnalyzeMoves() {
		m_indexOfBestMove = 0;
		m_depthOfBestMove = 100000;
		if(m_validNextMoves != null){
			int size = m_validNextMoves.length;
			sortMoves(m_validNextMoves,TableMove.DescValueMoveComparator);
			for(int i =0; i< size;i++){
				double evaluationValue4Move = doMinimaxForMove(m_validNextMoves[i]);
				m_validNextMoves[i].setValue(evaluationValue4Move);
//				if(isWinConditionReached){
//					setDistance(evaluationValue4Move);
//					m_indexOfBestMove = i;
//					m_isReady = true;
//					return;
//				}
				if (evaluationValue4Move > getDistance() || (evaluationValue4Move == getDistance() && m_depth < m_depthOfBestMove)) {
					m_indexOfBestMove = i;
					m_depthOfBestMove = m_depth;
					setDistance(evaluationValue4Move);
				}
			}
			m_nextBestMove = m_validNextMoves[m_indexOfBestMove];
			if(isWinConditionReached && m_depthOfBestMove <  m_maxDepth && m_maxDepth > 1){
				//need to investigate
				int m_maxDepthTemp =m_maxDepth;
				m_maxDepth =m_depthOfBestMove;
				AnalyzeMoves();
				System.out.println("Giving Up for move:" + m_nextBestMove.toString() + "\n");
			}
			m_isReady = true;
			
		}
	}
	protected double doMinimaxForMove(TableMove move) {
		double minmaxValue;
		m_stack.clear();
		if(m_maxDepth == 1){
			doMove(move);
			minmaxValue = getHeuristicValueForBoardState(getSide());
			undoLastMove();
		}
		else {
			minmaxValue=MaxValue(move,m_maxDepth);
		}
		move.setValue(minmaxValue);
		return minmaxValue;
		
	}
	
	protected double MinValue(TableMove tableMove,  int depth){
		double alpha = 0;
		if (depth == 1 ){
			
			m_depth = depth;
			doMove(tableMove);
			alpha = getHeuristicValueForBoardState(getSide());
			undoLastMove();
			return alpha;
		}
		if(getClonedTable().isWinConditionReachedForSide(getSide())){
			isWinConditionReached = true;
			m_depth = depth;
			return getHeuristicValueForBoardState(getSide());
		}
		TableMove[] validMoves = getValidMoves(getSide().getNextPlayerSide());// get all children
		Arrays.sort(validMoves, TableMove.AscValueMoveComparator);
//	   if the adversary is to play at node
		doMove(tableMove);
		alpha = INFINITY;
		for (int i = 0; i < validMoves.length; i++) {
			alpha = Math.min(alpha,MaxValue(validMoves[i],depth-1));
		}
		undoLastMove();
		return alpha;
	}
	protected double MaxValue(TableMove tableMove,  int depth){
		double alpha;
		
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
		TableMove[] validMoves = getValidMoves(getSide());// get all children
		Arrays.sort(validMoves,TableMove.DescValueMoveComparator);
		alpha = -1*INFINITY;
		for (int i = 0; i < validMoves.length; i++) {		
			alpha = Math.max(alpha,MinValue(validMoves[i],depth-1));
		}
		undoLastMove();
		return alpha;
	}
	/* (non-Javadoc)
	 * @see mystuff.checkers.base.AbstractBean#validateInput()
	 */
	public void validateInput() {
		if (m_maxDepth % 2 == 0 ) throw new RuntimeException("max depth is invalid, player can either look 1 or 3 ie. 2*n +1, n=0,1,... (1-st move for itself and 2-nd for opponent etc..) moves ahead! current depth set is :" + m_maxDepth + ". Default value is 1 ");
	}
	
}
