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
 * @author Vega
 */
package mystuff.checkers.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.interfaces.IPlayableTable;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;

public class BaseAutomatePlayer extends BasePlayer {
	
	
	protected boolean m_isReady = false;
	protected TableMove m_nextBestMove = null;
	private double m_distance;
	private TableState m_clonedTable;
	protected final static double INFINITY = 100000.00;
	protected static final int MaxLookingBehindToPreventCycleLimit = 5;
	private int m_numberOfMovesTillMidGame = 10;
	private int m_numberOfMovesTillEndGame = 38;
	protected int m_maxDepth = 5;
	protected int m_globalMaxDepth =1;
	protected boolean isWinConditionReached = false;
	protected TableMove m_bestPlyMove;
	protected LinkedList m_bestPlyMoveList;
	protected int m_indexOfBestMove;
	protected int m_turnNumber=0;
	protected int m_totalConsideredMoves =0;
	protected LinkedList fewLastMoves = null;
	
	/**
	 * holds table with heuristic values to giv value to current board state
	 */
	private HeuristicTable heuristicTable;
	
	public void executeImp() {
		m_turnNumber++;
//		if(m_turnNumber > m_numberOfMovesTillEndGame){
////			 do fast lookup for win
//			sortMoves(m_validNextMoves,TableMove.DescValueMoveComparator);
//			for (int i = 0; i < m_validNextMoves.length; i++) {
//				doMove(m_validNextMoves[i]);
//				if(getClonedTable().isOldWinConditionReachedForSide(getSide())){
//					m_nextBestMove = m_validNextMoves[i];
//					undoLastMove();
//					m_isReady = true;
//					return;
//				}
//			}
//		}
		/*
		m_globalMaxDepth = m_maxDepth;
		m_maxDepth=1; // start from 1 move ahead
		// do the analyze in plies
		while(m_maxDepth <= m_globalMaxDepth){
			AnalyzeMoves();
			m_bestPlyMove = m_validNextMoves[m_indexOfBestMove];
			m_bestPlyMoveList.add(m_bestPlyMove);
			if(isWinConditionReached){
				break;
			}
			m_maxDepth+=2;
				
		}
		*/
		AnalyzeMoves();
	}

	

