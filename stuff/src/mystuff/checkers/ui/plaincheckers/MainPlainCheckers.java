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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;



public class MainPlainCheckers extends JApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7874762142864820900L;
	protected static final String BLACK_COMPUTER = "BLACK computer";
	protected static final String RED_COMPUTER = "RED computer";
	public static String SIDE_RED = "red";
	public static String SIDE_BLACK = "black";
	private boolean isDebugMode = false;
	
	public static final String CLICK_TYPE_SELECT = "select";
	public static final String CLICK_TYPE_MOVE = "move";
	
	protected String[] lstClickRcvd = null;
	
//	Consumer clickConsumer = null;
	

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
private static final int TIMER_DELAY = 2000;
public String ownerId = UNIDENTIFIED_ID;

protected CheckersCanvas canvas;
private Timer timer;
protected Thread consumerThread;

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
	 try {
		SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
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
			});
	} catch (InterruptedException e) {
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		e.printStackTrace();
	}
	
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
	
	//timer = new Timer(TIMER_DELAY, new MyTimerListener());
	//clickConsumer = new Consumer(queue);
	//consumerThread =  new Thread(clickConsumer);
	//consumerThread.start();
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

//	BlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();
//	 protected class Consumer implements Runnable {
//		 int count = 0;
//		   private final BlockingQueue<String[]> queue;
//		   Consumer(BlockingQueue<String[]> q) { queue = q; }
//		   public void run() {
//		     try {
//		       while (true) {
//		    	   ++count;
//		    	   int thisCount = count;
//		    	   mylog("enter consume while : " + thisCount);
//		    	   
//		    	   consume(queue.take()); 
//		    	   mylog("after consume while" + thisCount);
//		    	   }
//		     } catch (InterruptedException ex) { ex.printStackTrace();}
//		   }
//		   void consume(Object x) {
//			   String[] clickInput = (String[])x;
//			   mylog("Consume: " + Arrays.toString(clickInput) + "playThread.isAlive: " + canvas.playThread.isAlive());
////			  while(canvas.playThread.isAlive()){
////				  try {
////					  mylog("sleeping in consume");
////					Thread.sleep(3000);
////				} catch (InterruptedException e) {
////					e.printStackTrace();
////				}
////			  }
//			   handleClick(clickInput[0], clickInput[1], clickInput[2], clickInput[3]);
//		   }
//		 }
	
	public void recieveClick(String row, String col, String fromSourceId, String clickType){
		if(!ownerId.equals(fromSourceId)){
			System.out.println("doClick recieved: <" + row + "," + col + ">" + ", " + fromSourceId + "," + clickType);
			//check if it is his turn
			if(!(canvas.getCurrentPlayer() == canvas.waveId2CurrentPlayer(fromSourceId))){
				System.out.println("It's not turn of: " +  fromSourceId + ", skipping." + " From: " + fromSourceId + ", currentPlayer: " + canvas.toPlayerWaveId(canvas.getCurrentPlayer()) );
				return;
			}
//			try {
//				queue.put(new String[] {row,col,fromSourceId,clickType});
//				if(!consumerThread.isAlive()){
//					try{
//						consumerThread.resume();
//					}catch(Exception e){
//						try{
//							consumerThread.start();
//						}catch(Exception e1){
//							mylog(e1.getMessage());
//						}
//					}
//				}
//				
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			handleClick(row, col, fromSourceId, clickType);
		}
	}
	private void handleClick(String row, String col, String fromSourceId,
			String clickType) {
		if(fromSourceId.equals(ownerId)){
			return;
		}
		mylog(getTimeStr() + ": " + "Entering handleClick: " + row + "," + col + "," + fromSourceId + "," + clickType + " : ownerID : " + ownerId);
		System.out.println(getTimeStr() + ": " + "Entering handleClick: " + row + "," + col + "," + fromSourceId + "," + clickType + " : ownerID : " + ownerId);
		lstClickRcvd = new String[] {row,col,fromSourceId,clickType};
		canvas.setClickerId(fromSourceId); // the id is cleared in canvas after each move
		canvas.mousePressedInternal(new int[] {Integer.parseInt(row), Integer.parseInt(col)},false );
		canvas.paint(canvas.getGraphics());
//		try {
//			
//			if(clickType.equals(CLICK_TYPE_SELECT)){
//				if (timer.isRunning()) {
//					timer.restart();
//				} else {
//					timer.start();
//				}
//			}
//		} catch (Exception e) {
//			mylog("Exiting handleClick:error: " + e.getMessage());
//			System.out.println("Exiting handleClick:error: " + e.getMessage());
//		}
		mylog("Exiting handleClick:");
	}

	public void transmitClick(int row, int col, String clickType){
		mylog("entering transmitClick: " + row + "," + col);
		String funcStr = "recieveMsgDoClick('" + row + "','" + col + "'," +"'"+ ownerId.toString()+"'"+ "," + "'"+ clickType +"')";
		try {
	        JSObject window = aquireJSObject();
	       if(window != null){
	    	   window.eval(funcStr);
	       }
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
	        JSObject window = aquireJSObject();
	        if(window != null){
		    	   window.eval(funcStr);
		       }
	    } catch (JSException jse) {
	//    	mylog(jse.getMessage());
	    }catch (Exception e) {
	//    	mylog(e.getMessage());
	    }
	}

	public void recieveClickNewGame(String clickerWaveId){
		mylog("recieved click new game, source: " + clickerWaveId);
		if(clickerWaveId.equals(ownerId) || canvas.gameInProgress || isBothPLayerAI()){
			return;
		}else{
			if(!canvas.redPlayerWaveId.equals(ownerId) || !canvas.blackPlayerWaveId.equals(ownerId) ){
				canvas.resignButton.setEnabled(false);//disable resign buttons for non players, that just watch
			}
			canvas.doNewGame(false);
		}
	}
	/**
	 * if opponent has selected a square but we still didn't received a move after a while
		check if client refresh is needed.
		client refresh is required when: 
		1)One applet waits that other will send a move when square is already selected
		2)Other applet waits when he just sent move and other should send a square select
	 */
	public void doRefreshIfRequired(){
		mylog("Entering isClientRefreshRequired");
		//get the wave state
		String funcStr = null;
		Object wstateStr = null;
		String[] clickLastArr = null;
		String[] clickLLastArr = null;
		try {
			funcStr = "getWstate";
	        JSObject window = aquireJSObject();
	        if(window == null) return;
	        wstateStr = window.call(funcStr,null);
	        
	        if(wstateStr == null){
	        	mylog("Exiting isClientRefreshRequired: false" );
	        	return ;
	        }
	        
	        int lastClickNum = Integer.parseInt(String.valueOf(wstateStr).split(",")[3]);
	        
	        funcStr = "getClickData";
	        Object clickDataStr1  = window.call(funcStr,new Object[] {lastClickNum});
	        if(clickDataStr1 == null) {
	        	mylog("Exiting isClientRefreshRequired: false" );
	        	return ;
	        }
	        clickLastArr = String.valueOf(clickDataStr1).split("$$");
	        
	        Object clickDataStr2  = window.call(funcStr,new Object[] {lastClickNum-1});
	        if(clickDataStr2 == null) {
	        	mylog("Exiting isClientRefreshRequired: false" );
	        	return ;
	        }
	        clickLLastArr = String.valueOf(clickDataStr2).split("$$");
	        
	        // now check
	        if(clickLastArr[3].equals(CLICK_TYPE_MOVE) && clickLLastArr[3].equals(CLICK_TYPE_SELECT) && !clickLastArr[2].equals(ownerId) && !clickLLastArr[2].equals(ownerId) 
	        		&& clickLLastArr[0].equals(lstClickRcvd[0]) &&  clickLLastArr[1].equals(lstClickRcvd[1])){
	        	mylog("Dedected Freeze! missing: " + Arrays.toString(clickLLastArr));
	        	System.out.println("Dedected Freeze! missing: ");
	        	handleClick(clickLastArr[0],clickLastArr[1],clickLastArr[2],clickLastArr[3]);
	        	return ;
	        }else if(clickLastArr[3].equals(CLICK_TYPE_SELECT) && clickLLastArr[3].equals(CLICK_TYPE_MOVE) && !clickLastArr[2].equals(ownerId) && !clickLLastArr[2].equals(ownerId) 
	        		&& clickLLastArr[0].equals(lstClickRcvd[0]) &&  clickLLastArr[1].equals(lstClickRcvd[1])){
	        	mylog("Dedected Freeze! missing: " + Arrays.toString(clickLLastArr));
	        	System.out.println("Dedected Freeze! missing: ");
	        	handleClick(clickLastArr[0],clickLastArr[1],clickLastArr[2],clickLastArr[3]);
	        	return ;
	        }
	        
	    } catch (JSException jse) {
	//    	mylog(jse.getMessage());
	    }catch (Exception e) {
	    	mylog(e.getMessage());
	    }
	    mylog("Exiting isClientRefreshRequired: false" );
	}

	private boolean isBothPLayerAI() {
		return canvas.isAutoBlack() && canvas.isAutoRed();
	}
	public void recieveWaveId(String viewerId){
		ownerId = viewerId;
		mylog("recieved owner id: " + ownerId );
		System.out.println("recieved owner id: " + ownerId );
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
		mylog("entering receiveGameOver: " + looserWaveId);
		if(canvas.gameInProgress == true){
			String[] strMsg = new String[2];
			if(looserWaveId.equals(canvas.redPlayerWaveId)){
				String playerNameResigned = canvas.getRedPLayerName();
				String playerNameWon = canvas.getBlackPLayerName();
				strMsg[0] = playerNameResigned + " resigns.";
				strMsg[1] = playerNameWon + " wins.";
				canvas.gameOver(strMsg, canvas.waveId2CurrentPlayer(looserWaveId), false);
			}else{
				String playerNameResigned = canvas.getBlackPLayerName();
				String playerNameWon = canvas.getRedPLayerName();
				strMsg[0] = playerNameResigned + " resigns.";
				strMsg[1] = playerNameWon + " wins.";
				canvas.gameOver(strMsg, canvas.waveId2CurrentPlayer(looserWaveId), false);
			}
		}
	}
	
	public void setDebugMode(String isDebug){
		System.out.println("Entering debug mode: " + isDebug);
//		isDebugMode = Boolean.parseBoolean(isDebug);
		System.out.println("Exiting debug mode: ");
	}

	private void myhlog(String str){
		
			JSObject window = aquireJSObject();
			if(window != null){
				window.eval("mylog('" + "applet: " +  str + "')");
			}
	}
	protected JSObject aquireJSObject() {
		JSObject window = null;
		try{
			window = JSObject.getWindow(this);
		} catch (JSException jse) {}
		catch(java.lang.ClassCastException cce){}
		return window;
	}
	public void mylog(String str){
		String logStr = null;
		if(ownerId != null){
			logStr = ownerId.split("@")[0] + " : " + str;
		}else{
			logStr = getTimeStr() + ": " + str;
		}
		if(isDebugMode){
			myhlog(logStr);
		}else{
			System.out.println(getTimeStr() + ": " + logStr);
		}
	}
	public String getTimeStr() {
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SS");
		return sdf.format(time);
	}


	protected class  MyTimerListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			mylog("Entering timer");
			Date date = new Date();
			SimpleDateFormat fmt = new SimpleDateFormat("hh:mm:ss");
			System.out.println(fmt.format(date) + ": " + "In timer, last move: " + Arrays.toString(lstClickRcvd));
			if(lstClickRcvd[3] != null && lstClickRcvd[3].equals(CLICK_TYPE_MOVE)){
				timer.stop();
			}else{
				// if it is still my turn
				if(canvas.getCurrentPlayer() == canvas.waveId2CurrentPlayer(ownerId)){
					doRefreshIfRequired();
				}
			}
			mylog("Exiting timer");
		}
	}
   
} 


