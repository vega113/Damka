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
package mystuff.checkers;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import mystuff.checkers.base.BaseAutomatePlayer;
import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.exceptions.WinConditionReachedException;
import mystuff.checkers.interfaces.IPlayableTable;
import mystuff.checkers.player.AlphaBetaPlayer;
import mystuff.checkers.player.MinimaxPlayer;
import mystuff.checkers.player.MaxPlayer;
import mystuff.checkers.player.MinimaxPlayerV2;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.player.PrincipalVariationPlayer;
import mystuff.checkers.table.CellColor;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;

public class MainClass {

	private static Thread gameThread;

	//Display a message, preceded by the name of the current thread
	static void threadMessage(String message) {
		String threadName = Thread.currentThread().getName();
		Object[] obj = { threadName, message };
		//System.out.format("%s:\n %s%n", obj );
		Logger.global.log(Level.INFO, String.format("%s:\n %s%n", obj));
	}

	public static class GameLoop implements Runnable {
		IPlayableTable tableState = null;

		BaseAutomatePlayer bluePlayer = null;

		BaseAutomatePlayer redPlayer = null;

		private boolean m_isBluePlayerReachedWinCondition = false;

		private boolean m_isRedPlayerReachedWinCondition = false;

		private int m_bluePlayerLostMovesNumber = 0;

		private int m_redPlayerLostMovesNumber = 0;

		/**
		 * 
		 */
		public GameLoop() {
			super();
			tableState = new TableState();
			bluePlayer = new MinimaxPlayerV2(UgolkiConstants.PlayerSideBlue);
			redPlayer = new MinimaxPlayerV2(UgolkiConstants.PlayerSideRed);
			threadMessage("BluePlayer:" + "<" + UgolkiConstants.CellColorBlueIndex
					+ ">" + bluePlayer.getClass().getName());
			threadMessage("RedPlayer:" + "<" + UgolkiConstants.CellColorRedIndex
					+ ">" + redPlayer.getClass().getName());
		}
		
		public double getResultBlue(){
			return m_bluePlayerLostMovesNumber+1;
		}
		public double getResultRed(){
			return m_redPlayerLostMovesNumber +1;
		}

