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
package mystuff.checkers.ui.plaincheckers;
/*
   This applet lets two uses play checkers against each other.
   Red always starts the game.  If a player can jump an opponent's
   piece, then the player must jump.  When a plyer can make no more
   moves, the game ends.
   
   This file defines four classes: the main applet class, MainPlainCheckers;
   CheckersCanvas, CheckersMove, and CheckersData.
   (This is not very good style; the other classes really should be
   nested classes inside the MainPlainCheckers class.)
*/

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import mystuff.checkers.ui.ugolki.MainVisualUgolki;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;



public class MainPlainCheckers extends JApplet {
	
	protected static final String BLACK_COMPUTER = "BLACK computer";
	protected static final String RED_COMPUTER = "RED computer";
	public static String SIDE_RED = "red";
	public static String SIDE_BLACK = "black";
	private boolean isDebugMode = true;
	

   /* The main applet class only lays out the applet.  The work of
      the game is all done in the CheckersCanvas object.   Note that
      the Buttons and Label used in the applet are defined as 
      instance variables in the CheckersCanvas class.  The applet
      class gives them their visual appearance and sets their
      size and positions.*/
	

   protected static final int BOARDCOORD = 30;
protected static final int BOARDSIZE = 164*2;
//public MainPlainCheckers(String string) {
////		super(string);
////		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
//	}

public final static String UNIDENTIFIED_ID = "unidentified";
public String ownerId = UNIDENTIFIED_ID;

protected CheckersCanvas canvas;

