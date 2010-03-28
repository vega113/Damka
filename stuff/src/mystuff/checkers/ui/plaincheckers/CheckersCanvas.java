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

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextArea;

import sun.security.acl.OwnerImpl;

public class CheckersCanvas extends Canvas implements ActionListener, MouseListener {

	// This canvas displays a 160-by-160 checkerboard pattern with
	// a 2-pixel black border.    This class does
	// the work of letting the users play checkers, and it displays
	// the checkerboard.
	
	private String redPLayerName;
	private String blackPLayerName;
	
	private boolean isAutoRed = true;
	private boolean isAutoBlack = true;
	
	protected Graphics backg; // used for buffering
	protected Image backbuffer;// used for buffering
	
	protected int[] pos;//hold last clicked position


	
	/**
	 * @return the redPLayerName
	 */
	public String getRedPLayerName() {
		return redPLayerName;
	}


	/**
	 * @param redPLayerName the redPLayerName to set
	 */
	public void setRedPLayerName(String redPLayerName) {
		this.redPLayerName = redPLayerName;
	}


	/**
	 * @return the blackPLayerName
	 */
	public String getBlackPLayerName() {
		return blackPLayerName;
	}


	/**
	 * @param blackPLayerName the blackPLayerName to set
	 */
	public void setBlackPLayerName(String blackPLayerName) {
		this.blackPLayerName = blackPLayerName;
	}


	protected static final int RECTSIZE = 40;
	protected int OVALSIZE = (int)(RECTSIZE*0.75-6);
	protected static final int DIMENSION = 180;
	protected  int SHIFTLEFT = 4;
	protected  int SCALE = 22;
	public Button resignButton;   // Current player can resign by clicking this button.
	public Button newGameButton;  // This button starts a new game.  It is enabled only
	public Label[] digits;
	public Label[] letters;
	//     when the current game has ended.

	public JTextArea message;   // A label for displaying messages to the user.

	CheckersData board;  // The data for the checkers board is kept here.
	//    This board is also responsible for generating
	//    lists of legal moves.

	public boolean gameInProgress; // Is a game currently in progress?

	/* The next three variables are valid only when the game is in progress. */

	int currentPlayer;      // Whose turn is it now?  The possible values
	//    are CheckersData.RED and CheckersData.BLACK.
	int selectedRow, selectedCol;  // If the current player has selected a piece to
	//     move, these give the row and column
	//     containing that piece.  If no piece is
	//     yet selected, then selectedRow is -1.
	CheckersMove[] legalMoves;  // An array containing the legal moves for the
	//   current player.
	protected MainPlainCheckers mainPlainCheckers;
	public MouseEvent evt;
	
	protected CanvasThread playThread = null;;

	public CheckersCanvas() {
		// Constructor.  Create the buttons and lable.  Listen for mouse
		// clicks and for clicks on the buttons.  Create the board and
		// start the first game.
	}
	
	public CheckersCanvas(boolean isAutoRed, boolean isAutoBlack ) {
		this.isAutoRed = isAutoRed;
		this.isAutoBlack = isAutoBlack;
	}


	/**
	 * 
	 */
	public void init() {
		setBackground(Color.black);
		addMouseListener(this);
		Font font = Font.getFont("Serif");
		if(font == null){
			font = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()[0];
		}
		if(font != null){
			font.deriveFont(10);
			font.deriveFont(Font.BOLD);
			setFont(font);
		}else{
			setFont(new  Font("Serif", Font.PLAIN, 12));
		}
		
		resignButton = new Button("Resign");
		resignButton.addActionListener(this);
		newGameButton = new Button("New Game");
		newGameButton.addActionListener(this);
		message = new JTextArea();
		message.setLineWrap(true);
		message.setWrapStyleWord(true);

		message.setEditable(false);
		String[] charsLet = {"A","B","C","D","E","F","G","H"};
		letters = new Label[8];
		for (int i = 0; i < letters.length; i++) {
			letters[i] = new Label(charsLet[i],Label.CENTER);
		}
		String[] charsDig = {"1","2","3","4","5","6","7","8"};
		 digits = new Label[8];
		for (int i = 0; i < digits.length; i++) {
			digits[i] = new Label(charsDig[i],Label.CENTER);
		}
		board = initData();
		
		//doNewGame();
	}