		public void run() {
			threadMessage("Starting Game");
			boolean side = false;
			int turns = 0;
			try {
				while (true) { // main game loop
					if (turns == UgolkiConstants.MaxMovesLimit) {
						threadMessage("" + turns
								+ " moves - taking too long, Aborting Game!!!");
						if (gameThread != null) {
							gameThread.interrupt();
							//Shouldn't be long now -- wait indefinitely
							gameThread.join();
						} else
							return;
					}
					if (turns == 108) {
						String  st = "-------###############################################--------\n";
						threadMessage(st + turns
								+ " moves - Damn the players are stupid!!!" + st);
						
//						tableState.toXmlString().toString();
					}
					if (m_isBluePlayerReachedWinCondition
							&& m_isRedPlayerReachedWinCondition) {
						// need to end the game
						String finalGameMessage = "Did Some player Win? Duhh....";
						if (m_bluePlayerLostMovesNumber == 0
								&& m_redPlayerLostMovesNumber > 0) {
							// Blue player have won by m_redPlayerLostMovesNumber moves
							finalGameMessage = "----##### Blue Player has WON by  "
									+ m_redPlayerLostMovesNumber
									+ " moves! #####-----";

						}
						if (m_redPlayerLostMovesNumber == 0
								&& m_bluePlayerLostMovesNumber > 0) {
							// Red player have won by m_bluePlayerLostMovesNumber moves
							finalGameMessage = "----##### Red Player has WON by  "
									+ m_bluePlayerLostMovesNumber
									+ " moves! #####-----";

						}
						threadMessage(finalGameMessage);
						return;
					}
					if (side) {// it's turn for Red to make the move
						if (m_isRedPlayerReachedWinCondition) {
							threadMessage("#####################################################################"
									+ '\n'
									+ "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
							threadMessage("Red Player has won and Makes No Move: #"
									+ turns++ );
							threadMessage(tableState.toString());
							threadMessage("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
									+ '\n'
									+ "#####################################################################");
							side = !side;
							continue;
						}
						if (m_isBluePlayerReachedWinCondition) {
							m_redPlayerLostMovesNumber++;
						}
						redPlayer.setCurrentTableState(tableState);
						redPlayer.execute();
						if (redPlayer.isReadyForMove()) {
							TableMove tableMove = redPlayer.getNextMove();
							try {
								threadMessage("#####################################################################"
										+ '\n'
										+ "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
								threadMessage("Red Player ("+ redPlayer.getSide().getPlayerSymbol()+")  Makes Move: #"
										+ turns++ + " " + tableMove.toString()
										+ " the move heuristic value is:"
										+ redPlayer.getStateValue());
								tableState.doMove(tableMove);
								threadMessage(tableState.toString());
							} catch (InvalidMoveException e) {
								e.printStackTrace();
							} catch (IndexOutOfBondsException e) {
								e.printStackTrace();
							}
						}
						
						//############## start check if win ######################
						if (tableState.isWinConditionReachedForSide(redPlayer
								.getSide())) {
							threadMessage("Player "
									+ redPlayer.getSide().getColorAsString()
									+ " with symbol "
									+ redPlayer.getSide().getPlayerSymbol()
									+ " reached win condition");
							m_isRedPlayerReachedWinCondition = true;
						}
//						############## end check if win ######################
						threadMessage(redPlayer.getMessage());
						threadMessage("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
								+ '\n'
								+ "#####################################################################");
						
						
						/*
						Logger.getLogger("Finer").log(Level.INFO,
								redPlayer.getMessage());
								*/
						side = !side;
						//Thread.sleep(1);
					} else { // it's turn for Blue to make the move
						if (m_isBluePlayerReachedWinCondition) {
							threadMessage("#####################################################################"
									+ '\n'
									+ "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
							threadMessage("Blue Player has won and Makes No Move: #"
									+ turns++ );
							threadMessage(tableState.toString());
							threadMessage("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
									+ '\n'
									+ "#####################################################################");
							side = !side;
							continue;
						}
						if (m_isRedPlayerReachedWinCondition) {
							m_bluePlayerLostMovesNumber++;
						}
						bluePlayer.setCurrentTableState(tableState);
						try {
							bluePlayer.execute();
						} catch (WinConditionReachedException e) {
							threadMessage(e.getMessage());
							PlayerSide playerSide = e.getSide();
							if (playerSide.getColor() == UgolkiConstants.CellColorBlueIndex) {
								m_isBluePlayerReachedWinCondition = true;
							} else {
								m_isRedPlayerReachedWinCondition = true;
							}

						}
						if (bluePlayer.isReadyForMove()) {
							TableMove tableMove = bluePlayer.getNextMove();
							try {
								threadMessage("Blue Player ("+ bluePlayer.getSide().getPlayerSymbol()+") : Makes Move: #"
										+ turns++ + " " + tableMove.toString()
										+ " the move heuristic value is:"
										+ bluePlayer.getStateValue());
								tableState.doMove(tableMove);
								threadMessage(tableState.toString());
							} catch (InvalidMoveException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IndexOutOfBondsException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
//						############## start check if win ######################
						if (tableState.isWinConditionReachedForSide(bluePlayer
								.getSide())) {
							threadMessage("Player "
									+ bluePlayer.getSide().getColorAsString()
									+ " with symbol "
									+ bluePlayer.getSide().getPlayerSymbol()
									+ " reached win condition");
							m_isBluePlayerReachedWinCondition = true;
						}
//						############## end check if win ######################
						
						/*
						Logger.getLogger("Finer").log(Level.INFO,
								bluePlayer.getMessage());
								*/
						threadMessage(bluePlayer.getMessage());
						side = !side;
						//Thread.sleep(500);
					}

				}
			} catch (InterruptedException e) {
				threadMessage("I wasn't done!");
			}

		}
	}

	public static void main1(String args[]) throws InterruptedException {
		Handler handler = null;
		;
		try {
			handler = new FileHandler("OutFile.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.global.addHandler(handler);

		try {
			handler = new FileHandler("OutFileFiner.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Handler[] parentHandlers = Logger.getLogger("Finer").getParent()
				.getHandlers();
		Logger.getLogger("Finer").getParent().removeHandler(parentHandlers[0]);
		Logger.getLogger("Finer").addHandler(handler);
		Logger.global.addHandler(parentHandlers[0]);

		//Delay, in milliseconds before we interrupt GameLoop
		//thread (default one hour).
		long patience = 1000 * 60 * 5;

		//If command line argument present, gives patience in seconds.
		if (args != null && args.length > 0) {
			try {
				patience = Long.parseLong(args[0]) * 1000;
			} catch (NumberFormatException e) {
				System.err.println("Argument must be an integer.");
				System.exit(1);
			}

		}

		threadMessage("Starting GameLoop thread");
		long startTime = System.currentTimeMillis();
		gameThread = new Thread(new GameLoop());
		gameThread.start();

		threadMessage("Waiting for GameLoop thread to finish");
		//loop until GameLoop thread exits
		while (gameThread.isAlive()) {
			long currentTime = System.currentTimeMillis();
			long playingTime = currentTime - startTime;
			threadMessage("Playing for " + Math.floor(playingTime / 1000)
					+ " seconds, time available to play: "
					+ Math.floor(patience - playingTime) + " seconds");
			//Wait maximum of 1 second for GameLoop thread to
			//finish.
			gameThread.join(1000 * 15);
			if (((System.currentTimeMillis() - startTime) > patience)
					&& gameThread.isAlive()) {
				threadMessage("Tired of waiting!");
				gameThread.interrupt();
				//Shouldn't be long now -- wait indefinitely
				gameThread.join();
			}

		}
		threadMessage("Finally!");
	}
	
	
	public static void main(String args[]) throws InterruptedException {
		main1(args);
//		GameLoop loop = new GameLoop();
//		gameThread = new Thread(loop);
//		gameThread.start();
//		System.out.println("blue: " + loop.getResultBlue() + ", red: " + loop.getResultRed());
//		loop.run();
//		main1(null);
//		System.out.println("blue: " + loop.getResultBlue() + ", red: " + loop.getResultRed());
		TableState s = new TableState();
		
//		s.print2dArr(s.gethTable());
		System.out.println("-----------------------------------------------------------");
//		s.print2dArr(s.getReverseHTable());
	}
	
}