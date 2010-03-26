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

import java.awt.image.IndexColorModel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import sun.security.action.GetBooleanAction;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidJumpException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.interfaces.IGraphicsHandler;
import mystuff.checkers.interfaces.IPlayableTable;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.table.CellColor;
import mystuff.checkers.table.CellIndex;
import mystuff.checkers.table.PlayableTableCell;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;


/**
 * @author Vega
 *
 */

public abstract class BaseTableState implements IPlayableTable,Serializable{

	protected TableStateContainer m_currentStateContainer;
	
	
	protected CellIndex[] m_winIndexesForBlueSide;
	
	
	protected CellIndex[] m_winIndexesForRedSide;
	
	
	protected CellIndex m_centerIndexForWin4Blue;
	
	
	protected CellIndex m_centerIndexForWin4Red;
	
	
	protected double m_currentAverageL1Distance4Blue;
	
	
	protected double m_currentAverageL1Distance4Red;
	
	/**
	 * contains all Red indexes, it is updated every move
	 */	
//	
	protected HashMap m_currentAllIndexesForRedMap;
	
//	@Element(name=
	protected HashMap m_currentAllIndexesForBlueMap;
	/**
	 * L1 distance when in win position for <bold>Blue</bold> player
	 * since the distance is computed to the most outer(corner) cell
	 * even in win condition the distance will be greater than 0
	 */
	
	
	protected double m_winAverageL1Distance4Blue;
	
	/**
	 * L1 distance when in win position for <bold>Red</bold> player
	 * since the distance is computed to the most outer(corner) cell
	 * even in win condition the distance will be greater than 0
	 */	
	
	protected double m_winAverageL1Distance4Red;
	
	/**
	 * when computing heuristic distance, the second parameter that we can take in account
	 * in addition to distance, is difference in average index
	 * the "to" or the center index is floating
	 * in order to attract the movement into unoccupied cells
	 * This one is for Red
	 */
	
	protected RealCellIndex m_floatingWinCenterIndex4Red;
	
	
	/**
	 * when computing heuristic distance, the second parameter that we can take in account
	 * in addition to distance, is difference in average index
	 * the "to" or the center index is floating
	 * in order to attract the movement into unoccupied cells
	 * This one is for Blue
	 */
	
	protected RealCellIndex m_floatingWinCenterIndex4Blue;
	
	
	protected RealCellIndex m_currentFloatingCenterIndex4Red; //TODO add these 2 members to XML
	
	
	protected RealCellIndex m_currentFloatingCenterIndex4Blue;
	
	private int maxX;
	
	/**
	 * should contain the previous moves
	 */
	
	protected List m_historyMoves;
	
	
	protected TableMove m_lastMove;
	
	
	protected double m_currentAverageL2Distance4Blue;
	
	
	protected double m_currentAverageL2Distance4Red;
	
	
	protected CellIndex[] m_startIndexes4Blue;
	
	
	protected CellIndex[] m_startIndexes4Red;

	
	protected class TableStateContainer {
		private int xSize = UgolkiConstants.TABLEXSIZE;
		private int ySize = UgolkiConstants.TABLEYSIZE;
		private PlayableTableCell[][] board = new PlayableTableCell[xSize][ySize];
		public PlayableTableCell[][] getBoard() {
			return board;
		}
		public void setBoard(PlayableTableCell[][] board) {
			this.board = board;
		}
		/**
		 * Init the board 
		 *
		 */
		public TableStateContainer() {
//			create empty board
			for (int i = 0; i < xSize; i++) {
				for (int j = 0; j < ySize; j++) {
					CellIndex cellIndex = new CellIndex(i,j);
					board[i][j] = new PlayableTableCell(cellIndex);
				}
				
			}
			int playersSize = UgolkiConstants.PLAYERSSIZE;
			// mark the cells with colors
			if(playersSize == 2){
//				centerIndexForBlue = new CellIndex(xSize-2,1);
//				centerIndexForRed = new CellIndex(6,ySize-2);
				for (int i = xSize-3; i < xSize; i++) {
					for (int j = 0; j < 3; j++) {
						board[i][j].setCellColor(new CellColor(UgolkiConstants.CellColorBlueIndex));
					}				
				}
				for (int i = 0; i < 3; i++) {
					for (int j = ySize-3; j < ySize; j++) {
						board[i][j].setCellColor(new CellColor(UgolkiConstants.CellColorRedIndex));
					}				
				}
			}
			
			
		}
		
		
		
		public String toString() {
			String[] playerSigns = {UgolkiConstants.UNOCCUPIEDPLAYERSYMBOL,UgolkiConstants.REDPLAYERSYMBOL,UgolkiConstants.BLUEPLAYERSYMBOL};
			StringBuilder buffer = new StringBuilder();
			buffer.append("******************"+'\n');
			for (int i = 0; i < xSize; i++) {
				buffer.append(""+i+"|");
				for (int j = 0; j < ySize; j++) {
//					buffer.append(m_currentStateContainer.getBoard()[i][j].getCellColor().toString());
					buffer.append(playerSigns[m_currentStateContainer.getBoard()[i][j].getCellColor().getColor()]);
				}
				buffer.append('\n');
			}
			buffer.append("----------"+'\n');
			buffer.append("  abcdefgh"+'\n');
			buffer.append("******************"+'\n');
			return buffer.toString();
		}
		public int getXSize() {
			return xSize;
		}
		public void setXSize(int size) {
			xSize = size;
		}
		public int getYSize() {
			return ySize;
		}
		public void setYSize(int size) {
			ySize = size;
		}
	}


	public boolean isMoveValid(TableMove tableMove) {
		CellIndex fromIndex = tableMove.getFrom();
		CellIndex toIndex = tableMove.getTo();
		CellColor fromColor = m_currentStateContainer.getBoard()[fromIndex.getXCoord()][fromIndex.getYCoord()].getCellColor();
		CellColor toColor = m_currentStateContainer.getBoard()[toIndex.getXCoord()][toIndex.getYCoord()].getCellColor();
		CellColor unocuppiedColor = new CellColor(UgolkiConstants.CellColorUnoccupied);
		if(fromColor.equals(unocuppiedColor)) return false;
		if(!toColor.equals(unocuppiedColor)) return false;
		TableMove[] validMoves = getAllValidMovesForPlayerSide(new PlayerSide(fromColor));
		for (int i = 0; i < validMoves.length; i++) {
			if(validMoves[i].equals(tableMove)) return true;
		}
		return false;
	}

	public void Rewind(int movesToRewind) {
		
	}

	public void setCurrentState(TableState tableState) {
		
	}

	public BaseTableState() {
		
		
		init();
	}
	
	
	private void init() {
		m_currentAllIndexesForRedMap = new HashMap();
		m_currentAllIndexesForBlueMap = new HashMap();
		
		m_currentFloatingCenterIndex4Red = new RealCellIndex(6.0,1.0);
		m_floatingWinCenterIndex4Red = new RealCellIndex(1,6);
		
		m_currentFloatingCenterIndex4Blue = new RealCellIndex(1.0,6.0);
		m_floatingWinCenterIndex4Blue = new RealCellIndex(6,1);
		
		m_centerIndexForWin4Blue = new CellIndex(0,7); //Blue == 2 O
		m_centerIndexForWin4Red = new CellIndex(7,0); // Red == 1 X
		
		m_winAverageL1Distance4Blue = UgolkiConstants.winL1DistanceFromWin4Player;
		m_winAverageL1Distance4Red = UgolkiConstants.winL1DistanceFromWin4Player;
		m_currentStateContainer = new TableStateContainer();
		m_winIndexesForBlueSide = new CellIndex[9];
		m_winIndexesForBlueSide[0] =  new CellIndex(0,7);
		m_winIndexesForBlueSide[1] =  new CellIndex(0,6);
		m_winIndexesForBlueSide[2] =  new CellIndex(0,5);
		m_winIndexesForBlueSide[3] =  new CellIndex(1,7);
		m_winIndexesForBlueSide[4] =  new CellIndex(1,6);
		m_winIndexesForBlueSide[5] =  new CellIndex(1,5);
		m_winIndexesForBlueSide[6] =  new CellIndex(2,7);
		m_winIndexesForBlueSide[7] =  new CellIndex(2,6);
		m_winIndexesForBlueSide[8] =  new CellIndex(2,5);
		
		m_winIndexesForRedSide = new CellIndex[9];
		m_winIndexesForRedSide[0] =  new CellIndex(7,0);
		m_winIndexesForRedSide[1] =  new CellIndex(6,0);
		m_winIndexesForRedSide[2] =  new CellIndex(5,0);
		m_winIndexesForRedSide[3] =  new CellIndex(7,1);
		m_winIndexesForRedSide[4] =  new CellIndex(6,1);
		m_winIndexesForRedSide[5] =  new CellIndex(5,1);
		m_winIndexesForRedSide[6] =  new CellIndex(7,2);
		m_winIndexesForRedSide[7] =  new CellIndex(6,2);
		m_winIndexesForRedSide[8] =  new CellIndex(5,2);
		
		
		
		
		m_historyMoves = new ArrayList();
		// init position of Red and Blue Cells into HashMap and maintain them each move
		// no need to locate them from the start
		CellIndex[] tempAllCellIndexesForSide = null;
		tempAllCellIndexesForSide = getAllCellIndexesForPlayerSide(UgolkiConstants.PlayerSideRed);
		for (int i = 0; i < tempAllCellIndexesForSide.length; i++) {
			m_currentAllIndexesForRedMap.put(tempAllCellIndexesForSide[i].toString(),tempAllCellIndexesForSide[i]);
		}
		m_startIndexes4Red = (CellIndex[])m_currentAllIndexesForRedMap.values().toArray(new CellIndex[0]);
		tempAllCellIndexesForSide = getAllCellIndexesForPlayerSide(UgolkiConstants.PlayerSideBlue);
		for (int i = 0; i < tempAllCellIndexesForSide.length; i++) {
			m_currentAllIndexesForBlueMap.put(tempAllCellIndexesForSide[i].toString(),tempAllCellIndexesForSide[i]);
		}
		m_startIndexes4Blue = (CellIndex[])m_currentAllIndexesForBlueMap.values().toArray(new CellIndex[0]);
		
		m_currentAverageL1Distance4Red = computeL1DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,this);
		m_currentAverageL1Distance4Blue = computeL1DistanceFromWin4Side(UgolkiConstants.PlayerSideBlue,this);
		m_currentAverageL2Distance4Red = computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,this);
		m_currentAverageL2Distance4Blue = computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideBlue,this);