	/**
	 * @return
	 */
	protected CheckersData initData() {
		CheckersData checkersData = new CheckersData();
		checkersData.setUpGame();
		return new CheckersData();
	}


	public void actionPerformed(ActionEvent evt) {
		// Respond to user's click on one of the two buttons.
		Object src = evt.getSource();
		if (src == newGameButton)
			doNewGame(true);
		else if (src == resignButton)
			doResign();
	}


	public boolean doNewGame(boolean isTransmit) {
		//let's check if we have 2 computers
		print("in checkers canvas: doNewGame");
		if (gameInProgress == true) {
			// This should not be possible, but it doens't 
			// hurt to check.
			message.setText("Finish the current game first!");
			print("cannot start new game - already game in progress!");
			return false;
		}
		if(redPlayerWaveId.equals(initRedId) && isAutoRed()){
			setOwnerRed(true);
		}
		if(blackPlayerWaveId.equals(initBlackId) && isAutoBlack()){
			setOwnerBlack(true);
		}
		// Begin a new game.
		
		if(mainPlainCheckers != null && isTransmit){
			mainPlainCheckers.transmitClickNewGame();
			mainPlainCheckers.mylog("new game!");
		}
		initAutomatePlayers(); 
		board = initData();
		currentPlayer = CheckersData.RED;   //" + getRedPLayerName() + "moves first.
		legalMoves = board.getLegalMoves(CheckersData.RED);  // Get RED's legal moves.
		selectedRow = -1;   //" + getRedPLayerName() + "has not yet selected a piece to move.
		message.setText(getRedPLayerName() + ":  Make your move.");
		gameInProgress = true;
		newGameButton.setEnabled(false);
		resignButton.setEnabled(true);
		paint(getGraphics());
		playThread = initCanvasThread();
		playThread.start();
		mainPlainCheckers.mylog("playThread: started");
		return gameInProgress;
	}



	protected CanvasThread initCanvasThread() {
		return  new CanvasThread();
	}


	public void doResign() {
		// Current player resigns.  Game ends.  Opponent wins.
		if (gameInProgress == false) {
			message.setText("There is no game in progress!");
			return;
		}
		String[] strMsg;
		if (currentPlayer == CheckersData.RED){
			strMsg= new String[2];
			strMsg[0] = getRedPLayerName() + " resigns.";
			strMsg[1] =  getBlackPLayerName() + " wins.";
			gameOver(strMsg,CheckersData.BLACK,true);
		}
		else{
			strMsg= new String[2];
			strMsg[0] = getBlackPLayerName() + " resigns.";
			strMsg[1] =  getRedPLayerName() + " wins.";
			gameOver(strMsg,CheckersData.RED,true);
		}
	}

