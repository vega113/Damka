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
package mystuff.checkers.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import javax.swing.text.html.MinimalHTMLWriter;
import mystuff.checkers.base.BaseAutomatePlayer;
import mystuff.checkers.base.BaseTableState;
import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.exceptions.IndexOutOfBondsException;
import mystuff.checkers.exceptions.InvalidMoveException;
import mystuff.checkers.interfaces.IPlayer;
import mystuff.checkers.player.AlphaBetaPlayer;
import mystuff.checkers.player.MinimaxPlayer;
import mystuff.checkers.player.MinimaxPlayerV2;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.table.CellColor;
import mystuff.checkers.table.CellIndex;
import mystuff.checkers.table.TableMove;
import mystuff.checkers.table.TableState;
import junit.framework.TestCase;

public class TestTable extends TestCase {
	protected TableState m_tableState = null;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestTable.class);
	}

	public TestTable(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		m_tableState = new TableState();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'mystuff.checkers.base.BaseTableState.isMoveValid(TableMove)'
	 */
	public final void testIsMoveValid() {
		System.out.println("Entering testIsMoveValid");
		CellIndex fromIndex = new CellIndex(5,0);
		CellIndex toIndex = new CellIndex(4,0);
		try {
			System.out.println(m_tableState.getCellByIndex(fromIndex).toString());
		} catch (IndexOutOfBondsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println(m_tableState.getCellByIndex(toIndex).toString());
		} catch (IndexOutOfBondsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TableMove move = new TableMove(fromIndex,toIndex);
		assertTrue( m_tableState.isMoveValid(move));
		System.out.println("Exiting testIsMoveValid");
	}

	/*
	 * Test method for 'mystuff.checkers.base.BaseTableState.getAllValidMovesForPlayerSide(BasePlayerSide)'
	 */
	public final void testGetAllValidMovesForPlayerSide() {
		assertTrue(true);

	}
	/*
	 * Test method for a jump move
	 */
	public final void testJumpMove(){
		System.out.println("Entering testJumpMove");
		System.out.println("Entering testForceMakeMove");
		System.out.println("BEFORE MOVE");
		System.out.println(m_tableState.toString());
		CellIndex fromIndex = new CellIndex(5,0);
		CellIndex toIndex = new CellIndex(4,0);
		TableMove move = new TableMove(fromIndex,toIndex);
		System.out.println(move.toString());
		try {
			m_tableState.doMove(move);
		} catch (InvalidMoveException e) {
			assertTrue(false);
		} catch (IndexOutOfBondsException e) {
			assertTrue(false);
		}
		System.out.println("AFTER MOVE " + move.toString());
		System.out.println(m_tableState.toString());
		try {
			assertTrue(m_tableState.isCellValidAndOccupied(toIndex));
		} catch (IndexOutOfBondsException e) {
			assertTrue(false);
		}
		fromIndex = new CellIndex(7,0);
		toIndex = new CellIndex(3,0);
		move = new TableMove(fromIndex,toIndex);
		System.out.println("Testting validity of next move: " + move.toString());
		assertTrue(m_tableState.isMoveValid(move));
		try {
			m_tableState.doMove(move);
		} catch (InvalidMoveException e) {
			assertTrue(false);
		} catch (IndexOutOfBondsException e) {
		}
		try {
			assertTrue(m_tableState.isCellValidAndOccupied(toIndex));
		} catch (IndexOutOfBondsException e) {
			assertTrue(false);
		}
		System.out.println("AFTER MOVE " + move.toString());
		System.out.println(m_tableState.toString());
		System.out.println("Exiting Jump Move");
	}
	/*
	 * Test method for 'mystuff.checkers.base.BaseTableState.forceMakeMove(TableMove tableMove)'
	 */
	public final void testForceMakeMove() {
		System.out.println("Entering testForceMakeMove");
		System.out.println(m_tableState.toString());
		System.out.println("BEFORE MOVE");
		CellIndex fromIndex = new CellIndex(5,0);
		CellIndex toIndex = new CellIndex(4,0);
		try {
			System.out.println(m_tableState.getCellByIndex(fromIndex).toString());
		} catch (IndexOutOfBondsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println(m_tableState.getCellByIndex(toIndex).toString());
		} catch (IndexOutOfBondsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TableMove move = new TableMove(fromIndex,toIndex);
		try {
			m_tableState.doMove(move);
		} catch (InvalidMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		} catch (IndexOutOfBondsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("AFTER MOVE");
		System.out.println(m_tableState.toString());
		try {
			assertTrue(m_tableState.isCellValidAndOccupied(toIndex));
		} catch (IndexOutOfBondsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(m_tableState.toString());
		System.out.println("Exiting testForceMakeMove");
	}
	/*
	 * Test method for 'mystuff.checkers.base.BaseTableState.isValidIndex(CellIndex cellIndex)'
	 */
	public final void testIsValidIndex() {
		CellIndex fromIndex = new CellIndex(0,5);
		assertTrue(m_tableState.isValidIndex(fromIndex));
	}
	
	public final void testIsWinConditionReachedForSide(){
		assertFalse("No Win Condition Reached",m_tableState.isWinConditionReachedForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorRedIndex))));
		System.out.println("computeL1DistanceFromWin4Side Blue starting position= "+BaseTableState.computeL1DistanceFromWin4Side(UgolkiConstants.PlayerSideBlue,m_tableState));
		System.out.println("computeL1DistanceFromWin4Side Red starting position= "+BaseTableState.computeL1DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,m_tableState));
		System.out.println("computeL2DistanceFromWin4Side Blue starting position= "+BaseTableState.computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideBlue,m_tableState));
		System.out.println("computeL2DistanceFromWin4Side Red starting position= "+BaseTableState.computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,m_tableState));
		System.out.println("computeDistanceFromAverageIndex4Side Blue starting position= "+BaseTableState.computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideBlue,m_tableState));
		System.out.println("computeDistanceFromAverageIndex4Side Red starting position= "+BaseTableState.computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideRed,m_tableState));
		double l2b4win = BaseTableState.computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,m_tableState);
		double[] avgIndexStartung = BaseTableState.computeAverageIndex4Side(UgolkiConstants.PlayerSideRed,m_tableState); 
		System.out.println("RED - avg index b4 start pos= (" +avgIndexStartung[0] +","+avgIndexStartung[1]+")" );
		avgIndexStartung = BaseTableState.computeAverageIndex4Side(UgolkiConstants.PlayerSideBlue,m_tableState);
		System.out.println("Blue - avg index b4 start pos= (" +avgIndexStartung[0] +","+avgIndexStartung[1]+")" );
		assertFalse(m_tableState.isWinConditionReachedForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorRedIndex))));
		assertFalse(m_tableState.isWinConditionReachedForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorBlueIndex))));
		
		
		m_tableState.initWinStates();
		double l2afterWin = BaseTableState.computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,m_tableState);
		System.out.println("computeL1DistanceFromWin4Side Blue in Win position = "+BaseTableState.computeL1DistanceFromWin4Side(UgolkiConstants.PlayerSideBlue,m_tableState));
		System.out.println("computeL1DistanceFromWin4Side Red in Win position= "+BaseTableState.computeL1DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,m_tableState));
		System.out.println("computeL2DistanceFromWin4Side Blue in Win position = "+BaseTableState.computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideBlue,m_tableState));
		System.out.println("computeL2DistanceFromWin4Side Red in Win position= "+BaseTableState.computeL2DistanceFromWin4Side(UgolkiConstants.PlayerSideRed,m_tableState));
		System.out.println("computeDistanceFromAverageIndex4Side Blue in Win position = "+BaseTableState.computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideBlue,m_tableState));
		System.out.println("computeDistanceFromAverageIndex4Side Red in Win position= "+BaseTableState.computeDistanceFromAverageIndex4Side(UgolkiConstants.PlayerSideRed,m_tableState));
		
		assertFalse(l2afterWin == l2b4win);
		
		avgIndexStartung = BaseTableState.computeAverageIndex4Side(UgolkiConstants.PlayerSideRed,m_tableState);
		System.out.println("RED - avg index win pos= (" +avgIndexStartung[0] +","+avgIndexStartung[1]+")" );
		avgIndexStartung = BaseTableState.computeAverageIndex4Side(UgolkiConstants.PlayerSideBlue,m_tableState);
		System.out.println("Blue - avg index win pos= (" +avgIndexStartung[0] +","+avgIndexStartung[1]+")" );
		assertTrue(m_tableState.isWinConditionReachedForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorRedIndex))));
		assertTrue(m_tableState.isWinConditionReachedForSide(new PlayerSide(new CellColor(UgolkiConstants.CellColorBlueIndex))));
	}

	public final void testIstoXmlWorking() {
		testForceMakeMove();
		m_tableState.toXmlString();
		assertTrue(false);
	}
	
	public final void testIsRestoreFromXmlWorking(){
		System.out.println("---------======== In testIsRestoreFromXmlWorking ----------=========");
		
		CellIndex fromIndex = new CellIndex(5,0);
		CellIndex toIndex = new CellIndex(4,0);
		try {
			System.out.println(m_tableState.getCellByIndex(fromIndex).toString());
		} catch (IndexOutOfBondsException e1) {
			assertTrue(false);
			e1.printStackTrace();
		}
		try {
			System.out.println(m_tableState.getCellByIndex(toIndex).toString());
		} catch (IndexOutOfBondsException e1) {
			assertTrue(false);
			e1.printStackTrace();
		}
		TableMove move = new TableMove(fromIndex,toIndex);
		try {
			m_tableState.doMove(move);
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		} catch (IndexOutOfBondsException e) {
			assertTrue(false);
			e.printStackTrace();
		}
		System.out.println("---------======== Table B4 restoring from xml ----------=========");
		System.out.println(m_tableState.toString());
		FileReader reader = null;
		String currentPathStr = null;
		try {
			File xmlPath = new File ("output.xml");
			try {
				currentPathStr = xmlPath.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reader = new FileReader("output.xml");
		} catch (FileNotFoundException e1) {
			assertTrue(false);
			e1.printStackTrace();
		}
		try {
			reader.read();
		} catch (IOException e) {
			assertTrue(false);
			e.printStackTrace();
		}
		m_tableState.restoreFromXml(currentPathStr);
		System.out.println("---------======== Table AFTER restoring from xml ----------=========");
		System.out.println(m_tableState.toString());
		assertTrue(true);
	}
	
	public void testIsHeuristicFunctionSound() throws InvalidMoveException, IndexOutOfBondsException{
		System.out.println("---------======== Entering testIsHeuristicFunctionSound ----------=========");
		// evaluate couple of positions, with one of them obviously better
		// move the table into win positions, move Red one step back and make sure he choses the move that returns him into win position.
		m_tableState.initWinStates();
		double h1Red = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideRed);
		double h1Blue = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideBlue);
		System.out.println("Table in Win states"+"\n" + m_tableState.toString()+"\n" + "Heuristic Value  - Red: "+h1Red + " Blue: " + h1Blue);
		assertTrue(h1Blue == h1Red);
		System.out.println("########################################################"+"\n");
		CellIndex fromIndex = new CellIndex(2,"f");
		CellIndex toIndex = new CellIndex(3,"f");
		TableMove tableMove = new TableMove(fromIndex,toIndex);
		System.out.println("Player" + UgolkiConstants.BluePlayerName +"(" + UgolkiConstants.BLUEPLAYERSYMBOL+")" +" makes back move: " + tableMove.toString());
		m_tableState.doMove(tableMove);
		double h2Red = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideRed);
		double h2Blue = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideBlue);
		System.out.println(UgolkiConstants.BluePlayerName+" is not in Win state"+"\n" + m_tableState.toString()+"\n" + "Heuristic Value  - Red: "+h2Red + " Blue: " + h2Blue);
		assertTrue(h2Blue < h1Blue);
		assertTrue(h2Blue <= h2Red);
		System.out.println("########################################################"+"\n");
		
		
		// make one more backmove
		CellIndex fromIndex1 = new CellIndex(2,"h");
		CellIndex toIndex1 = new CellIndex(4,"f");
		TableMove tableMove1 = new TableMove(fromIndex1,toIndex1);
		System.out.println("Player" + UgolkiConstants.BluePlayerName +"(" + UgolkiConstants.BLUEPLAYERSYMBOL+")" +" makes back move: " + tableMove1.toString());
		m_tableState.doMove(tableMove1);
		double h3Red = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideRed);
		double h3Blue = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideBlue);
		System.out.println(UgolkiConstants.BluePlayerName+" is not in Win state"+"\n" + m_tableState.toString()+"\n" + "Heuristic Value  - Red: "+h3Red + " Blue: " + h3Blue);
		assertTrue(h3Blue < h2Blue);
		System.out.println("########################################################"+"\n");
		
				// now get all valid moves for UgolkiConstants.BluePlayerName and see what has the highes heuristic values, off course only one
		// returns UgolkiConstants.BluePlayerName to win position, we expect it.
		TableMove[] tableMoves = m_tableState.getAllValidMovesForPlayerSide(UgolkiConstants.PlayerSideBlue);
		System.out.println("Now looking at all successor position for state" +"\n"+ m_tableState.toString() );
		System.out.println("The Current heuristic Values For " + UgolkiConstants.BluePlayerName + " is : " + h3Blue );
		System.out.println("########################################################"+"\n");
		for (int i = 0; i < tableMoves.length; i++) {
			m_tableState.doMove(tableMoves[i]);
			tableMoves[i].setValue(getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideBlue));
			m_tableState.undoLastMove();
		}
		Arrays.sort(tableMoves, TableMove.DescValueMoveComparator);
		TableMove bestMove = new TableMove(new CellIndex(4,"f"),new CellIndex(2,"h"));
		assertTrue(tableMoves[0].equals(bestMove));
		for (int i = 0; i < tableMoves.length; i++) {
			System.out.println("Player" + UgolkiConstants.BluePlayerName +"(" + UgolkiConstants.BLUEPLAYERSYMBOL+")" +" makes  move: " + tableMoves[i].toString());
			m_tableState.doMove(tableMoves[i]);
			double hiRed = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideRed);
			double hiBlue = getHeuristicValues(m_tableState,UgolkiConstants.PlayerSideBlue);
			System.out.println(UgolkiConstants.BluePlayerName+"\n" + m_tableState.toString()+"\n" + "Heuristic Value  - Red: "+hiRed + " Blue: " + hiBlue);
			System.out.println("The move delta in Heuristic Value for " + UgolkiConstants.BluePlayerName + " is: " + (hiBlue-h3Blue));
			m_tableState.undoLastMove();
			System.out.println("Doing move back" + "\n" + m_tableState.toString());
			System.out.println("########################################################"+"\n");
		}
		
		System.out.println("---------======== Exiting testIsHeuristicFunctionSound ----------=========");
		
	}
	
	private double getHeuristicValues(TableState state, PlayerSide side){
		return m_tableState.getNormalizedL1DistanceForSide(side);
	}
	
	public void testIsSimpleXmlWorking(){
//		Serializer serializer = new Persister();
//		 make one more backmove
		CellIndex fromIndex1 = new CellIndex(2,"h");
		CellIndex toIndex1 = new CellIndex(4,"f");
		TableMove tableMove1 = new TableMove(fromIndex1,toIndex1);
		try {
			m_tableState.doMove(tableMove1);
		} catch (InvalidMoveException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IndexOutOfBondsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		try {
//			serializer.write(m_tableState, new File("table.xml"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void testPlayer() throws InvalidMoveException, IndexOutOfBondsException{

		System.out.println("---------======== Entering testPlayer ----------=========");
		// evaluate couple of positions, with one of them obviously better
		// move the table into win positions, move Red one step back and make sure he choses the move that returns him into win position.
		m_tableState.initWinStates();
		
		CellIndex fromIndex = new CellIndex(2,"f");
		CellIndex toIndex = new CellIndex(3,"f");
		TableMove tableMove = new TableMove(fromIndex,toIndex);
		m_tableState.doMove(tableMove);
		
		
		// make one more backmove
		CellIndex fromIndex1 = new CellIndex(2,"h");
		CellIndex toIndex1 = new CellIndex(4,"f");
		TableMove tableMove1 = new TableMove(fromIndex1,toIndex1);
		m_tableState.doMove(tableMove1);
		
		
		System.out.println("---------======== Table State before move ----------=========");
		System.out.println(m_tableState.toString());
		
		// now ask Blue player for next move, the best move is (4,f)->(2,h)
		BaseAutomatePlayer player = new MinimaxPlayerV2(UgolkiConstants.PlayerSideBlue,3);
		player.setCurrentTableState(m_tableState);
		player.execute();
		TableMove move = player.getNextMove();
		
		System.out.println("---------======== Player Made Move ----------=========");
		System.out.println(move.toString());
		
		System.out.println("---------======== Table State after move ----------=========");
		m_tableState.doMove(move);
		System.out.println(m_tableState.toString());
		
		assertTrue((move.equals(new TableMove(new CellIndex(4,"f"),new CellIndex(2,"h")))));
		
		
		System.out.println("---------======== Table State before move ----------=========");
		System.out.println(m_tableState.toString());
		
		// now ask Blue player for next move, the best move is (4,f)->(2,h)
		
		player.setCurrentTableState(m_tableState);
		player.execute();
		move = player.getNextMove();
		
		
		System.out.println("---------======== Player Made Move ----------=========");
		System.out.println(move.toString());
		
		System.out.println("---------======== Table State after move ----------=========");
		m_tableState.doMove(move);
		System.out.println(m_tableState.toString());
		
		assertTrue((move.equals(new TableMove(new CellIndex(3,"f"),new CellIndex(2,"f")))));
		
		System.out.println("---------======== Exiting testIsHeuristicFunctionSound ----------=========");
		
		
		
	}
	
//	public void testIsXstreamWork(){
//		XStream xstream = new XStream();
//		xstream.alias("table", TableState.class);
//		String xml = xstream.toXML(m_tableState);
//		System.out.println(xml);
//	}
}
