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
package mystuff.checkers.base; 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.interfaces.IPlayableTable;
import mystuff.checkers.interfaces.IPlayer;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.table.TableMove;
public abstract class BasePlayer extends BaseBean implements IPlayer{

	protected String m_message;
	protected long analyzingTime=0;
	protected long startTime;
	protected int m_depth;
//	protected ArrayList m_previousMovesList = null;
	protected TableMove m_lastMove;
	protected long  m_totalValidMoves = 0;
	protected long m_totalTime = 0;
	private PlayerSide m_playerSide;
	protected IPlayableTable m_currentTableState;
	protected TableMove[] m_validNextMoves;
	protected IPlayableTable m_previousTableState;
//	protected Stack m_stack;

	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayer#getMessage()
	 */
	public String getMessage() {
		return m_message;
	}
	public BasePlayer(PlayerSide side) {
		super();
		setSide(side);
//		m_stack = new Stack();
	}
	public void doMove(TableMove tableMove, IPlayableTable clonedTable){
		try { // make the move on table
			clonedTable.doMove(tableMove);
//			m_stack.push(tableMove);
		} catch (InvalidMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBondsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void undoMove(TableMove tableMove, IPlayableTable clonedTable) {
//		 mabe back move
//		TableMove backmove = new TableMove(tableMove.getTo(),tableMove.getFrom());
		try {
			clonedTable.undoLastMove();
//			m_stack.pop();
		} catch (InvalidMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBondsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void preExecute() {
		startTime = System.currentTimeMillis();
	}
	public void postExecute() {
//		if (getClonedTable().isWinConditionReachedForSide(getSide())){
//			throw new WinConditionReachedException(getSide());
//		}
		analyzingTime = Math.round((System.currentTimeMillis() - startTime)/1000);
		m_totalTime+=analyzingTime;
		m_message = this.getClass().getName() + " :: number of valid moves =  " + m_validNextMoves.length + ", computation time = " + analyzingTime + " seconds," +
		" total valid moves till now = " + m_totalValidMoves + " total analyze time = " + m_totalTime;
	}
	public void setSide(PlayerSide side) {
		m_playerSide = side;

	}

	public PlayerSide getSide() {
		return m_playerSide;
	}
	public void setCurrentTableState(IPlayableTable currentTableState) {
		m_previousTableState = m_currentTableState;
		m_currentTableState=currentTableState;
		m_validNextMoves = currentTableState.getAllValidMovesForPlayerSide(getSide());
		m_totalValidMoves+=m_validNextMoves.length;
	}
	protected String printStack(){
		String str ="The following moves lead to win : ";
		String moves ="";
//		for (Iterator iter = m_stack.iterator(); iter.hasNext();) {
//			TableMove move = (TableMove) iter.next();
//			moves=  moves +move.toString() +" ";
//			
//		}
		str = str + moves;
		System.out.println(str);
		return str;
		
	}
}
