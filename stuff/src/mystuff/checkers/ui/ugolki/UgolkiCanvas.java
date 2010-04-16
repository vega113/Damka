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

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import mystuff.checkers.base.BaseAutomatePlayer;
import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.player.AlphaBetaPlayer;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;
import mystuff.checkers.ui.plaincheckers.CheckersCanvas;
import mystuff.checkers.ui.plaincheckers.CheckersData;
import mystuff.checkers.ui.plaincheckers.CheckersMove;
import mystuff.checkers.ui.plaincheckers.MainPlainCheckers;
import mystuff.checkers.ui.plaincheckers.CheckersCanvas.CanvasThread;

/**
 * @author Vega
 *
 */
public class UgolkiCanvas extends CheckersCanvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3272128782966878694L;
	protected TableState ugolkiState;
	/* (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersCanvas#doClickSquare(int, int, boolean)
	 */
	

	protected BaseAutomatePlayer automatePlayerBlue;
	protected BaseAutomatePlayer automatePlayerRed;
	protected int m_lastMoveCol = -1;
	protected int m_lastMoveRow = -1;
	
	public long autoSleepTime = 100; // time to sleep after auto click to select square

	public UgolkiCanvas(TableState ugolkiState2) {
		super();
		setUgolkiState(ugolkiState2);
	}

	protected void initAutomatePlayers() {
		BaseAutomatePlayer player = null;
		BaseAutomatePlayer player2 = null;
		try {
			Class playerClassRed = Class.forName(UgolkiConstants.CurrentPlayerRed);
			Class playerClassBlack = Class.forName(UgolkiConstants.CurrentPlayerBlack);
			try {
				try {
					player = (BaseAutomatePlayer)playerClassBlack.getConstructors()[1] .newInstance(new Object[] {UgolkiConstants.PlayerSideBlue,3});
					player2 = (BaseAutomatePlayer)playerClassRed.getConstructors()[1] .newInstance(new Object[] {UgolkiConstants.PlayerSideRed,3});
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
//		automatePlayerBlue = new AlphaBetaPlayer(UgolkiConstants.PlayerSideBlue,3);	
		if(isAutoBlack()){
			automatePlayerBlue = player;
		}
		if (isAutoRed()) {
			//		automatePlayerRed = null;
			automatePlayerRed = player2;
		}
		m_lastMoveCol = -1;
		m_lastMoveRow = -1;
	}

	/**
	 * @return
	 */
	protected CheckersData initData() {
		TableState newState = new TableState();
		UgolkiData ugolkiData = new UgolkiData(newState);
		setUgolkiState(newState);
		ugolkiData.setUpGame();
		return  ugolkiData;
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
	/**
	 * @param i 
	 * @return
	 */
	protected boolean isWinConditionForSideReached(int side) {
		boolean result = false;
		if(side == CheckersData.RED){
			result = getUgolkiState().isWinConditionReachedForSide(UgolkiConstants.PlayerSideRed);
		}
		else {
			result = getUgolkiState().isWinConditionReachedForSide(UgolkiConstants.PlayerSideBlue);
		}
		return result;
	}
	public boolean doNewGame() {
		boolean isSuperNewGame = super.doNewGame(true);
		return isSuperNewGame;
	}
	
	@Override
	protected CanvasThread initCanvasThread() {
		return  new UgolkiThread();
	}

	public  void doResign() {
		super.doResign();
		setUgolkiState(null);
	}

	/**
	 * @param g
	 */
	 protected void highlightPiecesCanBeMoved(Graphics g) {
		 // just override and do nothing
	 }

	 /**
	  * 
	  */
	 protected void highlightOnlyMove() {
//		 just override and do nothing
	 }
	 protected void doAutoPlay(int currentPlayer2) {
		 ((UgolkiThread)playThread).doAutoPlay(currentPlayer2);
	 }


	 @Override
		public void repaint() {
			mainPlainCheckers.mylog("repaint");
			Graphics g;
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		    g=getGraphics();
		    update(g);
		}

	@Override
	public void update(Graphics g) {
		super.update(g);
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersCanvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
//		mainPlainCheckers.mylog("IN ugolki paint");
		if (m_lastMoveCol >= 0) {
			
//			if (m_lastMoveCol >= 0) {
			if(getCurrentPlayer() == CheckersData.RED){
				g.setColor(Color.red.brighter());
			}else{
				g.setColor(Color.blue.darker());
			}
//			g.setColor(Color.blue.darker());
			
			g.drawRect(3 + m_lastMoveCol*SCALE, 3 + m_lastMoveRow*SCALE, RECTSIZE-3, RECTSIZE-3);
			g.drawRect(4 + m_lastMoveCol*SCALE, 4 + m_lastMoveRow*SCALE, RECTSIZE-4, RECTSIZE-4);
			g.setColor(Color.white);
		};
		
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersCanvas#doMovePiece(mystuff.checkers.ui.plaincheckers.CheckersMove)
	 */
	@Override
	protected void doMovePiece(CheckersMove move) {
		m_lastMoveCol = move.getToCol();
		m_lastMoveRow = move.getToRow();
		super.doMovePiece(move);
	}
	
	@Override
	protected void switchNextPlayer(int currentPlayer) {
		m_lastMoveCol = -1;
		super.switchNextPlayer(currentPlayer);
	}

	class UgolkiThread extends CheckersCanvas.CanvasThread {
		@Override
		 public void doAutoPlay(int currentPlayer2) {
			 if (currentPlayer2 == CheckersData.BLACK) {
				 subAutoPlay(automatePlayerBlue, CheckersData.BLACK,CheckersData.RED);
			 }
			 else {
				 subAutoPlay(automatePlayerRed, CheckersData.RED,CheckersData.BLACK);
			 }
		 }
		private void subAutoPlay(BaseAutomatePlayer autoPlayer, int playerType, int nextPlayerType){
//			mainPlainCheckers.mylog("Entering subAutoPlay: " + toPlayerSideId(playerType) );
			autoPlayer.setCurrentTableState(getUgolkiState());
			autoPlayer.execute();
			 TableMove move = autoPlayer.getNextMove();
//			 mainPlainCheckers.mylog(autoPlayer.getSide().getPlayerSymbol() + " click (from) on " + move.getFrom().toString());
			 setClickerId(toPlayerSideId(getCurrentPlayer()));//FIXME clickerId should be paremeter to pressMouseInternal
			 isTransmitClick = true;
			 doClickSquare ( move.getFrom().getXCoord(),move.getFrom().getYCoord());
			 paint(getGraphics());
			 try {
				Thread.sleep(autoSleepTime);
			} catch (InterruptedException e) {
			}
			setClickerId(toPlayerSideId(getCurrentPlayer()));//FIXME clickerId should be parameter to pressMouseInternal
			isTransmitClick = true;
			doClickSquare ( move.getTo().getXCoord(),move.getTo().getYCoord());
			paint(getGraphics());
//			mainPlainCheckers.mylog(autoPlayer.getSide().getPlayerSymbol() + " click (to) on " + move.getFrom().toString() + ", hval: " + move.getValue());
//			mainPlainCheckers.mylog("Exiting subAutoPlay: " );
		}
		
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersCanvas#msgBlackWins()
	 */
	@Override
	protected String[] msgBlackWins() {
		return new String[] {getBlackPLayerName() + " wins."};
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.ui.plaincheckers.CheckersCanvas#msgRedWins()
	 */
	@Override
	protected String[] msgRedWins() {
		return new String[] { getRedPLayerName() + " wins."};
	}
	 
	 
	

}
