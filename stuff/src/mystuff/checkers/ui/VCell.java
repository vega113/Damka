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
package mystuff.checkers.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;


import mystuff.checkers.base.UgolkiConstants;
import mystuff.checkers.table.CellColor;

public class VCell {
	public static Color redHighlight = Color.red.darker();
	public static Color blueHighlight = Color.blue.darker();
	public static Color sqColorWhite = Color.gray.brighter().brighter().brighter();
	public static Color sqColorBlack = Color.gray.darker().darker().darker();
	public static Color redButtonColor = Color.red.darker();
	public static Color blueButtonColor = Color.blue.darker();
	
	private Color highlightSqColor;
	private Color sqColor;
	private Color buttonColor;
	protected RoundButton jbutton;
	protected JPanel sqArea;
	public void VCell(CellColor cellColor, Color sqColor){
		if(cellColor.getColor() == UgolkiConstants.CellColorBlueIndex){
			this.buttonColor = blueButtonColor;
			highlightSqColor = blueHighlight;
			this.sqColor = sqColor;
			initialize();
		}
	}
	public void initialize() {
		
		sqArea = new CellArea();
		jbutton = new RoundButton(buttonColor,sqArea);
		sqArea.add(jbutton);
		sqArea.setBorder(BorderFactory.createLineBorder (Color.red, 2));

	}
//	 Test routine.
	   public static void main(String[] args) {
//		
		   JFrame frame = new JFrame();
		    frame.getContentPane().setBackground(Color.yellow);

		 
		   VCell vcell = new VCell();
		   vcell.initialize();
		   frame.getContentPane().add(vcell.jbutton);
		    frame.getContentPane().setLayout(new FlowLayout());
		    frame.setSize(200, 300);
		    frame.setVisible(true);
		   
	   }
	
}