//		computeFloatingCenterIndex4Side(UgolkiConstants.PlayerSideBlue);
//		computeFloatingCenterIndex4Side(UgolkiConstants.PlayerSideRed);
		// init floating center of attraction indexes
		
		
		
	}

	public void Visualize(IGraphicsHandler graphicsHandler) {
		System.out.println(toString());
		
	}

	public String toString() {
		return m_currentStateContainer.toString();
	}

	protected TableStateContainer getCurrentStateContainer() {
		return m_currentStateContainer;
	}

	protected void setContainer(TableStateContainer m_container) {
		this.m_currentStateContainer = m_container;
	}

	public void restoreFromXml(String uri) {
		Logger.getLogger("Finer").info("-------############## Entering restoreFromXml(String uri) #################--------------------");
		clearSideColors();

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(uri));

			// normalize text representation
			doc.getDocumentElement().normalize();
			Logger.getLogger("Finer").info("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

			NodeList listOfPlayers = doc.getElementsByTagName("player");
			int totalPlayers = listOfPlayers.getLength();
			Logger.getLogger("Finer").info("Total no of players : " + totalPlayers);

			for (int s = 0; s < totalPlayers; s++) {

				Node playerNode = listOfPlayers.item(s);
				if (playerNode.getNodeType() == Node.ELEMENT_NODE) {

					org.w3c.dom.Element playerElement = (org.w3c.dom.Element) playerNode;
					boolean isInRedPlayer = playerElement.getAttribute("name")
							.equals(UgolkiConstants.RedPlayerName);
					// -------
					NodeList currentIndexNode = playerElement
							.getElementsByTagName("current_indexes");
					// ---------------
					NodeList currentIndexesList = ((org.w3c.dom.Element) currentIndexNode
							.item(0)).getElementsByTagName("index");
					// --------- ------------------
					int totalIndexes = currentIndexesList.getLength();
					Logger.getLogger("Finer").info("Total no of indexes in  current_indexes : "
									+ totalIndexes);
					StringBuffer stringBuffer = new StringBuffer();
					for (int k = 0; k < totalIndexes; k++) {
						Node indexNode = currentIndexesList.item(k);
						if (indexNode.getNodeType() == Node.ELEMENT_NODE) {
							org.w3c.dom.Element indexElement = (org.w3c.dom.Element) indexNode;
							int xCoord = Integer.parseInt(indexElement
									.getAttribute("xCoord"));
							int yCoord = Integer.parseInt(indexElement
									.getAttribute("yCoord"));
							CellIndex cellIndex = new CellIndex(xCoord, yCoord);
							PlayableTableCell cell = getCellByIndex(cellIndex);
							if (isInRedPlayer) {
								cell.setCellColor(UgolkiConstants.CellColorRed);
								m_currentAllIndexesForRedMap.put(cell
										.getCellIndex().toString(), cell
										.getCellIndex());
								stringBuffer.append(cell.getCellIndex().toString() +"\n");

							} else {
								cell
										.setCellColor(UgolkiConstants.CellColorBlue);
								m_currentAllIndexesForBlueMap.put(cell
										.getCellIndex().toString(), cell
										.getCellIndex());
								stringBuffer.append(cell.getCellIndex().toString() +"\n");
							}
						}

					}
					Logger.getLogger("Finer").info(stringBuffer.toString());

					// -------
					NodeList winIndexNode = playerElement
							.getElementsByTagName("win_indexes");
					// ---------------
					NodeList winIndexesList = ((org.w3c.dom.Element) winIndexNode.item(0))
							.getElementsByTagName("index");
					// --------- ------------------
					int totalWinIndexes = winIndexesList.getLength();
					Logger.getLogger("Finer").info("Total no of indexes in  win_indexes : "
									+ totalWinIndexes);
					
					stringBuffer = new StringBuffer();
					for (int k = 0; k < totalWinIndexes; k++) {
						Node indexNode = winIndexesList.item(k);
						if (indexNode.getNodeType() == Node.ELEMENT_NODE) {
							org.w3c.dom.Element indexElement = (org.w3c.dom.Element) indexNode;
							int xCoord = Integer.parseInt(indexElement
									.getAttribute("xCoord"));
							int yCoord = Integer.parseInt(indexElement
									.getAttribute("yCoord"));
							CellIndex cellIndex = new CellIndex(xCoord, yCoord);
							PlayableTableCell cell = getCellByIndex(cellIndex);
							if (isInRedPlayer) {
								m_winIndexesForRedSide[k] = cell.getCellIndex();
								stringBuffer.append(cell.getCellIndex().toString() +"\n");

							} else {
								m_winIndexesForBlueSide[k] = cell
										.getCellIndex();
								stringBuffer.append(cell.getCellIndex().toString() +"\n");
							}
						}

					}
					Logger.getLogger("Finer").info(stringBuffer.toString());
					//----- center_index_for_win_4_
					
					NodeList centerIndexList = null;
					if(isInRedPlayer){
						centerIndexList = playerElement.getElementsByTagName("center_index_for_win_4_red");
					}
					else{
						centerIndexList = playerElement.getElementsByTagName("center_index_for_win_4_blue");
					}
					NodeList indexList = null;
					if (centerIndexList.item(0).getNodeType() == Node.ELEMENT_NODE) {
						indexList = ((org.w3c.dom.Element) centerIndexList.item(0))
								.getElementsByTagName("index");
					}
					org.w3c.dom.Element indexElement = null;
					if (indexList.item(0).getNodeType() == Node.ELEMENT_NODE) {
						indexElement = ((org.w3c.dom.Element) indexList.item(0));
						int xCoord = Integer.parseInt(indexElement
								.getAttribute("xCoord"));
						int yCoord = Integer.parseInt(indexElement
								.getAttribute("yCoord"));
						if(isInRedPlayer){
							m_centerIndexForWin4Red = new CellIndex(xCoord, yCoord);
							Logger.getLogger("Finer").info("m_centerIndexForWin4Red : " + m_centerIndexForWin4Red.toString());
						}
						else{
							m_centerIndexForWin4Blue = new CellIndex(xCoord, yCoord);
							Logger.getLogger("Finer").info("m_centerIndexForWin4Blue : " + m_centerIndexForWin4Blue.toString());
						}
						
								
					}
					//-------------floating_center_index_4_
					NodeList floatingCenterIndexList = null;
					if(isInRedPlayer){
						centerIndexList = playerElement.getElementsByTagName("floating_center_index_4_red");
					}
					else{
						centerIndexList = playerElement.getElementsByTagName("floating_center_index_4_blue");
					}
					indexList = null;
					if (centerIndexList.item(0).getNodeType() == Node.ELEMENT_NODE) {
						indexList = ((org.w3c.dom.Element) centerIndexList.item(0))
								.getElementsByTagName("index");
					}
					indexElement = null;
					if (indexList.item(0).getNodeType() == Node.ELEMENT_NODE) {
						indexElement = ((org.w3c.dom.Element) indexList.item(0));
						double xCoord = Double.parseDouble(indexElement
								.getAttribute("xCoord"));
						double yCoord = Double.parseDouble(indexElement
								.getAttribute("yCoord"));
						if(isInRedPlayer){
							m_floatingWinCenterIndex4Red = new RealCellIndex(xCoord, yCoord);
							Logger.getLogger("Finer").info("m_floatingCenterIndex4Red : " + m_floatingWinCenterIndex4Red.toString());
						}
						else{
							m_floatingWinCenterIndex4Blue = new RealCellIndex(xCoord, yCoord);
							Logger.getLogger("Finer").info("m_floatingCenterIndex4Blue : " + m_floatingWinCenterIndex4Blue.toString());
						}
						
								
					}
					
					//------win_average_L1_distance_4_
					NodeList winL1DistList = null;
					if(isInRedPlayer){
						winL1DistList = playerElement.getElementsByTagName("win_average_L1_distance_4_red");
					}
					else{
						winL1DistList = playerElement.getElementsByTagName("win_average_L1_distance_4_blue");
					}
					org.w3c.dom.Element winL1DistElement = null;
					if (winL1DistList.item(0).getNodeType() == Node.ELEMENT_NODE) {
						winL1DistElement = (org.w3c.dom.Element)winL1DistList.item(0);
					}
					if(isInRedPlayer){
						m_winAverageL1Distance4Red = Double.parseDouble(winL1DistElement.getFirstChild().getNodeValue());
						Logger.getLogger("Finer").info("m_winAverageL1Distance4Red : " + m_winAverageL1Distance4Red);
					}
					else{
						m_winAverageL1Distance4Blue = Double.parseDouble(winL1DistElement.getFirstChild().getNodeValue());
						Logger.getLogger("Finer").info("m_winAverageL1Distance4Blue : " + m_winAverageL1Distance4Blue);
					}
					
					//----------------current_average_L1_distance_4_blue
					NodeList curL1DistList = null;
					if(isInRedPlayer){
						curL1DistList = playerElement.getElementsByTagName("current_average_L1_distance_4_red");
					}
					else{
						curL1DistList = playerElement.getElementsByTagName("current_average_L1_distance_4_blue");
					}
					org.w3c.dom.Element curL1DistElement = null;
					if (curL1DistList.item(0).getNodeType() == Node.ELEMENT_NODE) {
						curL1DistElement = (org.w3c.dom.Element)winL1DistList.item(0);
					}
					if(isInRedPlayer){
						m_currentAverageL1Distance4Red = Double.parseDouble(curL1DistElement.getFirstChild().getNodeValue());
						System.out.println("m_currentAverageL1Distance4Red : " + m_currentAverageL1Distance4Red);
					}
					else{
						m_currentAverageL1Distance4Blue = Double.parseDouble(curL1DistElement.getFirstChild().getNodeValue());
						System.out.println("m_currentAverageL1Distance4Blue : " + m_currentAverageL1Distance4Blue);
					}
				}// end of if clause

			}// end of for loop with s var
			// ------------ common
			NodeList common = doc.getElementsByTagName("common");
			// ----------lastMove
			NodeList lastMoveList = null;
			if (common.item(0).getNodeType() == Node.ELEMENT_NODE) {
				lastMoveList = ((org.w3c.dom.Element) common.item(0))
						.getElementsByTagName("last_move");
			}
			if (lastMoveList.item(0).getNodeType() == Node.ELEMENT_NODE) {
				readMoveFromXml(((org.w3c.dom.Element) lastMoveList.item(0)), m_lastMove);
			}

			// historyMoves
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("history_moves"+"\n");
			NodeList historyMoveList = null;
			if (common.item(0).getNodeType() == Node.ELEMENT_NODE) {
				historyMoveList = ((org.w3c.dom.Element) common.item(0))
						.getElementsByTagName("history_moves");
			}
			org.w3c.dom.Element movesListElement = null;
			if (historyMoveList.item(0).getNodeType() == Node.ELEMENT_NODE) {
				movesListElement = ((org.w3c.dom.Element) historyMoveList.item(0));
			}
			NodeList moveList = movesListElement.getElementsByTagName("move");

			int historyMoveListLength = moveList.getLength();
			for (int i = 0; i < historyMoveListLength; i++) {
				if (moveList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					TableMove currentHistoryMove = new TableMove();
					readMoveFromXml(((org.w3c.dom.Element) moveList.item(i)),
							currentHistoryMove);
					m_historyMoves.add(i, currentHistoryMove);
					stringBuffer.append(currentHistoryMove.toString()+"\n");
				}
			}
			Logger.getLogger("Finer").info(stringBuffer.toString());
			Logger.getLogger("Finer").info("-------############## Exiting restoreFromXml(String uri) #################--------------------");

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void readMoveFromXml(org.w3c.dom.Element element, TableMove move) {
		if(element.getElementsByTagName("from").item(0).getNodeType() == Node.ELEMENT_NODE){
			NodeList indexNodeList = ((org.w3c.dom.Element)element.getElementsByTagName("from").item(0)).getElementsByTagName("index");
			  if(indexNodeList.item(0).getNodeType() == Node.ELEMENT_NODE){
				  org.w3c.dom.Element indexElement = (org.w3c.dom.Element)indexNodeList.item(0);
				  int xCoord = Integer.parseInt(indexElement.getAttribute("xCoord"));
					int yCoord = Integer.parseInt(indexElement.getAttribute("yCoord"));
					CellIndex fromIndex = new CellIndex(xCoord,yCoord);
					move.setFrom(fromIndex);
			  }
			  indexNodeList = ((org.w3c.dom.Element)element.getElementsByTagName("to").item(0)).getElementsByTagName("index");
			  if(indexNodeList.item(0).getNodeType() == Node.ELEMENT_NODE){
				  org.w3c.dom.Element indexElement = (org.w3c.dom.Element)indexNodeList.item(0);
				  int xCoord = Integer.parseInt(indexElement.getAttribute("xCoord"));
					int yCoord = Integer.parseInt(indexElement.getAttribute("yCoord"));
					CellIndex toIndex = new CellIndex(xCoord,yCoord);
					move.setTo(toIndex);
			  }
			
		}
		
	}

	public StringReader toXmlString() {
		StringWriter stringOut = null;
		try {
			Document doc = new DocumentImpl();
			Map tmpAllSideIndexesMap = null;
			CellIndex[] tmpAllSideIndexesArray = null;

			// Create Root org.w3c.dom.Element
			org.w3c.dom.Element root = doc.createElement("root_ugolki");
			
			
//			 Create Second Level - Common
			org.w3c.dom.Element itemCommon = doc.createElement("common");
			root.appendChild(itemCommon);
			// create third level - last move
			org.w3c.dom.Element itemLastMove = doc.createElement("last_move");
			itemCommon.appendChild(itemLastMove);
			
			// create 4-th level 
			if(m_lastMove != null){
				xmlAddMoveToParentNode(doc,itemLastMove,m_lastMove);
			}
			
			// create third level  - history moves
			org.w3c.dom.Element itemHistoryMoves = doc.createElement("history_moves");
			itemCommon.appendChild(itemHistoryMoves);
			int indexMove = 0;
			for (Iterator iter = m_historyMoves.iterator();  iter.hasNext();) {
				TableMove move = (TableMove) iter.next();
				indexMove++;
//				 create 4-th level 
				org.w3c.dom.Element itemMove = doc.createElement("move");
				itemHistoryMoves.appendChild(itemMove);
				itemMove.setAttribute("index", ""+indexMove);
				xmlAddMoveToParentNode(doc,itemMove,move);
				
			}
			
			
			// Create Second Level - Blue Player
			org.w3c.dom.Element itemPlayer = doc.createElement("player");
			itemPlayer.setAttribute("name", UgolkiConstants.BluePlayerName);
			itemPlayer.setAttribute("symbol", UgolkiConstants.BLUEPLAYERSYMBOL);
			root.appendChild(itemPlayer);
			// create third level - current indexes
			org.w3c.dom.Element itemCurrentIndexes = doc.createElement("current_indexes");
			itemPlayer.appendChild(itemCurrentIndexes);
			tmpAllSideIndexesMap = m_currentAllIndexesForBlueMap;
			Set keyset = tmpAllSideIndexesMap.keySet();
			for (Iterator iter = keyset.iterator(); iter.hasNext();) {
				// create fourth level - index
				CellIndex index = (CellIndex) tmpAllSideIndexesMap
						.get(iter.next());
				org.w3c.dom.Element itemIndex = doc.createElement("index");
				itemIndex.setAttribute("xCoord",""+index.getXCoord());
				itemIndex.setAttribute("yCoord",""+index.getYCoord());
				itemCurrentIndexes.appendChild(itemIndex);
			}
			
//			 create third level - win indexes
			org.w3c.dom.Element itemWinIndexes = doc.createElement("win_indexes");
			itemPlayer.appendChild(itemWinIndexes);
			tmpAllSideIndexesArray = m_winIndexesForBlueSide;
			for (int i = 0; i < tmpAllSideIndexesArray.length; i++) {
				CellIndex index = tmpAllSideIndexesArray[i];
				xmlAddIndexToParentNode(doc,itemWinIndexes,index);
			}
//			keyset = tmpAllSideIndexesMap.keySet();
//			for (Iterator iter = keyset.iterator(); iter.hasNext();) {
//				// create fourth level - index
//				CellIndex index = (CellIndex) tmpAllSideIndexesMap
//						.get(iter.next());
//				xmlAddIndexToParentNode(doc,itemWinIndexes,index);
//			}
			
			// create third level - centerIndexForWin4Blue
			org.w3c.dom.Element itemCenterIndexForWin4Blue = doc.createElement("center_index_for_win_4_blue");
			itemPlayer.appendChild(itemCenterIndexForWin4Blue);
			xmlAddIndexToParentNode(doc,itemCenterIndexForWin4Blue,m_centerIndexForWin4Blue);
			
			
//			 create third level - FloatingCenterIndexFor4Blue
			org.w3c.dom.Element itemFloatingCenterIndexFor4Blue = doc.createElement("floating_center_index_4_blue");
			itemPlayer.appendChild(itemFloatingCenterIndexFor4Blue);
			xmlAddIndexToParentNode(doc,itemFloatingCenterIndexFor4Blue,m_floatingWinCenterIndex4Blue);
			
			
//			 create third level - winAverageL1Distance4Blue
				org.w3c.dom.Element itemWinAverageL1Distance4Blue = doc.createElement("win_average_L1_distance_4_blue");
				itemPlayer.appendChild(itemWinAverageL1Distance4Blue);
				Text textDistBlue = doc.createTextNode(""+m_winAverageL1Distance4Blue);
				itemWinAverageL1Distance4Blue.appendChild(textDistBlue);
				

//				 create third level - currentAverageL1Distance4Blue
				org.w3c.dom.Element itemCurrentAverageL1Distance4Blue = doc.createElement("current_average_L1_distance_4_blue");
				itemPlayer.appendChild(itemCurrentAverageL1Distance4Blue);
				Text textCurrentDistBlue = doc.createTextNode(""+m_currentAverageL1Distance4Blue);
				itemCurrentAverageL1Distance4Blue.appendChild(textCurrentDistBlue);
				
				
			
			

//				 Create Second Level - RedPlayer
				org.w3c.dom.Element itemRedPlayer = doc.createElement("player");
				itemRedPlayer.setAttribute("name", UgolkiConstants.RedPlayerName);
				itemRedPlayer.setAttribute("symbol", UgolkiConstants.REDPLAYERSYMBOL);
				root.appendChild(itemRedPlayer);
				// create third level - current indexes
				org.w3c.dom.Element itemRedCurrentIndexes = doc.createElement("current_indexes");
				itemRedPlayer.appendChild(itemRedCurrentIndexes);
				tmpAllSideIndexesMap = m_currentAllIndexesForRedMap;
				keyset = tmpAllSideIndexesMap.keySet();
				for (Iterator iter = keyset.iterator(); iter.hasNext();) {
					// create fourth level - index
					CellIndex index = (CellIndex) tmpAllSideIndexesMap
							.get(iter.next());
					org.w3c.dom.Element itemIndex = doc.createElement("index");
					itemIndex.setAttribute("xCoord",""+index.getXCoord());
					itemIndex.setAttribute("yCoord",""+index.getYCoord());
					itemRedCurrentIndexes.appendChild(itemIndex);
				}
				
//				 create third level - win indexes
				org.w3c.dom.Element itemRedWinIndexes = doc.createElement("win_indexes");
				itemRedPlayer.appendChild(itemRedWinIndexes);
				tmpAllSideIndexesArray = m_winIndexesForRedSide;
				for (int i = 0; i < tmpAllSideIndexesArray.length; i++) {
					CellIndex index = tmpAllSideIndexesArray[i];
					xmlAddIndexToParentNode(doc,itemRedWinIndexes,index);
				}
//				keyset = tmpAllSideIndexesMap.keySet();
//				for (Iterator iter = keyset.iterator(); iter.hasNext();) {
//					// create fourth level - index
//					CellIndex index = (CellIndex) tmpAllSideIndexesMap
//							.get(iter.next());
//					xmlAddIndexToParentNode(doc,itemRedWinIndexes,index);
//				}
				
//				 create third level - centerIndexForWin4Red
				org.w3c.dom.Element itemCenterIndexForWin4Red = doc.createElement("center_index_for_win_4_red");
				itemRedPlayer.appendChild(itemCenterIndexForWin4Red);
				xmlAddIndexToParentNode(doc,itemCenterIndexForWin4Red,m_centerIndexForWin4Red);
				
//				 create third level - FloatingCenterIndexFor4Red
				org.w3c.dom.Element itemFloatingCenterIndexFor4Red = doc.createElement("floating_center_index_4_red");
				itemRedPlayer.appendChild(itemFloatingCenterIndexFor4Red);
				xmlAddIndexToParentNode(doc,itemFloatingCenterIndexFor4Red,m_floatingWinCenterIndex4Red);
				
//				 create third level - winAverageL1Distance4Red
				org.w3c.dom.Element itemWinAverageL1Distance4Red = doc.createElement("win_average_L1_distance_4_red");
				itemRedPlayer.appendChild(itemWinAverageL1Distance4Red);
				Text textDistRed = doc.createTextNode(""+m_winAverageL1Distance4Red);
				itemWinAverageL1Distance4Red.appendChild(textDistRed);
				
//				 create third level - winAverageL1Distance4Red
				org.w3c.dom.Element itemCurrentAverageL1Distance4Red = doc.createElement("current_average_L1_distance_4_red");
				itemRedPlayer.appendChild(itemCurrentAverageL1Distance4Red);
				Text textCurrentDistRed = doc.createTextNode(""+m_currentAverageL1Distance4Red);
				itemCurrentAverageL1Distance4Red.appendChild(textCurrentDistRed);
				
				
				
				
				
			doc.appendChild(root);
//			Serialize DOM
			 OutputFormat format = new OutputFormat (doc);
			 // as a String
			 stringOut = new StringWriter ();
			 XMLSerializer serial = new XMLSerializer (stringOut,
			 format);
			 serial.serialize(doc);
			 // Display the XML
			 String xmlString = stringOut.toString();
			 System.out.println(xmlString);
			 
			 XMLSerializer serializer = new XMLSerializer (
					    new FileWriter("output.xml"), format);
					serializer.asDOMSerializer();
					serializer.serialize(doc);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}


		return null;
	}
	
	private org.w3c.dom.Element xmlAddIndexToParentNode(Document doc, org.w3c.dom.Element parentElement, CellIndex index){
		org.w3c.dom.Element indexElement = doc.createElement("index");
		if(index != null){
			indexElement.setAttribute("xCoord",""+index.getXCoord());
			indexElement.setAttribute("yCoord",""+index.getYCoord());
		}
		else {
			indexElement.setAttribute("xCoord","-1");
			indexElement.setAttribute("yCoord","-1");
		}
		parentElement.appendChild(indexElement);
		return parentElement;
	}
	private org.w3c.dom.Element xmlAddIndexToParentNode(Document doc, org.w3c.dom.Element parentElement, RealCellIndex index){
		org.w3c.dom.Element indexElement = doc.createElement("index");
		if(index != null){
			indexElement.setAttribute("xCoord",""+index.getXCoord());
			indexElement.setAttribute("yCoord",""+index.getYCoord());
		}
		else {
			indexElement.setAttribute("xCoord","-1");
			indexElement.setAttribute("yCoord","-1");
		}
		parentElement.appendChild(indexElement);
		return parentElement;
	}
	private org.w3c.dom.Element xmlAddMoveToParentNode(Document doc, org.w3c.dom.Element parentElement, TableMove move){
//		 create 4-th level - from
		org.w3c.dom.Element itemFrom = doc.createElement("from");
		parentElement.appendChild(itemFrom);
		
		// create 5-th level index from
		xmlAddIndexToParentNode(doc,itemFrom,move.getFrom());
		
//		 create 4-th level - to
		org.w3c.dom.Element itemTo = doc.createElement("to");
		parentElement.appendChild(itemTo);
		
		// create 5-th level index to
		xmlAddIndexToParentNode(doc,itemTo,move.getTo());
		return parentElement;
	}
	
	private List CollectOneJumpCloseValidIndexesForCell(CellIndex currentCellIndex, boolean isInJump) throws IndexOutOfBondsException, InvalidJumpException{
		PlayableTableCell cell = getCellByIndex(currentCellIndex);
		
		if(!isValidIndex(currentCellIndex) || (!isInJump&& cell.getCellColor().getColor() == UgolkiConstants.CellColorUnoccupied))
			throw new InvalidJumpException(currentCellIndex);
		// check for each cell the 4 possible jump locations
		// first check that there is occupied cell adjucent in each of 4 possible directions
		// then check that the jump location is legal to move into
		List validOneJumpIndexesListForCell = new ArrayList();
		//check up
		CellIndex adjIndex = currentCellIndex.getUp();
		CellIndex distIndex = currentCellIndex.getJumpUp();
		if(isCellValidAndOccupied(adjIndex) && isCellValidAndEmpty(distIndex) )
			validOneJumpIndexesListForCell.add(distIndex);
//		check down
		adjIndex = currentCellIndex.getDown();
		distIndex = currentCellIndex.getJumpDown();
		if(isCellValidAndOccupied(adjIndex) && isCellValidAndEmpty(distIndex) )
			validOneJumpIndexesListForCell.add(distIndex);
//		check left
		adjIndex = currentCellIndex.getLeft();
		distIndex = currentCellIndex.getJumpLeft();
		if(isCellValidAndOccupied(adjIndex) && isCellValidAndEmpty(distIndex) )
			validOneJumpIndexesListForCell.add(distIndex);
//		check right
		adjIndex = currentCellIndex.getRight();
		distIndex = currentCellIndex.getJumpRight();
		if(isCellValidAndOccupied(adjIndex) && isCellValidAndEmpty(distIndex) )
			validOneJumpIndexesListForCell.add(distIndex);
		

		return validOneJumpIndexesListForCell;
	}
	
	/**
	 * @param currentCellIndex
	 * @return
	 * @throws IndexOutOfBondsException
	 */
	private List CollectNoJumpValidIndexesForCell(CellIndex currentCellIndex) throws IndexOutOfBondsException{
		PlayableTableCell cell = getCellByIndex(currentCellIndex);
		
		if(!isValidIndex(currentCellIndex) || cell.getCellColor().equals(new CellColor(UgolkiConstants.CellColorUnoccupied)))
			throw new IndexOutOfBondsException(currentCellIndex);
		List validNoJumpIndexesListForCell = new ArrayList();
		//check up
		CellIndex adjIndex = currentCellIndex.getUp();
		if(isCellValidAndEmpty(adjIndex))
			validNoJumpIndexesListForCell.add(adjIndex);
//		check down
		adjIndex = currentCellIndex.getDown();
		if(isCellValidAndEmpty(adjIndex) )
			validNoJumpIndexesListForCell.add(adjIndex);
//		check left
		adjIndex = currentCellIndex.getLeft();
		if(isCellValidAndEmpty(adjIndex))
			validNoJumpIndexesListForCell.add(adjIndex);
//		check right
		adjIndex = currentCellIndex.getRight();
		if(isCellValidAndEmpty(adjIndex))
			validNoJumpIndexesListForCell.add(adjIndex);
		return validNoJumpIndexesListForCell;
	}
	
	/**
	 * given cell, the function returns valid indexes to move into. 
	 * @param cell
	 * @return
	 * @throws IndexOutOfBondsException
	 */
	public CellIndex[] CollectAllValidForMoveIndexesForCell(PlayableTableCell cell) throws IndexOutOfBondsException{
		List allValidIndexesListForCell = new ArrayList();
		List tempAllValidIndexesListForCell = new ArrayList();
		List tempList = null;
		try {
			tempList = CollectOneJumpCloseValidIndexesForCell(cell.getCellIndex(), false);
		} catch (IndexOutOfBondsException e1) {
			e1.printStackTrace();
		} catch (InvalidJumpException e1) {
			e1.printStackTrace();
		}
		if(tempList.size() > 0){
			tempAllValidIndexesListForCell.addAll(tempList);
		}
		while (tempAllValidIndexesListForCell.size() > 0){
			CellIndex tempIndex = (CellIndex)tempAllValidIndexesListForCell.remove(0);
			allValidIndexesListForCell.add(tempIndex);
			try {
				tempList = CollectOneJumpCloseValidIndexesForCell(tempIndex, true);
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			} catch (InvalidJumpException e) {
				e.printStackTrace();
			}
			for (Iterator iter = tempList.iterator(); iter.hasNext();) {
				CellIndex element = (CellIndex) iter.next();
				if(!allValidIndexesListForCell.contains(element))
					tempAllValidIndexesListForCell.add(element);
			}
		}
		allValidIndexesListForCell.addAll(CollectNoJumpValidIndexesForCell(cell.getCellIndex()));// add the no jump indexes
		return (CellIndex[])allValidIndexesListForCell.toArray(new CellIndex[0]);
	}
	private boolean isCellValidAndEmpty(CellIndex cellIndex) throws IndexOutOfBondsException{
		 return isValidIndex(cellIndex)&& getCellByIndex(cellIndex).getCellColor().equals(new CellColor(UgolkiConstants.CellColorUnoccupied));
		
	}
	public PlayableTableCell getCellByIndex(CellIndex cellIndex) throws IndexOutOfBondsException {
		if(!isValidIndex(cellIndex)) throw new IndexOutOfBondsException(cellIndex);
		return getCurrentStateContainer().getBoard()[cellIndex.getXCoord()][cellIndex.getYCoord()];
	}
	/*
	 * checks that index is in bounds
	 */
	/**
	 * @param cellIndex
	 * @return
	 */
	public boolean isValidIndex(CellIndex cellIndex){
		CellIndex currentCellIndex = cellIndex;
		int maxX = getCurrentStateContainer().getXSize();
		int maxY = getCurrentStateContainer().getYSize();
		return currentCellIndex.getXCoord() < maxX && currentCellIndex.getXCoord() >= 0 && currentCellIndex.getYCoord() < maxY 
		&& currentCellIndex.getYCoord() >= 0;
	}
	public boolean isCellValidAndOccupied(CellIndex cellIndex) throws IndexOutOfBondsException{
		 return isValidIndex(cellIndex)&& !getCellByIndex(cellIndex).getCellColor().equals(new CellColor(UgolkiConstants.CellColorUnoccupied));
	}
	
	/**
	 * @param playerSide
	 * @return
	 * @deprecated use getFastAllCellIndexesForPlayerSide
	 */
	public CellIndex[] getAllCellIndexesForPlayerSide(BasePlayerSide playerSide){
		List allCellIndexesForPlayerSideList = new LinkedList();
		maxX = getCurrentStateContainer().getXSize();
		int maxY = getCurrentStateContainer().getYSize();
		PlayableTableCell[][] playableTableCells =  getCurrentStateContainer().getBoard();
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				if(playableTableCells[i][j].getCellColor().getColor() == playerSide.getColor()){
					allCellIndexesForPlayerSideList.add(playableTableCells[i][j].getCellIndex());
				}
			}
		}
		return (CellIndex[])allCellIndexesForPlayerSideList.toArray(new CellIndex[0]);
	
	}
	
	public Map getFastAllCellIndexesForPlayerSide(BasePlayerSide playerSide){
		
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			return m_currentAllIndexesForBlueMap;
		}
		else {
			return m_currentAllIndexesForRedMap;
		}
	
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayableTable#getAllValidMovesForPlayerSide(mystuff.checkers.base.BasePlayerSide)
	 */
	public TableMove[] getAllValidMovesForPlayerSide(BasePlayerSide playerSide) {
		CellColor currentColor = new CellColor(playerSide.getColor());
		List allValidMovesForPlayerSideList  = new LinkedList();
		CellIndex[] cellIndexesForPlayerSide = getAllCellIndexesForPlayerSide(playerSide);
		for (int i = 0; i < cellIndexesForPlayerSide.length; i++) {
			CellIndex currentCellIndex = cellIndexesForPlayerSide[i];
			CellIndex[] validIndexesToMove = null;
			try {
				validIndexesToMove = CollectAllValidForMoveIndexesForCell(new PlayableTableCell(currentColor,currentCellIndex));
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			}
			if(validIndexesToMove != null){
				for (int j = 0; j < validIndexesToMove.length; j++) {
					TableMove tableMove = new TableMove(currentCellIndex,validIndexesToMove[j]);
					allValidMovesForPlayerSideList.add(tableMove);
				}
			}
			
		}
		return (TableMove[])allValidMovesForPlayerSideList.toArray(new TableMove[0]);
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayableTable#forceMakeMove()
	 */
	/**
	 * @deprecated
	 * use doMove, undoLastMove instead
	 */
	public IPlayableTable forceMakeMove(TableMove tableMove) throws InvalidMoveException, IndexOutOfBondsException {
		CellIndex fromIndex = tableMove.getFrom();
		CellIndex toIndex = tableMove.getTo();
		//FIXME well-supposed to be force move, i will implement later soft move that will check things
		//if(!isCellValidAndOccupied(fromIndex)) throw new InvalidMoveException(tableMove);
		//if(!isCellValidAndEmpty(toIndex)) throw new InvalidMoveException(tableMove);
		if(!isValidIndex(fromIndex)) throw new IndexOutOfBondsException(fromIndex);
		if(!isValidIndex(toIndex)) throw new IndexOutOfBondsException(toIndex);
		PlayableTableCell fromCell= getCellByIndex(fromIndex);
		PlayableTableCell toCell= getCellByIndex(toIndex);
		CellColor fromColor = fromCell.getCellColor();
		CellColor toColor = toCell.getCellColor();
		toCell.setCellColor(fromColor);
		fromCell.setCellColor(toColor);
		return this;
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayableTable#softMakeMove()
	 */
	public IPlayableTable softMakeMove(TableMove tableMove) {
		return null;
	}

	/* (non-Javadoc)
	 * @see mystuff.checkers.interfaces.IPlayableTable#getWinConditionForPlayerSide(mystuff.checkers.base.BasePlayerSide)
	 */
	/**
	 * @deprecated
	 */
	public boolean isWinConditionReachedForSide(BasePlayerSide player) {
		if(player.getColor() == UgolkiConstants.CellColorBlueIndex){
			for(int j = 0; j < m_winIndexesForBlueSide.length; j++){
				PlayableTableCell currentCell = null;
				try {
					currentCell = getCellByIndex(m_winIndexesForBlueSide[j]);
				} catch (IndexOutOfBondsException e) {
					e.printStackTrace();
				}
				if (currentCell.getCellColor().getColor() != UgolkiConstants.CellColorBlueIndex) {
					return false;
				}
			}
		}
		else{
			for(int j = 0; j < m_winIndexesForRedSide.length; j++){
				PlayableTableCell currentCell = null;
				try {
					currentCell = getCellByIndex(m_winIndexesForRedSide[j]);
				} catch (IndexOutOfBondsException e) {
					e.printStackTrace();
				}
				if (currentCell.getCellColor().getColor() != UgolkiConstants.CellColorRedIndex) {
					return false;
				}
			}
		}
		
		return true;
	
	}
	public boolean isFasterWinConditionReachedForSide(BasePlayerSide player){
		if(player.getColor() == UgolkiConstants.CellColorBlueIndex){
			double currentDist = computeL1DistanceFromWin4Side((PlayerSide)player,this);
			return currentDist == m_winAverageL1Distance4Blue;
		}
		else{
			double currentDist = computeL1DistanceFromWin4Side((PlayerSide)player,this);
			return currentDist == m_winAverageL1Distance4Red;
		}
	}

	/**
	 * @return Returns the winIndexesForBlueSide.
	 */
	protected CellIndex[] getWinIndexesForBlueSide() {
		return m_winIndexesForBlueSide;
	}

	/**
	 * @param winIndexesForBlueSide The winIndexesForBlueSide to set.
	 */
	protected void setWinIndexesForBlueSide(CellIndex[] winIndexesForBlueSide) {
		this.m_winIndexesForBlueSide = winIndexesForBlueSide;
	}

	/**
	 * @return Returns the winIndexesForRedSide.
	 */
	protected CellIndex[] getWinIndexesForRedSide() {
		return m_winIndexesForRedSide;
	}

	/**
	 * @param winIndexesForRedSide The winIndexesForRedSide to set.
	 */
	protected void setWinIndexesForRedSide(CellIndex[] winIndexesForRedSide) {
		this.m_winIndexesForRedSide = winIndexesForRedSide;
	}
	
	public void initWinStates(){
		if(UgolkiConstants.PLAYERSSIZE == 2){
			for (int i = getCurrentStateContainer().getXSize()-3; i < getCurrentStateContainer().getXSize(); i++) {
				for (int j = 0; j < 3; j++) {
					getCurrentStateContainer().getBoard()[i][j].setCellColor(new CellColor(UgolkiConstants.CellColorRedIndex));
				}				
			}
			for (int i = 0; i < 3; i++) {
				for (int j = getCurrentStateContainer().getYSize()-3; j < getCurrentStateContainer().getYSize(); j++) {
					getCurrentStateContainer().getBoard()[i][j].setCellColor(new CellColor(UgolkiConstants.CellColorBlueIndex));
				}				
			}
		}
		m_currentAllIndexesForBlueMap.clear();
		for (int i = 0; i < m_winIndexesForBlueSide.length; i++) {
			CellIndex winIndex = m_winIndexesForBlueSide[i];
			m_currentAllIndexesForBlueMap.put(m_winIndexesForBlueSide[i].toString(),m_winIndexesForBlueSide[i]);
		}
		m_currentAllIndexesForRedMap.clear();
		for (int i = 0; i < m_winIndexesForRedSide.length; i++) {
			CellIndex winIndex = m_winIndexesForRedSide[i];
			m_currentAllIndexesForRedMap.put(m_winIndexesForRedSide[i].toString(),m_winIndexesForRedSide[i]);
		}
	}

	public void doMove(TableMove tableMove) throws InvalidMoveException, IndexOutOfBondsException{
		m_lastMove = tableMove;
		m_historyMoves.add(tableMove);
		if(getCellByIndex(tableMove.getFrom()).getCellColor().getColor() == UgolkiConstants.CellColorBlueIndex){
			adjustTableOnMoveForSide(tableMove,UgolkiConstants.PlayerSideBlue);
		}
		else{
			adjustTableOnMoveForSide(tableMove,UgolkiConstants.PlayerSideRed);
		}
		forceMakeMove(tableMove);// should be last
	}
	
	protected void adjustTableOnMoveForSide(TableMove tableMove,PlayerSide playerSide )throws InvalidMoveException, IndexOutOfBondsException{
	
		int color = getCellByIndex(tableMove.getFrom()).getCellColor().getColor();
		if(color == UgolkiConstants.CellColorBlueIndex){
			adjustAverageIndex4Side(UgolkiConstants.PlayerSideBlue,tableMove );
			// maintain the map with indexes for side
			CellIndex from = new CellIndex(tableMove.getFrom().getXCoord(),tableMove.getFrom().getYCoord());
			CellIndex to = new CellIndex(tableMove.getTo().getXCoord(),tableMove.getTo().getYCoord());
			m_currentAllIndexesForBlueMap.remove(from.toString());
			m_currentAllIndexesForBlueMap.put(to.toString(),to);
		}
		else {
			adjustAverageIndex4Side(UgolkiConstants.PlayerSideRed,tableMove );
//			 maintain the map with indexes for side
			CellIndex from = new CellIndex(tableMove.getFrom().getXCoord(),tableMove.getFrom().getYCoord());
			CellIndex to = new CellIndex(tableMove.getTo().getXCoord(),tableMove.getTo().getYCoord());
			m_currentAllIndexesForRedMap.remove(from.toString());
			m_currentAllIndexesForRedMap.put(to.toString(),to);
		}
		adjustL1DistanceFromWin4Side(tableMove );
		
	}
	
	public void undoLastMove() throws InvalidMoveException, IndexOutOfBondsException{
		m_lastMove = (TableMove)m_historyMoves.remove(m_historyMoves.size()-1);
		TableMove backMove = new TableMove(m_lastMove.getTo(),m_lastMove.getFrom());
		if(getCellByIndex(backMove.getFrom()).getCellColor().getColor() == UgolkiConstants.CellColorBlueIndex){
			adjustTableOnMoveForSide(backMove,UgolkiConstants.PlayerSideBlue);
		}
		else{
			adjustTableOnMoveForSide(backMove,UgolkiConstants.PlayerSideRed);
		}
		forceMakeMove(backMove);// should be last
	}
	public static  double computeL1DistanceFromWin4Side(PlayerSide playerSide, BaseTableState baseTableState ){
		CellIndex centerIndex;
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex ){
			centerIndex = baseTableState.m_centerIndexForWin4Blue;
		}
		else {
			centerIndex = baseTableState.m_centerIndexForWin4Red;
		}
		double averageXDistance = 0;
		double averageYDistance = 0;
		
		Map allSideIndexesMap = baseTableState.getFastAllCellIndexesForPlayerSide(playerSide);
		double averageX = 0;
		double averageY = 0;
		Set keyset = allSideIndexesMap.keySet();
		for (Iterator iter = keyset.iterator(); iter.hasNext();) {
			CellIndex element = (CellIndex) allSideIndexesMap.get(iter.next());
			averageXDistance+=Math.abs(element.getXCoord()-centerIndex.getXCoord());
			averageYDistance+=Math.abs(element.getYCoord() - centerIndex.getYCoord());
		}
		
		CellIndex[] allSideIndexes = baseTableState.getAllCellIndexesForPlayerSide(playerSide);
		for (int i = 0; i < allSideIndexes.length; i++) {
			
		}
		double result = (averageXDistance  + averageYDistance)/9; //TODO got to remove this MAGIC number(9)
		return result;
	}
	
	
	
	public void adjustL1DistanceFromWin4Side(TableMove tableMove){
		int color = 0;
		try {
			color = getCellByIndex(tableMove.getFrom()).getCellColor().getColor();
		} catch (IndexOutOfBondsException e) {
			e.printStackTrace();
		}
		if(color == UgolkiConstants.CellColorBlueIndex){
			double xDistFrom = Math.abs(tableMove.getFrom().getXCoord()-m_centerIndexForWin4Blue.getXCoord());
			double yDistFrom = Math.abs(tableMove.getFrom().getYCoord()-m_centerIndexForWin4Blue.getYCoord());
			double xDistTo = Math.abs(tableMove.getTo().getXCoord()-m_centerIndexForWin4Blue.getXCoord());
			double yDistTo = Math.abs(tableMove.getTo().getYCoord()-m_centerIndexForWin4Blue.getYCoord());
			m_currentAverageL1Distance4Blue = (m_currentAverageL1Distance4Blue*9 - (xDistFrom + yDistFrom) + (xDistTo + yDistTo))/9;
		}
		else{
			double xDistFrom = Math.abs(tableMove.getFrom().getXCoord()-m_centerIndexForWin4Red.getXCoord());
			double yDistFrom = Math.abs(tableMove.getFrom().getYCoord()-m_centerIndexForWin4Red.getYCoord());
			double xDistTo = Math.abs(tableMove.getTo().getXCoord()-m_centerIndexForWin4Red.getXCoord());
			double yDistTo = Math.abs(tableMove.getTo().getYCoord()-m_centerIndexForWin4Red.getYCoord());
			m_currentAverageL1Distance4Red = (m_currentAverageL1Distance4Red*9 - (xDistFrom + yDistFrom) + (xDistTo + yDistTo))/9;
		}
	}
	public static  double computeL2DistanceFromWin4Side(PlayerSide playerSide, BaseTableState baseTableState ){
		CellIndex centerIndex;
		double result = 0;
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex ){
			centerIndex = baseTableState.m_centerIndexForWin4Blue;
		}
		else {
			centerIndex = baseTableState.m_centerIndexForWin4Red;
		}
		double averageXDistance = 0;
		double averageYDistance = 0;
		
		Map allSideIndexesMap = baseTableState.getFastAllCellIndexesForPlayerSide(playerSide);
		double averageX = 0;
		double averageY = 0;
		Set keyset = allSideIndexesMap.keySet();
		for (Iterator iter = keyset.iterator(); iter.hasNext();) {
			CellIndex element = (CellIndex) allSideIndexesMap.get(iter.next());
			averageXDistance+=Math.pow(element.getXCoord()-centerIndex.getXCoord(),2);
			averageYDistance+=Math.pow(element.getYCoord() - centerIndex.getYCoord(),2);
			result += Math.sqrt(averageXDistance + averageYDistance) ;
		}
		
		return result;
	}
	
	public void adjustL2DistanceFromWin4Side(TableMove tableMove){
		int color = 0;
		try {
			color = getCellByIndex(tableMove.getFrom()).getCellColor().getColor();
		} catch (IndexOutOfBondsException e) {
			e.printStackTrace();
		}
		if(color == UgolkiConstants.CellColorBlueIndex){
			int xp = tableMove.getTo().getXCoord();
			int yp = tableMove.getTo().getYCoord();
			int xz = m_centerIndexForWin4Blue.getXCoord();
			int yz = m_centerIndexForWin4Blue.getYCoord();
			int xm = tableMove.getFrom().getXCoord();
			int ym = tableMove.getFrom().getYCoord();
			double deltaPlus = Math.sqrt(xp*xp -2*xp*xz +xz*xz +yp*yp -2*yp*yz +yz*yz);
			double deltaMinus = Math.sqrt(xm*xm -2*xm*xz +xz*xz +ym*ym -2*ym*yz +yz*yz);
			
			m_currentAverageL2Distance4Blue = m_currentAverageL2Distance4Blue - deltaMinus + deltaPlus;
		}
		else{
			int xp = tableMove.getTo().getXCoord();
			int yp = tableMove.getTo().getYCoord();
			int xz = m_centerIndexForWin4Red.getXCoord();
			int yz = m_centerIndexForWin4Red.getYCoord();
			int xm = tableMove.getFrom().getXCoord();
			int ym = tableMove.getFrom().getYCoord();
			double deltaPlus = Math.sqrt(xp*xp -2*xp*xz +xz*xz +yp*yp -2*yp*yz +yz*yz);
			double deltaMinus = Math.sqrt(xm*xm -2*xm*xz +xz*xz +ym*ym -2*ym*yz +yz*yz);
			
			m_currentAverageL2Distance4Red = m_currentAverageL2Distance4Red - deltaMinus + deltaPlus;
		}
	}
	
	public static  double[] computeAverageIndex4Side(PlayerSide playerSide, BaseTableState baseTableState ){
		Map allSideIndexesMap = baseTableState.getFastAllCellIndexesForPlayerSide(playerSide);
		double averageX = 0;
		double averageY = 0;
		Set keyset = allSideIndexesMap.keySet();
		for (Iterator iter = keyset.iterator(); iter.hasNext();) {
			CellIndex element = (CellIndex) allSideIndexesMap.get(iter.next());
			averageX+=element.getXCoord();
			averageY+=element.getYCoord();
		}
		double[] result = {averageX/9, averageY/9};
		return result;
	}
	
	/**
	 * don't compute the average X,Y indexes from scratch, compute just the delta
	 * 
	 */
	public  void  adjustAverageIndex4Side(PlayerSide playerSide, TableMove tableMove ){
		double averageX = 0;
		double averageY = 0;
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			averageX = m_currentFloatingCenterIndex4Blue.getXCoord();
			averageY = m_currentFloatingCenterIndex4Blue.getYCoord();
			averageX = (averageX*9 - tableMove.getFrom().getXCoord() + tableMove.getTo().getXCoord())/9;
			averageY = (averageY*9 - tableMove.getFrom().getYCoord() + tableMove.getTo().getYCoord())/9;
			m_currentFloatingCenterIndex4Blue.setXCoord(averageX);
			m_currentFloatingCenterIndex4Blue.setYCoord(averageY);
		}
		else {
			averageX = m_currentFloatingCenterIndex4Red.getXCoord()*9;
			averageY = m_currentFloatingCenterIndex4Red.getYCoord()*9;
			averageX = (averageX - tableMove.getFrom().getXCoord() + tableMove.getTo().getXCoord());
			averageY = (averageY - tableMove.getFrom().getYCoord() + tableMove.getTo().getYCoord());
			m_currentFloatingCenterIndex4Red.setXCoord(averageX/9);
			m_currentFloatingCenterIndex4Red.setYCoord(averageY/9);
		}
		
	}
	/**
	 * used in constructor to init the m_floatingCenterIndex4Blue and m_floatingCenterIndex4Red
	 * @param playerSide
	 */
	public void computeFloatingWinCenterIndex4Side(PlayerSide playerSide){
		double averageX = 0;
		double averageY = 0;
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			
			for (int i = 0; i < 3; i++) {
				for (int j = getCurrentStateContainer().getYSize()-3; j < getCurrentStateContainer().getYSize(); j++) {
					if(getCurrentStateContainer().getBoard()[i][j].getCellColor().getColor() != UgolkiConstants.CellColorBlueIndex){
						averageX+=getCurrentStateContainer().getBoard()[i][j].getCellIndex().getXCoord();
						averageY+=getCurrentStateContainer().getBoard()[i][j].getCellIndex().getYCoord();
					}
				}				
			}
			if(m_floatingWinCenterIndex4Blue == null) m_floatingWinCenterIndex4Blue= new RealCellIndex();
			m_floatingWinCenterIndex4Blue.setXCoord(averageX);
			m_floatingWinCenterIndex4Blue.setYCoord(averageY);
		}
		else {
			for (int i = getCurrentStateContainer().getXSize()-3; i < getCurrentStateContainer().getXSize(); i++) {
				for (int j = 0; j < 3; j++) {
					if(getCurrentStateContainer().getBoard()[i][j].getCellColor().getColor() != UgolkiConstants.CellColorRedIndex){
						averageX+=getCurrentStateContainer().getBoard()[i][j].getCellIndex().getXCoord();
						averageY+=getCurrentStateContainer().getBoard()[i][j].getCellIndex().getYCoord();
					}
				}				
			}
			if(m_floatingWinCenterIndex4Red == null) m_floatingWinCenterIndex4Red = new RealCellIndex();
			m_floatingWinCenterIndex4Red.setXCoord(averageX);
			m_floatingWinCenterIndex4Red.setYCoord(averageY);
		}
	}
	
	/**
	 * Compute average distance form current average center, we don't wan't those outliers !!!
	 * @param side
	 * @param state
	 * @return
	 */
	/*
	 * starting distance for blue is 139.52134901888445, win 19.318509705208598
	 * starting distance for red is 139.02110858350886, win 23.75760041081968
	 * notice the difference ! there should be no difference!!! ????  ...
	 */
	public static double computeDistanceFromAverageIndex4Side(PlayerSide side, BaseTableState state){
		RealCellIndex avIndex = null;
		LinkedList sideIndexesList = new LinkedList();
		boolean isBlue = false;
		if(side.getColor()==UgolkiConstants.CellColorBlueIndex){
			isBlue = true;
			avIndex = state.getCurrentFloatingCenterIndex4Blue();
			sideIndexesList.addAll(state.getCurrentAllIndexesForBlueMap().values());
		}
		else {
			avIndex = state.getCurrentFloatingCenterIndex4Red();
			sideIndexesList.addAll(state.getCurrentAllIndexesForRedMap().values());
		}
		double result = 0 ;
		double averageXDistance = 0;
		double averageYDistance = 0;
		
		
		for (Iterator iter = sideIndexesList.iterator(); iter.hasNext();) {
			CellIndex element = (CellIndex) iter.next();
			averageXDistance+=Math.pow(element.getXCoord()-avIndex.getXCoord(),2);
			averageYDistance+=Math.pow(element.getYCoord() - avIndex.getYCoord(),2);
			result += Math.sqrt(averageXDistance + averageYDistance) ;
		}
		
		return result;
		
	}
	
	public double getNormalizedL1DistanceForSide(PlayerSide playerSide){
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			return ((m_winAverageL1Distance4Blue / m_currentAverageL1Distance4Blue  - 1/6 ) - (m_winAverageL1Distance4Red / m_currentAverageL1Distance4Red -1/6))*(12/5);//12= starting distance
		}
		else{
			return ((m_winAverageL1Distance4Red / m_currentAverageL1Distance4Red -1/6) -  (m_winAverageL1Distance4Blue / m_currentAverageL1Distance4Blue  - 1/6 ))*(12/5);
		}
	}
	public double getNormalizedIndexDistanceForSide(PlayerSide playerSide){
		double blueDist = (Math.abs(m_floatingWinCenterIndex4Blue.getXCoord()- m_currentFloatingCenterIndex4Blue.getXCoord()) + Math.abs(m_floatingWinCenterIndex4Blue.getYCoord()- m_currentFloatingCenterIndex4Blue.getYCoord()))/10;
		double redDist = (Math.abs(m_floatingWinCenterIndex4Red.getXCoord()- m_currentFloatingCenterIndex4Red.getXCoord()) + Math.abs(m_floatingWinCenterIndex4Red.getYCoord()- m_currentFloatingCenterIndex4Red.getYCoord()))/10;
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			return  blueDist;
		}
		else{
			return  redDist;
		
		}
	}
	public double getNormalizedL2DistanceForSide(PlayerSide playerSide){
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			return (166.09443099123834 - m_currentAverageL2Distance4Blue)/(166.09443099123834 - 35.32137679537579) - (166.09443099123834 - m_currentAverageL2Distance4Red)/(166.09443099123834 - 35.32137679537579);
		}
		else{
			return (166.09443099123834 - m_currentAverageL2Distance4Red)/(166.09443099123834 - 35.32137679537579) - (166.09443099123834 - m_currentAverageL2Distance4Blue)/(166.09443099123834 - 35.32137679537579);
		}
	}
