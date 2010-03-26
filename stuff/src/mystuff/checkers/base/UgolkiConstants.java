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

import mystuff.checkers.player.MinimaxPlayer;
import mystuff.checkers.player.MinimaxPlayerV2;
import mystuff.checkers.player.PlayerSide;
import mystuff.checkers.table.CellColor;

public final class UgolkiConstants {
	
	public final static int CellColorUnoccupied = 0;
	public final static int CellColorRedIndex = 1;
	public final static int CellColorBlueIndex = 2;
	public static final int TABLEXSIZE = 8;
	public static final int TABLEYSIZE = 8;
	public static final int PLAYERSSIZE = 2;
	public static final String BLUEPLAYERSYMBOL = "O";
	public static final String REDPLAYERSYMBOL = "X";
	public static final String UNOCCUPIEDPLAYERSYMBOL = "+";
	public static final int MaxMovesLimit = 100;
	public static final CellColor CellColorRed = new CellColor(CellColorRedIndex);
	public static final CellColor CellColorBlue = new CellColor(CellColorBlueIndex);
	public static final PlayerSide PlayerSideRed = new PlayerSide(CellColorRed);
	public static final PlayerSide PlayerSideBlue = new PlayerSide(CellColorBlue);
	public static final double startingL1DistanceFromWin4Player = 12.0;
	public static final double winL1DistanceFromWin4Player = 2.0;
	public static final int maxSameColorIndexSize = 9;
	public static final String BluePlayerName = "Blue";
	public static final String RedPlayerName = "Red";
	public static final String CurrentPlayer = MinimaxPlayerV2.class.getName();
}
