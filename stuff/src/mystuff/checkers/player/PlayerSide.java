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

import mystuff.checkers.base.BasePlayerSide;
import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.table.CellColor;

public class PlayerSide extends BasePlayerSide{

	/* (non-Javadoc)
	 * @see mystuff.checkers.base.BaseColor#toString()
	 */
	@Override
	public String toString() {
		String str = "Integer value : " + getColor() + ", Player symbol : " + getPlayerSymbol() + ", Color as string : " + getColorAsString() + ".";
		return super.toString();
	}
	public PlayerSide(CellColor fromColor) {
		super();
		setColor(fromColor.getColor());
	}
	public PlayerSide getNextPlayerSide() {
		if (getColor() == UgolkiConstants.CellColorBlueIndex) {
			return new PlayerSide(new CellColor(UgolkiConstants.CellColorRedIndex));
		}
		else return new PlayerSide(new CellColor(UgolkiConstants.CellColorBlueIndex));
	}
	public String getColorAsString(){
		if(getColor() == UgolkiConstants.CellColorBlueIndex)
			return "Blue";
		else
			return "Red";
	}
	public String getPlayerSymbol(){
		if(getColor() == UgolkiConstants.CellColorBlueIndex)
			return UgolkiConstants.BLUEPLAYERSYMBOL;
		else
			return UgolkiConstants.REDPLAYERSYMBOL;
	}
}
