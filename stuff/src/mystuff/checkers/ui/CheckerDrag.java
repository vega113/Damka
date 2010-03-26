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

// CheckerDrag.java


import java.awt.*;
import java.awt.event.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class CheckerDrag extends JPanel
{
   // Dimension of checkerboard square.


   final static int SQUAREDIM = 40;


   // Dimension of checkerboard -- includes black outline.


   final static int BOARDDIM = 8 * SQUAREDIM + 2;


   // Dimension of checker -- 3/4 the dimension of a square.


   final static int CHECKERDIM = 3 * SQUAREDIM / 4;


   // Square colors are dark green or white.


   final static Color darkGreen = new Color (0, 128, 0);


   // Dragging flag -- set to true when user presses mouse button over checker
   // and cleared to false when user releases mouse button.


   boolean inDrag = false;


   // Left coordinate of checkerboard's upper-left corner.


   int boardx;


   // Top coordinate of checkerboard's upper-left corner.


   int boardy;


   // Left coordinate of checker rectangle origin (upper-left corner).


   int ox;


   // Top coordinate of checker rectangle origin (upper-left corner).


   int oy;


   // Left displacement between mouse coordinates at time of press and checker
   // rectangle origin.


   int relx;


   // Top displacement between mouse coordinates at time of press and checker
   // rectangle origin.


   int rely;


   // Width of applet drawing area.


   int width;


   // Height of applet drawing area.


   int height;


   // Image buffer.


   Image imBuffer;


   // Graphics context associated with image buffer.


   Graphics imG;


   public void init ()
   {
      // Obtain the size of the applet's drawing area.


      width = getSize ().width;
      height = getSize ().height;


      // Create image buffer.


      imBuffer = createImage (width, height);

     if(imBuffer == null){
    	 imBuffer = createVolatileImage(width, height);
     }
      // Retrieve graphics context associated with image buffer.


//      imG = imBuffer.getGraphics ();
     imG = getGraphics();


      // Initialize checkerboard's origin, so that board is centered.


      boardx = (width - BOARDDIM) / 2 + 1;
      boardy = (height - BOARDDIM) / 2 + 1;


      // Initialize checker's rectangle's starting origin so that checker is
      // centered in the square located in the top row and second column from
      // the left.


      ox = boardx + SQUAREDIM + (SQUAREDIM - CHECKERDIM) / 2 + 1;
      oy = boardy + (SQUAREDIM - CHECKERDIM) / 2 + 1;


      // Attach a mouse listener to the applet. That listener listens for
      // mouse-button press and mouse-button release events.


      addMouseListener (new MouseAdapter ()
                        {
                            public void mousePressed (MouseEvent e)
                            {
                               // Obtain mouse coordinates at time of press.


                               int x = e.getX ();
                               int y = e.getY ();


                               // If mouse is over draggable checker at time
                               // of press (i.e., contains (x, y) returns
                               // true), save distance between current mouse
                               // coordinates and draggable checker origin
                               // (which will always be positive) and set drag
                               // flag to true (to indicate drag in progress).


                               if (contains (x, y))
                               {
                                   relx = x - ox;
                                   rely = y - oy;
                                   inDrag = true;
                               }
                            }
 

                            boolean contains (int x, int y)
                            {
                               // Calculate center of draggable checker.


                               int cox = ox + CHECKERDIM / 2;
                               int coy = oy + CHECKERDIM / 2;


                               // Return true if (x, y) locates with bounds
                               // of draggable checker. CHECKERDIM / 2 is the
                               // radius.


                               return (cox - x) * (cox - x) +
                                      (coy - y) * (coy - y) <
                                      CHECKERDIM / 2 * CHECKERDIM / 2;
                            }


                            public void mouseReleased (MouseEvent e)
                            {
                               // When mouse is released, clear inDrag (to
                               // indicate no drag in progress) if inDrag is
                               // already set.


                               if (inDrag)
                                   inDrag = false;
                            }
                        });


      // Attach a mouse motion listener to the applet. That listener listens
      // for mouse drag events.


      addMouseMotionListener (new MouseMotionAdapter ()
                              {
                                  public void mouseDragged (MouseEvent e)
                                  {
                                     if (inDrag)
                                     {
                                         // Calculate draggable checker's new
                                         // origin (the upper-left corner of
                                         // the checker rectangle).


                                         int tmpox = e.getX () - relx;
                                         int tmpoy = e.getY () - rely;


                                         // If the checker is not being moved
                                         // (at least partly) off board, 
                                         // assign the previously calculated
                                         // origin (tmpox, tmpoy) as the
                                         // permanent origin (ox, oy), and
                                         // redraw the display area (with the
                                         // draggable checker at the new
                                         // coordinates).


                                         if (tmpox > boardx &&
                                             tmpoy > boardy &&
                                             tmpox + CHECKERDIM
                                             < boardx + BOARDDIM &&
                                             tmpoy + CHECKERDIM
                                             < boardy + BOARDDIM)
                                         {
                                             ox = tmpox;
                                             oy = tmpoy;
                                             repaint ();
                                         }
                                     }
                                  }
                              });
   }


   public void paint (Graphics g)
   {
      // Paint the checkerboard over which the checker will be dragged.


      paintCheckerBoard (imG, boardx, boardy);


      // Paint the checker that will be dragged.


      paintChecker (imG, ox, oy);


      // Draw contents of image buffer.


      g.drawImage (imBuffer, 0, 0, this);
   }


   void paintChecker (Graphics g, int x, int y)
   {
      // Set checker shadow color.


      g.setColor (Color.black);


      // Paint checker shadow.


      g.fillOval (x, y, CHECKERDIM, CHECKERDIM);


      // Set checker color.


      g.setColor (Color.red);


      // Paint checker.


      g.fillOval (x, y, CHECKERDIM - CHECKERDIM / 13, CHECKERDIM - CHECKERDIM / 13);
   }


   void paintCheckerBoard (Graphics g, int x, int y)
   {
      // Paint checkerboard outline.


      g.setColor (Color.black);
      g.drawRect (x, y, 8 * SQUAREDIM + 1, 8 * SQUAREDIM + 1);


      // Paint checkerboard.


      for (int row = 0; row < 8; row++)
      {
           g.setColor (((row & 1) != 0) ? darkGreen : Color.white);


           for (int col = 0; col < 8; col++)
           {
                g.fillRect (x + 1 + col * SQUAREDIM, y + 1 + row * SQUAREDIM,
                            SQUAREDIM, SQUAREDIM);


                g.setColor ((g.getColor () == darkGreen) ? Color.white :
                            darkGreen);
           }
      }
   }


   // The AWT invokes the update() method in response to the repaint() method
   // calls that are made as a checker is dragged. The default implementation
   // of this method, which is inherited from the Container class, clears the
   // applet's drawing area to the background color prior to calling paint().
   // This clearing followed by drawing causes flicker. CheckerDrag overrides
   // update() to prevent the background from being cleared, which eliminates
   // the flicker.


   public void update (Graphics g)
   {
      paint (g);
   }
   public static void main(String[] args) {
	   CheckerDrag checkerDrag = new CheckerDrag();
//	   checkerDrag.setSize(150,150);
	   checkerDrag.init();
	   JFrame frame = new JFrame("Drag");
	   frame.setSize(300, 300);
	   frame.getContentPane().setLayout(new FlowLayout());
	   frame.getContentPane().add(checkerDrag);
	   frame.pack();
	   frame.setVisible(true);
   }
} 