//	public double getNormalizedIndexDistanceForSide(PlayerSide playerSide){
//		double blueDist = (Math.abs(m_floatingWinCenterIndex4Blue.getXCoord()- m_currentFloatingCenterIndex4Blue.getXCoord()) + Math.abs(m_floatingWinCenterIndex4Blue.getYCoord()- m_currentFloatingCenterIndex4Blue.getYCoord()))/10;
//		double redDist = (Math.abs(m_floatingWinCenterIndex4Red.getXCoord()- m_currentFloatingCenterIndex4Red.getXCoord()) + Math.abs(m_floatingWinCenterIndex4Red.getYCoord()- m_currentFloatingCenterIndex4Red.getYCoord()))/10;
//		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
//			return  blueDist;
//		}
//		else{
//			return  redDist;
//		
//		}
//	}
	
	/*
	 * starting distance for blue is 139.52134901888445, win 19.318509705208598
	 * starting distance for red is 139.02110858350886, win 23.75760041081968
	 * notice the difference ! there should be no difference!!! ????  ...
	 */
	public double getNormalizedDistanceFromAverageIndex4Side(PlayerSide playerSide){
		double distanceRed = 0;
		double distanceBlue = 0;
		if(playerSide.getColor() == UgolkiConstants.CellColorBlueIndex){
			distanceBlue = computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideBlue, this);
			distanceRed = computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideRed, this);
			return (139.52134901888445 - distanceBlue)/(139.52134901888445 - 19.318509705208598) - (139.02110858350886 - distanceRed)/(139.02110858350886 - 23.75760041081968);
		}
		else{
			distanceBlue = computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideBlue, this);
			distanceRed = computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideRed, this);
			return (139.52134901888445 - distanceRed)/(139.52134901888445 - 23.75760041081968) - (139.52134901888445 - distanceBlue)/(139.52134901888445-  19.318509705208598);
		}
	}
	
	
	public double getNormalizedHitWinPositionDistanceForSide(BasePlayerSide player) {
		int numberOfBlueHits =0;
		int numberOfRedHits =0;
		double result = 0;
		for(int j = 0; j < m_winIndexesForRedSide.length; j++){
			PlayableTableCell currentCell = null;
			try {
				currentCell = getCellByIndex(m_winIndexesForRedSide[j]);
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			}
			if (currentCell.getCellColor().getColor() == UgolkiConstants.CellColorRedIndex) {
				numberOfRedHits++;
			}
		}
		for(int j = 0; j < m_winIndexesForBlueSide.length; j++){
			PlayableTableCell currentCell = null;
			try {
				currentCell = getCellByIndex(m_winIndexesForBlueSide[j]);
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			}
			if (currentCell.getCellColor().getColor() == UgolkiConstants.CellColorBlueIndex) {
				numberOfBlueHits++;
			}
		}
		if(player.getColor() == UgolkiConstants.CellColorBlueIndex){
			
			result = numberOfBlueHits  ;
		}
		else{
			result = numberOfRedHits  ;
		}
		return result/9;
	
	}
	public double getNormalizedLosePositionDistanceForSide(BasePlayerSide player) {
		int numberOfBlueHits =0;
		int numberOfRedHits =0;
		double result = 0;
		for(int j = 0; j < m_startIndexes4Red.length; j++){
			PlayableTableCell currentCell = null;
			try {
				currentCell = getCellByIndex(m_startIndexes4Red[j]);
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			}
			if (currentCell.getCellColor().getColor() == UgolkiConstants.CellColorRedIndex) {
				numberOfRedHits++;
			}
		}
		for(int j = 0; j < m_startIndexes4Blue.length; j++){
			PlayableTableCell currentCell = null;
			try {
				currentCell = getCellByIndex(m_startIndexes4Blue[j]);
			} catch (IndexOutOfBondsException e) {
				e.printStackTrace();
			}
			if (currentCell.getCellColor().getColor() == UgolkiConstants.CellColorBlueIndex) {
				numberOfBlueHits++;
			}
		}
		if(player.getColor() == UgolkiConstants.CellColorBlueIndex){
			
			result = numberOfBlueHits ;
		}
		else{
			result = numberOfRedHits  ;
		}
		
		return -result/9;
	
	}
	
	protected void clearSideColors(){
		for (int i = 0; i < UgolkiConstants.maxSameColorIndexSize-1; i++) {
			for (int j = 0; j < UgolkiConstants.maxSameColorIndexSize-1; j++) {
				getCurrentStateContainer().getBoard()[i][j].setCellColor(new CellColor(UgolkiConstants.CellColorUnoccupied));
			}				
		}
		m_currentAllIndexesForRedMap.clear();
		m_currentAllIndexesForBlueMap.clear();
	}
	
	
	
	private class RealCellIndex {
		
		private double xCoord;
		
		
		private double yCoord;
		
		public RealCellIndex(){
			xCoord=0;
			yCoord=0;
		}
		public RealCellIndex(double x, double y){
			xCoord=x;
			yCoord=y;
		}
		/**
		 * @return Returns the xCoord.
		 */
		public double getXCoord() {
			return xCoord;
		}
		/**
		 * @param coord The xCoord to set.
		 */
		public void setXCoord(double coord) {
			xCoord = coord;
		}
		/**
		 * @return Returns the yCoord.
		 */
		public double getYCoord() {
			return yCoord;
		}
		/**
		 * @param coord The yCoord to set.
		 */
		public void setYCoord(double coord) {
			yCoord = coord;
		}
		public String toString() {
			return "("+getXCoord()+","+ getYCoord()+")";
		}
	}

	/**
	 * @return Returns the m_centerIndexForWin4Blue.
	 */
	public CellIndex getCenterIndexForWin4Blue() {
		return m_centerIndexForWin4Blue;
	}

	/**
	 * @return Returns the m_centerIndexForWin4Red.
	 */
	public CellIndex getCenterIndexForWin4Red() {
		return m_centerIndexForWin4Red;
	}

	/**
	 * @return Returns the m_currentAllIndexesForBlueMap.
	 */
	public HashMap getCurrentAllIndexesForBlueMap() {
		return m_currentAllIndexesForBlueMap;
	}

	/**
	 * @return Returns the m_currentAllIndexesForRedMap.
	 */
	public HashMap getCurrentAllIndexesForRedMap() {
		return m_currentAllIndexesForRedMap;
	}

	/**
	 * @return Returns the m_currentAverageL1Distance4Blue.
	 */
	public double getCurrentAverageL1Distance4Blue() {
		return m_currentAverageL1Distance4Blue;
	}

	/**
	 * @return Returns the m_currentAverageL1Distance4Red.
	 */
	public double getCurrentAverageL1Distance4Red() {
		return m_currentAverageL1Distance4Red;
	}

	/**
	 * @return Returns the m_currentAverageL2Distance4Blue.
	 */
	public double getCurrentAverageL2Distance4Blue() {
		return m_currentAverageL2Distance4Blue;
	}

	/**
	 * @return Returns the m_currentAverageL2Distance4Red.
	 */
	public double getCurrentAverageL2Distance4Red() {
		return m_currentAverageL2Distance4Red;
	}

	/**
	 * @return Returns the m_currentFloatingCenterIndex4Blue.
	 */
	public RealCellIndex getCurrentFloatingCenterIndex4Blue() {
		return m_currentFloatingCenterIndex4Blue;
	}

	/**
	 * @return Returns the m_currentFloatingCenterIndex4Red.
	 */
	public RealCellIndex getCurrentFloatingCenterIndex4Red() {
		return m_currentFloatingCenterIndex4Red;
	}



	/**
	 * @return Returns the m_floatingWinCenterIndex4Blue.
	 */
	public RealCellIndex getFloatingWinCenterIndex4Blue() {
		return m_floatingWinCenterIndex4Blue;
	}

	/**
	 * @return Returns the m_floatingWinCenterIndex4Red.
	 */
	public RealCellIndex getFloatingWinCenterIndex4Red() {
		return m_floatingWinCenterIndex4Red;
	}

	/**
	 * @return Returns the m_historyMoves.
	 */
	public List getHistoryMoves() {
		return m_historyMoves;
	}

	/**
	 * @return Returns the m_lastMove.
	 */
	public TableMove getLastMove() {
		return m_lastMove;
	}

	/**
	 * @return Returns the m_startIndexes4Blue.
	 */
	public CellIndex[] getStartIndexes4Blue() {
		return m_startIndexes4Blue;
	}

	/**
	 * @return Returns the m_startIndexes4Red.
	 */
	public CellIndex[] getStartIndexes4Red() {
		return m_startIndexes4Red;
	}

	/**
	 * @return Returns the m_winAverageL1Distance4Blue.
	 */
	public double getWinAverageL1Distance4Blue() {
		return m_winAverageL1Distance4Blue;
	}

	/**
	 * @return Returns the m_winAverageL1Distance4Red.
	 */
	public double getWinAverageL1Distance4Red() {
		return m_winAverageL1Distance4Red;
	}

}
