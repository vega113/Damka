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
package mystuff.checkers.exceptions;

import mystuff.checkers.player.PlayerSide;

/**
 * @author Vega
 *
 */
public class WinConditionReachedException extends RuntimeException{

	private PlayerSide side = null;
	
	public WinConditionReachedException(PlayerSide side) {
		super("Player " + side.getColorAsString()+" with symbol " + side.getPlayerSymbol()+ " reached win condition");
		this.side = side;
	}
	/**
	 * @return Returns the side.
	 */
	public PlayerSide getSide() {
		return side;
	}
	/**
	 * @param side The side to set.
	 */
	protected void setSide(PlayerSide side) {
		this.side = side;
	}

}
