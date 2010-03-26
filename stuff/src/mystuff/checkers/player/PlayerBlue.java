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
package mystuff.checkers.player;

import mystuff.checkers.base.BaseAutomatePlayer;
import mystuff.checkers.table.TableMove;

public class PlayerBlue extends BaseAutomatePlayer {

	public PlayerBlue(PlayerSide playerSide) {
		super(playerSide);
		// TODO Auto-generated constructor stub
	}

	protected TableMove[] getValidMoves(PlayerSide side) {
		 TableMove[] outputMoves = getClonedTable().getAllValidMovesForPlayerSide(side);
		 m_totalConsideredMoves+=outputMoves.length;
		 return outputMoves;
	}

}