   public MainPlainCheckers() {
//		setTitle(getMainClassName());
		addComponentListener(new java.awt.event.ComponentAdapter() {
	        public void componentResized(java.awt.event.ComponentEvent evt) {
	                formComponentResized(evt);
	        }
	});

	}
   protected void formComponentResized(ComponentEvent evt) {
	   System.out.println(""+evt.getID());
}
public static void main(String[] args) {
	   MainPlainCheckers frame = initMainClass();
	   initFrame(frame);
   }

public void init(){
	final MainPlainCheckers frame = this;
//	 try {
//		SwingUtilities.invokeAndWait(new Runnable() {
//				public void run() {
//					
//				}
//			});
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	} catch (InvocationTargetException e) {
//		e.printStackTrace();
//	}
	canvas = initFrameNonStatic(frame);
	canvas.setAutoBlack(true);
	canvas.setAutoRed(true);
	canvas.setRedPLayerName(RED_COMPUTER);
	canvas.setBlackPLayerName(BLACK_COMPUTER);
	canvas.setOwnerRed(false);
	canvas.setOwnerBlack(false);
	canvas.setApplet(frame);
	System.out.println("Initialized! Applet ownerId: " + ownerId );
	}
/**
 * @param frame
 */
protected static void initFrame(MainPlainCheckers frame) { 
	frame.setLayout(null);  // I will do the layout myself.
	   
	      /* Create the components and add them to the applet. */

	      CheckersCanvas board = frame.initBoard();
	          // Note: The constructor creates the buttons board.resignButton
	          // and board.newGameButton and the Label board.message.
	      frame.getContentPane().add(board);
	      board.newGameButton.setBackground(Color.lightGray);
	      frame.getContentPane().add(board.newGameButton);

	      board.resignButton.setBackground(Color.lightGray);
	      frame.getContentPane().add(board.resignButton);

	      board.message.setForeground(Color.darkGray);
	      board.message.setBackground(Color.gray);
	      board.message.setFont(new Font("Serif", Font.BOLD, 13));
	      frame.getContentPane().add(board.message);

	      for (int i = 0; i < board.letters.length; i++) {
	    	  frame.getContentPane().add(board.letters[i]);
	      }
	      
	      for (int i = 0; i < board.letters.length; i++) {
	    	  frame.getContentPane().add(board.digits[i]);
	      }
	      
	      /* Set the position and size of each component by calling
	         its setBounds() method. */

	      board.setBounds(BOARDCOORD,BOARDCOORD,BOARDSIZE+4,BOARDSIZE+4); // Note:  size MUST be multiple of 164-by-164 !
	      board.newGameButton.setBounds(BOARDCOORD+BOARDSIZE+12, BOARDCOORD, 100, 30);
	      board.resignButton.setBounds(BOARDCOORD+BOARDSIZE+12, BOARDCOORD+52, 100, 30);
	      board.message.setBounds(BOARDCOORD+12, BOARDCOORD+BOARDSIZE+30,330, 30);
	      
	      for (int i = 0; i < board.letters.length; i++) {
	    	  board.digits[i].setFont(new Font("Serif", Font.BOLD, 18));
	    	  board.digits[i].setForeground(Color.blue);
	    	  board.digits[i].setBounds(BOARDCOORD/2, BOARDCOORD + i*(BOARDSIZE/8) + 16, 12, 14);
	      }
	      
	      for (int i = 0; i < board.letters.length; i++) {
	    	  board.letters[i].setFont(new Font("Serif", Font.BOLD, 18));
	    	  board.letters[i].setForeground(Color.blue);
	    	  board.letters[i].setBounds(BOARDCOORD+ i*(BOARDSIZE/8)+ 16, BOARDCOORD + BOARDSIZE + 8 , 12, 12);
	      }
	      
	      
	      frame.setSize(600, 500);
	      frame.setVisible(true);
}


public CheckersCanvas initFrameNonStatic(MainPlainCheckers frame) {

	frame.setLayout(null);  // I will do the layout myself.

	/* Create the components and add them to the applet. */

	CheckersCanvas board = frame.initBoard();
	// Note: The constructor creates the buttons board.resignButton
	// and board.newGameButton and the Label board.message.
	frame.getContentPane().add(board);
	int red = Color.red.getRGB();
	int green = Color.green.getRGB();
	int blue = Color.blue.getRGB();
	int col1 = 
		(int) (Math.sqrt(180*red + 204*green + 100*blue));
	frame.getContentPane().setBackground(new Color(220,220,220));
	board.newGameButton.setBackground(Color.lightGray);
	frame.getContentPane().add(board.newGameButton);

	board.resignButton.setBackground(Color.lightGray);
	frame.getContentPane().add(board.resignButton);

	board.message.setForeground(Color.darkGray);
	board.message.setBackground(Color.lightGray);
	board.message.setFont(new Font("Serif", Font.BOLD, 12));
	board.message.setColumns(16);
	frame.getContentPane().add(board.message);

	for (int i = 0; i < board.letters.length; i++) {
		frame.getContentPane().add(board.letters[i]);
	}

	for (int i = 0; i < board.letters.length; i++) {
		frame.getContentPane().add(board.digits[i]);
	}

	/* Set the position and size of each component by calling
	         its setBounds() method. */

	board.setBounds(BOARDCOORD,BOARDCOORD,BOARDSIZE+4,BOARDSIZE+4); // Note:  size MUST be multiple of 164-by-164 !
	board.newGameButton.setBounds(BOARDCOORD+BOARDSIZE+12, BOARDCOORD, 100, 30);
	board.resignButton.setBounds(BOARDCOORD+BOARDSIZE+12, BOARDCOORD+52, 100, 30);
	board.message.setBounds(BOARDCOORD+12, BOARDCOORD+BOARDSIZE+30,305, 50);

	int digitsSize = 18;
	int fontSize = 16;
	for (int i = 0; i < board.letters.length; i++) {
		board.digits[i].setFont(new Font("Serif", Font.BOLD, digitsSize));
		board.digits[i].setForeground(Color.darkGray);
		board.digits[i].setBounds(BOARDCOORD/2, BOARDCOORD + i*(BOARDSIZE/8) + 16, 12, 15);
	}

	for (int i = 0; i < board.letters.length; i++) {
		board.letters[i].setFont(new Font("Serif", Font.BOLD, fontSize));
		board.letters[i].setForeground(Color.darkGray);
		board.letters[i].setBounds(BOARDCOORD+ i*(BOARDSIZE/8)+ 16, BOARDCOORD + BOARDSIZE + 8 , 12, 12);
	}


	frame.setSize(600, 500);
	frame.setVisible(true);
	return board;
}
/**
 * @return
 */
	protected static MainPlainCheckers initMainClass() {
		System.out.println("MainPlainCheckers initMainClass");
	//	return new MainPlainCheckers(getMainClassName());
		return new MainPlainCheckers();
	}
	protected static  String getMainClassName() {
		String mainClassName = "MainPlainCheckers";
		return mainClassName;
	}
	/**
	 * @return
	 */
	protected  CheckersCanvas initBoard() {
		System.out.println("plain checkers init");
		CheckersCanvas board = new CheckersCanvas();
		board.init();
		board.setApplet(this);
		return board;
	}



	public void recieveClick(String row, String col, String fromSourceId){
		if(!ownerId.equals(fromSourceId)){
			//check if it is his turn
			if(!(canvas.getCurrentPlayer() == canvas.id2CurrentPlayer(fromSourceId))){
				return;
			}
			System.out.println("doClick recieved: <" + row + "," + col + ">");
			canvas.setClickerId(fromSourceId); // the id is cleared in canvas after each move
			canvas.mousePressedInternal(new int[] {Integer.parseInt(row), Integer.parseInt(col)},false );
		}
	}