	public void stopThreads(){
		try{
			playThread.stop();
		}catch(Exception e){
			mainPlainCheckers.mylog(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param str
	 * @param side
	 * @param isGameOverByResign
	 */
	void gameOver(String[] str, int side, boolean isGameOverByResign) {
		gameInProgress = false;
		// The game ends.  The parameter, str, is displayed as a message
		// to the user.  The states of the buttons are adjusted so playes
		// can start a new game.
		message.setText("");
		message.append(str[0]);
		if(str.length > 1 && str[1] != null){
			message.append("\n");
			message.append(str[1]);
		}
		newGameButton.setEnabled(true);
		resignButton.setEnabled(false);
		if(mainPlainCheckers != null){
			redPlayerWaveId = initRedId;
			blackPlayerWaveId = initBlackId;
			setRedPLayerName(MainPlainCheckers.RED_COMPUTER);
			setBlackPLayerName(MainPlainCheckers.BLACK_COMPUTER);
			setAutoBlack(true);
			setAutoRed(true);
			setOwnerBlack(true);
			setOwnerRed(true);
			if (!isGameOverByResign) {
				if (side == CheckersData.RED) {
					mainPlainCheckers.transmitGameOver(redPlayerWaveId,
							blackPlayerWaveId);
					mainPlainCheckers.mylog("transmitGameOver: red");
				} else {
					mainPlainCheckers.transmitGameOver(blackPlayerWaveId,
							redPlayerWaveId);
					mainPlainCheckers.mylog("transmitGameOver: black");
				}
			}else{
				if(mainPlainCheckers.ownerId.equals(blackPlayerWaveId)){
					mainPlainCheckers.transmitGameOver(blackPlayerWaveId,
							redPlayerWaveId);
					mainPlainCheckers.mylog("transmitGameOver: black");
				}else{
					mainPlainCheckers.transmitGameOver(redPlayerWaveId,blackPlayerWaveId);
					mainPlainCheckers.mylog("transmitGameOver: red");
				}
			}
		}
		try{
			playThread.stop();
			mainPlainCheckers.mylog("play thread stopped");
		}catch(Exception e){}
		
		
		
//		mainPlainCheckers
	}


	protected boolean doClickSquare(int row, int col ) {
		mainPlainCheckers.mylog("entering doClickSquare: " + row + "," + col);
		// This is called by mousePressed() when a player clicks on the
		// square in the specified row and col.  It has already been checked
		// that a game is, in fact, in progress.

		/* If the player clicked on one of the pieces that the player
         can move, mark this row and col as selected and return.  (This
         might change a previous selection.)  Reset the message, in
         case it was previously displaying an error message. */
//		boolean isOwnerClick = false;
//		if(getCurrentPlayer() == CheckersData.RED && isOwnerRed){
//			isOwnerClick = true;
//		}
//		if(getCurrentPlayer() == CheckersData.BLACK && isOwnerBlack()){
//			isOwnerClick = true;
//		}
//	if(!isOwnerRed || !isOwnerBlack){
//		print("isOwnerRed: " + isOwnerRed + ", isOwnerBlack: " + isOwnerBlack + ", cplayer: " + getCurrentPlayer());
//	}
		
		for (int i = 0; i < legalMoves.length; i++)
			if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
				selectedRow = row;
				selectedCol = col;
				//check if selecte
				if (currentPlayer == CheckersData.RED)
					message.setText(getRedPLayerName() + ":  Make your move.");
				else
					message.setText(getBlackPLayerName() + ":  Make your move.");
				
				if (mainPlainCheckers != null  && isTransmitClick) {
					mainPlainCheckers.transmitClick(row, col);
				}else{
					mainPlainCheckers.mylog("not transmitting click: " + row + "," + col);
				}
				mainPlainCheckers.mylog("exiting doClickSquare (select square): " + row + "," + col);
				return false;
			}

		/* If no piece has been selected to be moved, the user must first
         select a piece.  Show an error message and return. */

		if (selectedRow < 0) {
			message.setText("Click the piece you want to move.");
			return false;
		}

		/* If the user clicked on a squre where the selected piece can be
         legally moved, then make the move and return. */

		for (int i = 0; i < legalMoves.length; i++)
			if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol
					&& legalMoves[i].toRow == row && legalMoves[i].toCol == col) {
				doMakeMove(legalMoves[i]);
//				if (mainPlainCheckers != null && toPlayerSideId(getCurrentPlayer()).equals(clickerId)) {
				mainPlainCheckers.mylog("curentPlayerSide: " + toPlayerSideId(getCurrentPlayer()) + ", clickerId: " + clickerId);
				if (mainPlainCheckers != null  && isTransmitClick) {
					mainPlainCheckers.transmitClick(row, col);
				}else{
					mainPlainCheckers.mylog("not transmitting click: " + row + "," + col);
				}
				mainPlainCheckers.mylog("exiting doClickSquare (move): " + legalMoves[i].toString());
				return true;
			}

		/* If we get to this point, there is a piece selected, and the square where
         the user just clicked is not one where that piece can be legally moved.
         Show an error message. */

		message.setText("Click the square you want to move to.");
		return false;
	}  // end doClickSquare()


	protected void doMakeMove(CheckersMove move) {
		doMovePiece(move);
		selectedRow = -1;
		highlightOnlyMove();
//		repaint();
	}  


	/**
	 * 
	 */
	protected void highlightOnlyMove() {
		/* As a courtesy to the user, if all legal moves use the same piece, then
         select that piece automatically so the use won't have to click on it
         to select it. */

		if (legalMoves != null) {
			boolean sameStartSquare = true;
			for (int i = 1; i < legalMoves.length; i++)
				if (legalMoves[i].fromRow != legalMoves[0].fromRow
						|| legalMoves[i].fromCol != legalMoves[0].fromCol) {
					sameStartSquare = false;
					break;
				}
			if (sameStartSquare) {
				selectedRow = legalMoves[0].fromRow;
				selectedCol = legalMoves[0].fromCol;
			}
		}
	}


	/**
	 * 
	 */
	protected void switchNextPlayer(int currentPlayer) {
		mainPlainCheckers.mylog("Entering switchNextPlayer: " + toPlayerSideId(currentPlayer));
		if (getCurrentPlayer() == CheckersData.BLACK) {
			
			setCurrentPlayer(CheckersData.RED);// go on to the next player
			if (isWinConditionForSideReached(CheckersData.RED))
				gameOver(msgRedWins(),CheckersData.RED,false);
			
			else if (legalMoves[0].isJump())
				message.setText(getRedPLayerName() + ":  Make your move.  You must jump.");
			else
				message.setText(getRedPLayerName() + ":  Make your move.");
			
			
		}
		else {
			setCurrentPlayer(CheckersData.BLACK);// go on to the next player
			
			if (isWinConditionForSideReached(CheckersData.BLACK))
				gameOver(msgBlackWins(),CheckersData.BLACK,false);
			
			else if (legalMoves[0].isJump())
				message.setText(getBlackPLayerName() + ":  Make your move.  You must jump.");
			else
				message.setText(getBlackPLayerName() + ":  Make your move.");
			
			
		}
		setLegalMoves(getBoard().getLegalMoves(getCurrentPlayer()));
		mainPlainCheckers.mylog("Exiting switchNextPlayer");
		
	}


	protected void doAutoPlay(int currentPlayer2) {
		throw new RuntimeException("Method Not Implemented: doAutoPlay(int currentPlayer2) ");
	}


	protected boolean isAutoPlayer(int currentPlayer2) {
		 if (currentPlayer2 == CheckersData.BLACK) {
	        return isAutoBlack();
	      }
	      else {
	    	  return isAutoRed();
	      }
	 }


	/**
	 *  Thiis is called when the current player has chosen the specified
	    move.  Make the move, and then either end or continue the game
	    appropriately.

	 * @param move
	 */
	protected void doMovePiece(CheckersMove move) {

		board.makeMove(move);

		/* If the move was a jump, it's possible that the player has another
	 jump.  Check for legal jumps starting from the square that the player
	 just moved to.  If there are any, the player must jump.  The same
	 player continues moving.
		 */

		if (move.isJump()) {
			legalMoves = board.getLegalJumpsFrom(currentPlayer,move.toRow,move.toCol);
			if (legalMoves != null) {
				if (currentPlayer == CheckersData.RED)
					message.setText(getRedPLayerName() + ":  You must continue jumping.");
				else
					message.setText(getBlackPLayerName() + ":  You must continue jumping.");
				selectedRow = move.toRow;  // Since only one piece can be moved, select it.
				selectedCol = move.toCol;
//				repaint();
			}
		}
	}


	/**
	 * @param i 
	 * @return
	 */
	protected boolean isWinConditionForSideReached(int side) {
		return legalMoves == null;
	}


	public void update(Graphics g) {
		// Draw  checkerboard pattern in gray and lightGray.  Draw the
		// checkers.  If a game is in progress, hilite the legal moves.
		Rectangle rec= getBounds();
		backbuffer = createImage( (int)rec.getWidth()+2, (int)rec.getHeight() + 2);
		try{
	    backg = backbuffer.getGraphics();
		}catch(java.lang.NullPointerException npe){
			System.err.println(npe.getMessage() + ", trying again..");
			backg = backbuffer.getGraphics();
		}
	    

		if (backg != null) {
			paintBoard(backg);
			if (gameInProgress) {
				highlightPiecesCanBeMoved(backg);
				// If a piece is selected for moving (i.e. if selectedRow >= 0), then
				// draw a 2-pixel white border around that piece and draw green borders 
				// around each square that that piece can be moved to.

				paintSelectedSquare(backg);
				paintLegalMoves(backg);

			}
			if (mainPlainCheckers != null) {
				mainPlainCheckers.repaint();
			}
			getToolkit().sync();
			g.drawImage(backbuffer, 0, 0, this);
			try{
			if (mainPlainCheckers != null) {
//				mainPlainCheckers.paintAll(mainPlainCheckers.getGraphics());
			}
			getToolkit().sync();
			}catch(Exception e){
				mainPlainCheckers.mylog(e.getMessage());
			}
		}
	}


	public void paint(Graphics g) {
		update(g);

		
	}  // end paint()


	protected void paintSelectedSquare(Graphics g) {
		if (selectedRow >= 0) {
			g.setColor(Color.white);
			g.drawRect(3 + selectedCol*SCALE, 3 + selectedRow*SCALE, RECTSIZE-3, RECTSIZE-3);
			g.drawRect(4 + selectedCol*SCALE, 4 + selectedRow*SCALE, RECTSIZE-4, RECTSIZE-4);

			
			
		}
	}


	private void paintLegalMoves(Graphics g) {
		if (selectedRow >= 0) {
			g.setColor(Color.green.brighter());
			for (int i = 0; i < legalMoves.length; i++) {
				if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
					g.drawRect(3 + legalMoves[i].toCol*SCALE, 3 + legalMoves[i].toRow*SCALE, RECTSIZE-3, RECTSIZE-3);
			}
		}
	}


	protected void paintBoard(Graphics g) {
		int row_buffer = 0;
		int col_buffer = 0;
		Rectangle rec= getBounds();
		/* Draw a two-pixel black border around the edges of the canvas. */
		//g.setColor(Color.black);
//		g.fillRect((int)rec.getX()+2,getY()+2,getWidth()+2,getHeight()+2);
//		g.drawRect(0 + col_buffer,0 + row_buffer ,getSize().width-4,getSize().height-4);
//		g.drawRect(2 + col_buffer,2 + row_buffer,getSize().width-2,getSize().height-2);
//		g.drawRect(0 ,0  ,getSize().width-1,getSize().height-1);
//		g.drawRect(1 ,1,getSize().width-3,getSize().height-3);


		/* Draw the squares of the checkerboard and the checkers. */

		SCALE = (int)Math.ceil(getSize().width/8);
		SHIFTLEFT  = (int)Math.ceil(SCALE/4);
		for (int row = 0; row < 8; row++) { 
			for (int col = 0; col < 8; col++) {
				if ( row % 2 == col % 2 )
					g.setColor(Color.lightGray);
				else
					g.setColor(Color.gray);
				g.fillRect(2 + col*SCALE, 2 + row*SCALE, RECTSIZE, RECTSIZE);
				switch (board.pieceAt(row,col)) {
				case CheckersData.RED:
					g.setColor(Color.red);
					g.fillOval(SHIFTLEFT + col*SCALE, SHIFTLEFT + row*SCALE, OVALSIZE, OVALSIZE);
					break;
				case CheckersData.BLACK:
					g.setColor(Color.black);
					g.fillOval(SHIFTLEFT + col*SCALE, SHIFTLEFT + row*SCALE, OVALSIZE, OVALSIZE);
					break;
				case CheckersData.RED_KING:
					g.setColor(Color.red);
					g.fillOval(SHIFTLEFT + col*SCALE, SHIFTLEFT + row*SCALE, OVALSIZE, OVALSIZE);
					g.setColor(Color.white);
					g.drawString("K", 7 + col*SCALE, OVALSIZE + row*SCALE);
					break;
				case CheckersData.BLACK_KING:
					g.setColor(Color.black);
					g.fillOval(SHIFTLEFT + col*SCALE, SHIFTLEFT + row*SCALE, OVALSIZE, OVALSIZE);
					g.setColor(Color.white);
					g.drawString("K", 7 + col*SCALE, OVALSIZE + row*SCALE);
					break;
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	@Override
	public void repaint() {
		super.repaint();
	}


	/**
	 * @param g
	 */
	protected void highlightPiecesCanBeMoved(Graphics g) {
		// First, draw a cyan border around the pieces that can be moved.
		g.setColor(Color.blue);
		// 2 = the board width
		for (int i = 0; i < legalMoves.length; i++) {
			g.drawRect(2 + legalMoves[i].fromCol*SCALE, 2 + legalMoves[i].fromRow*SCALE, RECTSIZE, RECTSIZE);
//			(2 + col*SCALE, 2 + row*SCALE, RECTSIZE, RECTSIZE)
		}
	}


	public Dimension getPreferredSize() {
		// Specify desired size for this component.  Note:
		// the size MUST be 164 by 164.
		return new Dimension(DIMENSION, DIMENSION);
	}


	public Dimension getMinimumSize() {
		return new Dimension(DIMENSION, DIMENSION);
	}

	boolean threadSuspended = false;
	private boolean isOwnerBlack;
	private boolean isOwnerRed;
	protected final static String initRedId = "red@Boty";
	protected static final String initBlackId = "black@Boty";
	protected String redPlayerWaveId = initRedId;
	protected String blackPlayerWaveId = initBlackId;
	private String clickerId = "";
	protected boolean isTransmitClick;
	
	public void mousePressed(MouseEvent evt) {
		pos = getSquareByCoord(evt.getX(), evt.getY());
		mainPlainCheckers.mylog(" entering mousePressed: " + pos[0] + "," + pos[1]);
		evt.consume();
		clickerId = mainPlainCheckers.ownerId;
		mousePressedInternal(pos,true);
		// Respond to a user click on the board.  If no game is
		// in progress, show an error message.  Otherwise, find
		// the row and column that the user clicked and call
//		// doClickSquare() to handle it.
//		if (gameInProgress == false)
//			message.setText("Click \"New Game\" to start a new game.");
//		else {
//			if(threadSuspended){
//				threadSuspended = false;
//				playThread.resume();
//			}
//
//		}
		mainPlainCheckers.mylog(" exiting mousePressed: ");
	}
	
	public void mousePressedInternal(int[] posIn, boolean isTransmitClick) {
		this.isTransmitClick = isTransmitClick;
		mainPlainCheckers.mylog("entering mousePressedInternal, isTransmit:  " + isTransmitClick);
		pos = posIn;
		// Respond to a user click on the board.  If no game is
		// in progress, show an error message.  Otherwise, find
		// the row and column that the user clicked and call
		// doClickSquare() to handle it.
		if (gameInProgress == false){
			message.setText("Click \"New Game\" to start a new game.");
			return;
		}
		else {
			if(threadSuspended){
				threadSuspended = false;
				try{
					playThread.resume();
				}catch(Exception e){
					mainPlainCheckers.mylog(e.getMessage());
					try{
						playThread.start();
					}catch(Exception e1){
						mainPlainCheckers.mylog(e1.getMessage());
					}
				}
				
			}
			try{
				playThread.resume();
			}catch(Exception e){
				mainPlainCheckers.mylog(e.getMessage());
			}
			
		}
		mainPlainCheckers.mylog("exiting mousePressedInternal: ");
//		mainPlainCheckers.mylog("playThread.isAlive: " + playThread.isAlive());
	}


	protected int[] getSquareByCoord(int x, int y) {
		for (int row = 0; row < 8; row++) { 
			for (int col = 0; col < 8; col++) {
				if( 2 + col*SCALE < x && x < 2 + col*SCALE + RECTSIZE &&  2 + row*SCALE < y && y < 2 + row*SCALE + RECTSIZE){
					int[] ar = new int[2];
					ar[0]=row;
					ar[1]=col;
					return ar;
				}
			}
		}
		return null;
	}


	public void mouseReleased(MouseEvent evt) { }
	public void mouseClicked(MouseEvent evt) { }
	public void mouseEntered(MouseEvent evt) { }
	public void mouseExited(MouseEvent evt) { }


	/**
	 * @return the board
	 */
	protected CheckersData getBoard() {
		return board;
	}


	/**
	 * @param board the board to set
	 */
	protected void setBoard(CheckersData board) {
		this.board = board;
	}


	/**
	 * @return the currentPlayer
	 */
	protected int getCurrentPlayer() {
		return currentPlayer;
	}
	


	/**
	 * @param currentPlayer the currentPlayer to set
	 */
	protected void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}


	/**
	 * @return the gameInProgress
	 */
	protected boolean isGameInProgress() {
		return gameInProgress;
	}


	/**
	 * @param gameInProgress the gameInProgress to set
	 */
	protected void setGameInProgress(boolean gameInProgress) {
		this.gameInProgress = gameInProgress;
	}


	/**
	 * @return the legalMoves
	 */
	protected CheckersMove[] getLegalMoves() {
		return legalMoves;
	}


	/**
	 * @param legalMoves the legalMoves to set
	 */
	protected void setLegalMoves(CheckersMove[] legalMoves) {
		this.legalMoves = legalMoves;
	}


	/**
	 * @return the message
	 */
	protected JTextArea getMessage() {
		return message;
	}


	/**
	 * @param message the message to set
	 */
	protected void setMessage(JTextArea message) {
		this.message = message;
	}


	/**
	 * @return the newGameButton
	 */
	protected Button getNewGameButton() {
		return newGameButton;
	}


	/**
	 * @param newGameButton the newGameButton to set
	 */
	protected void setNewGameButton(Button newGameButton) {
		this.newGameButton = newGameButton;
	}


	/**
	 * @return the resignButton
	 */
	protected Button getResignButton() {
		return resignButton;
	}


	/**
	 * @param resignButton the resignButton to set
	 */
	protected void setResignButton(Button resignButton) {
		this.resignButton = resignButton;
	}


	/**
	 * @return the selectedCol
	 */
	protected int getSelectedCol() {
		return selectedCol;
	}


	/**
	 * @param selectedCol the selectedCol to set
	 */
	protected void setSelectedCol(int selectedCol) {
		this.selectedCol = selectedCol;
	}


	/**
	 * @return the selectedRow
	 */
	protected int getSelectedRow() {
		return selectedRow;
	}


	/**
	 * @param selectedRow the selectedRow to set
	 */
	protected void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}


	public void setApplet(MainPlainCheckers mainPlainCheckers) {
		this.mainPlainCheckers = mainPlainCheckers;
	}


	/**
	 * @return the isAutoRed
	 */
	public boolean isAutoRed() {
		return isAutoRed;
	}


	/**
	 * @param isAutoRed the isAutoRed to set
	 */
	public void setAutoRed(boolean isAutoRed) {
		this.isAutoRed = isAutoRed;
	}


	/**
	 * @return the isAutoBlack
	 */
	public boolean isAutoBlack() {
		return isAutoBlack;
	}


	/**
	 * @param isAutoBlack the isAutoBlack to set
	 */
	public void setAutoBlack(boolean isAutoBlack) {
		this.isAutoBlack = isAutoBlack;
	}


	protected void initAutomatePlayers() {
	}
	
	protected Thread createPlayerThread(){
		return new CanvasThread();
	}
	
	public class CanvasThread extends Thread {
		public void run() {
	    	 mainPlainCheckers.mylog("entering " + this.getClass().getName() + "");
	    	while(gameInProgress){
	    		paint(getGraphics());
	    		mainPlainCheckers.mylog("player thread in while : looping");
	    		boolean isOwnerClick = false;
	    		if(getCurrentPlayer() == CheckersData.RED && (isOwnerRed || clickerId.equals(redPlayerWaveId))){
	    			isOwnerClick = true;
	    			clickerId = "";
	    		}else if(getCurrentPlayer() == CheckersData.BLACK && (isOwnerBlack || clickerId.equals(blackPlayerWaveId))){
	    			isOwnerClick = true;
	    			clickerId = "";
	    		}
	    		 if(isAutoPlayer(getCurrentPlayer()) && isOwnerClick){
	    			 mainPlainCheckers.mylog("playing auto: " + toPlayerSideId(getCurrentPlayer()));
	    			 try {
						playThread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	 	 			doAutoPlay(getCurrentPlayer());
	 	 			switchNextPlayer(getCurrentPlayer());
	 	 			if(!isAutoPlayer(getCurrentPlayer())){
	 	 				threadSuspended = true;
	 	 				playThread.suspend();
	 	 			}
	 	 		}else{
	 	 			if(!isOwnerClick){
	 	 				print("isOwnerClick: " + isOwnerClick + ", redPlayerWaveId: " + redPlayerWaveId + ", blackPlayerWaveId: " + blackPlayerWaveId + ", clickerId: " + clickerId + ", ownerId: " + mainPlainCheckers.ownerId + ", isOwnerRed: " + isOwnerRed + ", isOwnerBlack: " + isOwnerBlack + ", cplayer: " + toPlayerSideName(getCurrentPlayer()) );
	 	 			}
 	 				if(pos != null && isOwnerClick){    		  
 	 					if(doClickSquare(pos[0],pos[1])){
 	 						paint(getGraphics());
 	 						switchNextPlayer(getCurrentPlayer());
 	 						paint(getGraphics());
 	 					}
 	 					paint(getGraphics());
 	 					pos = null;
 	 					continue;
 	 				}else{
 	 					threadSuspended = true;
 	 	 				playThread.suspend();
 	 				}
 	 				
 	 				
	 	 		}
	    	}
	    	 mainPlainCheckers.mylog("exiting " + this.getClass().getName() + "");
	     }
	     public void doAutoPlay(int currPLayer){
	    	 CheckersCanvas.this.doAutoPlay(currPLayer);
	     }
	     
	 };
	 
	 protected String[] msgBlackWins(){
			return new String[] {getRedPLayerName() + " has no moves. " + getBlackPLayerName() + "wins."};
	 }
	 
	 public String toPlayerSideName(int currentPlayer2) {
		 if(getCurrentPlayer() == CheckersData.RED){
			 return MainPlainCheckers.SIDE_RED;
 		}else if(getCurrentPlayer() == CheckersData.BLACK){
 			return MainPlainCheckers.SIDE_BLACK;
 		}
		return null;
	}
	 
	 public String toPlayerSideId(int currentPlayer2) {
		 if(getCurrentPlayer() == CheckersData.RED){
			 return redPlayerWaveId;
 		}else if(getCurrentPlayer() == CheckersData.BLACK){
 			return blackPlayerWaveId;
 		}
		return null;
	}
	 
	 public int id2CurrentPlayer(String id){
		 if(id.equals(redPlayerWaveId))
			 return CheckersData.RED;
		 else
			 return CheckersData.BLACK;
	 }


	protected String[] msgRedWins(){
		 return new String[] {getBlackPLayerName() + " has no moves. " + getRedPLayerName() + "wins."};
	 }


	public boolean isOwnerRed() {
		return isOwnerRed;
	}
	
	public boolean isOwnerBlack() {
		return isOwnerBlack;
	}


	public void setOwnerBlack(boolean isOwner) {
		this.isOwnerBlack = isOwner;
		
	}
	public void setOwnerRed(boolean isOwner) {
		this.isOwnerRed = isOwner;
		
	}


	public void setRedPLayerWaveId(String playerWaveId) {
		this.redPlayerWaveId = playerWaveId;
	}


	public void setBlackPLayerWaveId(String playerWaveId) {
		this.blackPlayerWaveId = playerWaveId;
	}
	public void print(String str){

		if(mainPlainCheckers != null){
			mainPlainCheckers.mylog(str);
		}else
			mainPlainCheckers.mylog(str);
	}


	public void setClickerId(String fromSourceId) {
		this.clickerId = fromSourceId;
	}
		
} 












