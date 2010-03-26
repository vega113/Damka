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
package mystuff.checkers.ui.plaincheckers;

public class CheckersMove {
    // A CheckersMove object represents a move in the game of MainPlainCheckers.
    // It holds the row and column of the piece that is to be moved
    // and the row and column of the square to which it is to be moved.
    // (This class makes no guarantee that the move is legal.)
  int fromRow, fromCol;  // Position of piece to be moved.
  int toRow, toCol;      // Square it is to move to.
  public CheckersMove(int r1, int c1, int r2, int c2) {
       // Constructor.  Just set the values of the instance variables.
     fromRow = r1;
     fromCol = c1;
     toRow = r2;
     toCol = c2;
  }
  protected boolean isJump() {
       // Test whether this move is a jump.  It is assumed that
       // the move is legal.  In a jump, the piece moves two
       // rows.  (In a regular move, it only moves one row.)
     return (fromRow - toRow == 2 || fromRow - toRow == -2);
  }
/**
 * @return the fromCol
 */
public int getFromCol() {
	return fromCol;
}
/**
 * @param fromCol the fromCol to set
 */
public void setFromCol(int fromCol) {
	this.fromCol = fromCol;
}
/**
 * @return the fromRow
 */
public int getFromRow() {
	return fromRow;
}
/**
 * @param fromRow the fromRow to set
 */
public void setFromRow(int fromRow) {
	this.fromRow = fromRow;
}
/**
 * @return the toCol
 */
public int getToCol() {
	return toCol;
}
/**
 * @param toCol the toCol to set
 */
public void setToCol(int toCol) {
	this.toCol = toCol;
}
/**
 * @return the toRow
 */
public int getToRow() {
	return toRow;
}
/**
 * @param toRow the toRow to set
 */
public void setToRow(int toRow) {
	this.toRow = toRow;
}
}  // end class CheckersMove.