	public void transmitClick(int row, int col){
		mylog("entering transmitClick: " + row + "," + col);
		String funcStr = "recieveMsgDoClick('" + row + "','" + col + "'," +"'"+ ownerId.toString() + "')";
		try {
	        JSObject window = JSObject.getWindow(this);
	        window.eval(funcStr);
	    } catch (JSException jse) {
	//       System.out.println(jse.getMessage());
	    }catch (Exception e) {
	//    	mylog(e.getMessage());
	    }
	    mylog("exiting transmitClick: " + row + "," + col);
	}

	public void transmitClickNewGame(){
		/*
		 * There could be several setups for the game
		 * 1)2 computers
		 * 2)Red human black comp
		 * 3)Black human - red comp
		 * 4)2 humans
		 * If 2 computers play - they should play only on comp of whoever clicked start
		 * In any way, computers should play on comp of whoever clicked start
		 * If human plays - it should be checked that current side belongs to owner if this side
		 */
		String funcStr = "recieveMsgDoClickNewGame('" + ownerId + "')";
		try {
	        JSObject window = JSObject.getWindow(this);
	        window.eval(funcStr);
	        window.eval("mylog('ver 22')");
	    } catch (JSException jse) {
	//    	mylog(jse.getMessage());
	    }catch (Exception e) {
	//    	mylog(e.getMessage());
	    }
	}

	public void recieveClickNewGame(String clickerWaveId){
		mylog("recieved click new game, source: " + clickerWaveId);
		if(clickerWaveId.equals(ownerId) || canvas.gameInProgress){
			return;
		}else{
			if(!canvas.redPlayerWaveId.equals(ownerId) || !canvas.blackPlayerWaveId.equals(ownerId) ){
				canvas.resignButton.setEnabled(false);//disable resign buttons for non players, that just watch
			}
			canvas.doNewGame(false);
		}
	}

	public void recieveWaveId(String viewerId){
		ownerId = viewerId;
		mylog("recieved owner id: " + ownerId );
	}
	
	public void receivePlayerInfo(String side,String playerName, String playerWaveId){
		if(side.equals(SIDE_RED)){
			canvas.setRedPLayerName(playerName);
			canvas.setRedPLayerWaveId(playerWaveId);
			canvas.setAutoRed(false);
			//ownerId = UNIDENTIFIED_ID only if outside wave
			canvas.setOwnerRed(playerWaveId.equals(ownerId) || ownerId.equals(UNIDENTIFIED_ID));
		}else if(side.equals(SIDE_BLACK)){
			canvas.setBlackPLayerName(playerName);
			canvas.setBlackPLayerWaveId(playerWaveId);
			canvas.setAutoBlack(false);
			canvas.setOwnerBlack(playerWaveId.equals(ownerId) || ownerId.equals(UNIDENTIFIED_ID));
		}
		mylog("receivePlayerInfo: " + side + ", " +  playerName + ", playerWaveId: " + playerWaveId + ", ownerId: " +  ownerId);
	}

	public void transmitGameOver(String looserWaveId, String winnerWaveId){
		System.out.println("in transmitGameOver applet");
		String funcStr = "recieveGameOver('" + ownerId + "','" + looserWaveId + "','" + winnerWaveId + "')";
		try {
	        JSObject window = JSObject.getWindow(this);
	        window.eval(funcStr);
	        mylog("game over");
	    } catch (JSException jse) {
	//    	mylog(jse.getMessage());
	    }catch (Exception e) {
	//    	mylog(e.getMessage());
	    }
	}
	
	public void receiveGameOver(String looserWaveId, String winnerWaveId){
		if(canvas.gameInProgress == true){
			canvas.setCurrentPlayer(canvas.id2CurrentPlayer(looserWaveId));
			canvas.doResign();
		}
	}
	
	public void setDebugMode(String isDebug){
		System.out.println("Entering debug mode: " + isDebug);
		isDebugMode = Boolean.parseBoolean(isDebug);
		System.out.println("Exiting debug mode: ");
	}

	private void myhlog(String str){
		try{
			JSObject window = JSObject.getWindow(this);
			window.eval("mylog('" + "applet: " +  str + "')");
		} catch (JSException jse) {}
	}
	public void mylog(String str){
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SS");
	
		String logStr = null;
		if(ownerId != null){
			logStr = ownerId.split("@")[0] + " : " + str;
		}else{
			logStr = sdf.format(time) + ": " + str;
		}
		if(isDebugMode){
			myhlog(logStr);
		}else{
			System.out.println(logStr);
		}
	}


   
} 


