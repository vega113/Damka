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
package mystuff.checkers.ui.ugolki;

import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.table.CellIndex;
import mystuff.checkers.table.PlayableTableCell;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;
import mystuff.checkers.ui.plaincheckers.CheckersData;
import mystuff.checkers.ui.plaincheckers.CheckersMove;

/**
 * @author Vega
 *
 */
public class UgolkiData extends CheckersData {
	private TableState ugolkiState;

	private UgolkiData() {
		 // Constructor.  Create the board and set it up for a new game.
	    setBoard( new int[8][8]);
	    setUgolkiState(ugolkiState);
	}

	public UgolkiData(TableState ugolkiState2) {
		setBoard( new int[8][8]);
	    setUgolkiState(ugolkiState2);
	    setUpGame();
	}

	/**
	 * @return the ugolkiState
	 */
	public TableState getUgolkiState() {
		return ugolkiState;
	}

	/**
	 * @param ugolkiState the ugolkiState to set
	 */
	public void setUgolkiState(TableState ugolkiState) {
		this.ugolkiState = ugolkiState;
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersData#setUpGame()
	 */
	@Override
	public void setUpGame() {
		 updateGameState();
	}

	/**
	 * 
	 */
	protected void updateGameState() {
		for (int row = 0; row < 8; row++) {
		       for (int col = 0; col < 8; col++) {
		    	   PlayableTableCell cell = null;
				try {
					cell = getUgolkiState().getCellByIndex(row,col);
				} catch (IndexOutOfBondsException e) {

					e.printStackTrace();
				}
		          if ( cell.getCellColor().getColor() == UgolkiConstants.CellColorBlueIndex) {
		        	  getBoard()[row][col] = BLACK;
		          }
		          else if ( cell.getCellColor().getColor() == UgolkiConstants.CellColorRedIndex) {
		            	 getBoard()[row][col] = RED;
		          }
		             else
		            	 getBoard()[row][col] = EMPTY;
		         
		       }
		    }
	}
	/*
	 * (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersData#getLegalMoves(int)
	 * @Override
	 */
	public CheckersMove[] getLegalMoves(int player) {
		if (player != RED && player != BLACK)
			return null;

		TableMove[] tableMove = null; 
		if(player == RED){
			tableMove = ugolkiState.getAllValidMovesForPlayerSide(UgolkiConstants.PlayerSideRed);
		}
		else  if(player == BLACK){
			tableMove = ugolkiState.getAllValidMovesForPlayerSide(UgolkiConstants.PlayerSideBlue);
		}

		/* If no legal moves have been found, return null.  Otherwise, create
       an array just big enough to hold all the legal moves, copy the
       legal moves from the vector into the array, and return the array. */

		if (tableMove.length == 0)
			return null;
		else {
			CheckersMove[] moveArray = new CheckersMove[tableMove.length];
			for (int i = 0; i < tableMove.length; i++)
				moveArray[i] = new UgolkiVisualMove(tableMove[i]);
			return moveArray;
		}

	}  // end getLegalMoves
	
	/*
	 * (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersData#getLegalJumpsFrom(int, int, int)
	 * @Override
	 */
	 public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
		 if (player != RED && player != BLACK)
				return null;
		 	
		 	mystuff.checkers.table.CellIndex[] cellIndexs = null; 
			PlayableTableCell currentCell = null;
			try {
				currentCell = ugolkiState.getCellByIndex(row, col);
				cellIndexs = ugolkiState.CollectAllValidForMoveIndexesForCell(currentCell);
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			}

			/* If no legal moves have been found, return null.  Otherwise, create
	       an array just big enough to hold all the legal moves, copy the
	       legal moves from the vector into the array, and return the array. */

			if (cellIndexs.length == 0)
				return null;
			else {
				CheckersMove[] moveArray = new CheckersMove[cellIndexs.length];
				for (int i = 0; i < cellIndexs.length; i++)
					moveArray[i] = new UgolkiVisualMove(row,col,cellIndexs[i].getXCoord(),cellIndexs[i].getYCoord());
				return moveArray;
			}
	 }  // end getLegalMovesFrom()
	 
	 /*
	  * (non-Javadoc)
	  * @see mystuff.checkers.ui.plaincheckers.CheckersData#makeMove(int, int, int, int)
	  * @Override
	  */
	 public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {

		 CellIndex from = new CellIndex(fromRow,fromCol);
		 CellIndex to = new CellIndex(toRow,toCol); 
		 TableMove tableMove  = new TableMove(from,to);
		 try {
			getUgolkiState().doMove(tableMove);
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		} catch (IndexOutOfBondsException e) {
			e.printStackTrace();
		}
		 updateGameState();
	 }


}