	public void preExecute() {
		super.preExecute();
		preAnalyzeMoves();
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayer#getStateValue()
	 */
	public double getStateValue() {
		return getDistance();
	}

	public BaseAutomatePlayer(PlayerSide side) {
		super(side);
//		m_previousMovesList = new ArrayList();
		m_bestPlyMoveList = new LinkedList();
		fewLastMoves = new LinkedList();
		
		heuristicTable = new HeuristicTable();// default heuristic values
	}	

	/**
	 *  put all pre AnalyzeMoves initializations here
	 *
	 */
	protected void preAnalyzeMoves(){
		try {
			m_clonedTable = (TableState)m_currentTableState.cloneMe();
			m_depth = 0;
			isWinConditionReached = false;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		m_distance = -10000;

		
	}
	protected void AnalyzeMoves() {
		if(m_validNextMoves != null){
			int size = m_validNextMoves.length;
			int indexOfBestMove = (int)Math.round(Math.random()*100) % size;
			m_nextBestMove = m_validNextMoves[indexOfBestMove];
			m_isReady = true;
		}
		
	}
	

	public double getHeuristicValueForBoardState(PlayerSide playerSide) {
		
		double[] k = {0.3,0.05,0.5,0.001,0.249};
//		double[] k = {0,0.0,1.0,0.0,0};
		//return getHeuristicDistanceForBoardState(playerSide);
		double result = k[0]*getClonedTable().getNormalizedL2DistanceForSide(playerSide) + 
		k[1]*getClonedTable().getNormalizedIndexDistanceForSide(playerSide)+ 
		k[2]* getClonedTable().getNormalizedHitWinPositionDistanceForSide(playerSide) + 
		k[3]*getClonedTable().getNormalizedLosePositionDistanceForSide(playerSide) 
		+ k[4]*getClonedTable().getNormalizedDistanceFromAverageIndex4Side(playerSide);
		return result;
	}
	
	protected double getL1DistanceFromWinForSide(PlayerSide playerSide){
		return 0;
	}
	
	/**
	 * 
	 * @param playerSide
	 * @return double - returns heuristic distance from win position
	 */
	
	protected double getHeuristicDistanceForBoardState(PlayerSide playerSide){
		double finalValue = 0;
		if (getSide().equals(playerSide)) {
			if (getClonedTable().isWinConditionReachedForSide(playerSide))
				finalValue+= INFINITY;
		}
		else{
			if (getClonedTable().isWinConditionReachedForSide(playerSide))
				finalValue+= -INFINITY;
		}
		double oursPercent = getPercentage4(getSide());
		double oppPercent = getPercentage4(getSide().getNextPlayerSide());
		finalValue +=  oursPercent - oppPercent;
		return finalValue;
	}
	private double getPercentage4(PlayerSide side) {
		if(side.getColor()== UgolkiConstants.CellColorBlueIndex){
			double blueper = m_clonedTable.getStartingDistanceFromWin4Blue()- m_clonedTable.getDistanceFromWinConditionForSide(side);
			blueper = blueper/(m_clonedTable.getStartingDistanceFromWin4Blue() - m_clonedTable.getFinalDistanceFromWin4Blue());
			return blueper;
		}
		else{
			double redper = m_clonedTable.getStartingDistanceFromWin4Red()- m_clonedTable.getDistanceFromWinConditionForSide(side);
			redper = redper/(m_clonedTable.getStartingDistanceFromWin4Red() - m_clonedTable.getFinalDistanceFromWin4Red());
			return redper;
		}
	}

	public TableMove getNextMove() {
		m_isReady = false;
		return m_nextBestMove;
	}

	public boolean isReadyForMove() {
		return m_isReady;
	}

	public IPlayableTable getPreviousTableState() {
		return m_previousTableState;
	}
	/**
	 * @return Returns the m_distance.
	 */
	public double getDistance() {
		return m_distance;
	}

	/**
	 * @param m_distance The m_distance to set.
	 */
	public void setDistance(double m_distance) {
		this.m_distance = m_distance;
	}

	/**
	 * @return Returns the m_clonedTable.
	 */
	protected TableState getClonedTable() {
		return m_clonedTable;
	}

	/**
	 * @param table The m_clonedTable to set.
	 */
	private void setClonedTable(TableState table) {
		m_clonedTable = table;
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BasePlayer#doMove(mystuff.checkers.table.TableMove, mystuff.checkers.interfaces.IPlayableTable)
	 */
	public void doMove(TableMove tableMove) {
		m_lastMove = tableMove;
//		m_previousMovesList.add(tableMove);
		super.doMove(tableMove, getClonedTable());
	}

	public void undoLastMove(){
		super.undoMove(null,getClonedTable());
//		m_previousMovesList.remove(m_previousMovesList.size()-1);
	}



	/**
	 * @return Returns the numberOfMovesB4MidGame.
	 */
	protected int getNumberOfMovesB4MidGame() {
		return m_numberOfMovesTillMidGame;
	}



	/**
	 * @param numberOfMovesB4MidGame The numberOfMovesB4MidGame to set.
	 */
	protected void setNumberOfMovesB4MidGame(int numberOfMovesB4MidGame) {
		this.m_numberOfMovesTillMidGame = numberOfMovesB4MidGame;
	}



	/**
	 * @return Returns the numberOfMovesTillEndGame.
	 */
	protected int getNumberOfMovesTillEndGame() {
		return m_numberOfMovesTillEndGame;
	}



	/**
	 * @param numberOfMovesTillEndGame The numberOfMovesTillEndGame to set.
	 */
	protected void setNumberOfMovesTillEndGame(int numberOfMovesTillEndGame) {
		this.m_numberOfMovesTillEndGame = numberOfMovesTillEndGame;
	}
	protected void sortMoves(TableMove[] validMoves, Comparator comparator) {
		for (int i = 0; i < validMoves.length; i++) {	
			doMove(validMoves[i]);
			validMoves[i].setValue(getHeuristicValueForBoardState(getSide()));
			undoLastMove();
		}
		Arrays.sort(validMoves, comparator);
		
	}
	protected boolean isTerminalCondition(int depth) {
		if (depth == 0 || getClonedTable().isWinConditionReachedForSide(getSide())){
			m_depth = depth;
			return true;
		}
		else 
			return false;
		
	}



	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BasePlayer#postExecute()
	 */
	@Override
	public void postExecute() {
		super.postExecute();
		if(fewLastMoves.contains(m_nextBestMove)){
			// We are in cycle!!! pick up random move
			int size = m_validNextMoves.length;
			int indexOfBestMove = (int)Math.round(Math.random()*100) % size;
			m_nextBestMove = m_validNextMoves[indexOfBestMove];
			m_isReady = true;
		}
		fewLastMoves.addLast(m_nextBestMove);
		if(fewLastMoves.size() > MaxLookingBehindToPreventCycleLimit){
			fewLastMoves.remove(0);
		}
		m_message=m_message+" total considered moves untill now : " + m_totalConsideredMoves+m_validNextMoves.length;
	}
	protected TableMove[] getValidMoves(PlayerSide side) {
		 TableMove[] outputMoves = getClonedTable().getAllValidMovesForPlayerSide(side);
		 m_totalConsideredMoves+=outputMoves.length;
		 return outputMoves;
	}
	
	public void setHeuristicTable(HeuristicTable ht){
		this.heuristicTable = ht;
	}
	
	protected boolean isHeuristicTable(){
		return this.heuristicTable != null;
	}



	/**
	 * @return the heuristicTable
	 */
	protected HeuristicTable getHeuristicTable() {
		return heuristicTable;
	}
}
