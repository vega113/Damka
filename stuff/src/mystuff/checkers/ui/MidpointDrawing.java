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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
 
public class MidpointDrawing {
  static int PAD = 10;
 
  public static void main(String[] args) {
    final Panel panel = new Panel() {
      public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(Color.black);
        int width = getWidth();
        int height = getHeight();
        int side = (int)(Math.min(width, height) - 2*PAD)/16;
        int x0 = PAD + (width - 2*PAD - 16*side)/2;
        int y0 = PAD + (height - 2*PAD - 16*side)/2;
        int midWidth = width/2;
        int midHeight = height/2;
  
        // draw grid
        g2.draw(new Rectangle2D.Double(x0, y0, 16*side, 16*side));
        int
          x1 = x0,
          y1 = y0,
          x2 = x0 + 16*side,
          y2 = y0 + 16*side;
        for(int i = 0; i < 16; i++) {
          g2.draw(new Line2D.Double(x1, y1, x2, y1));
          y1 += side;
        }
        x1 = x0;
        y1 = y0;
        for(int i = 0; i < 16; i++) {
          g2.draw(new Line2D.Double(x1, y1, x1, y2));
          x1 += side;
        }
  
        // draw midpoint lines
        g2.setPaint(Color.red);
        g2.draw(new Line2D.Double(x0, y0 + 8*side, x0 + 16*side, y0 + 8*side));
        g2.draw(new Line2D.Double(x0 + 8*side, y0, x0 + 8*side, y0 + 16*side));
  
        // draw red squares
        // squares touching horizontal midpoint line
        g2.draw(new Line2D.Double(x0, y0 + 7*side, x0 + 16*side, y0 + 7*side));
        g2.draw(new Line2D.Double(x0, y0 + 9*side, x0 + 16*side, y0 + 9*side));
        x1 = x0;
        y1 = y0 + 7*side;
        y2 = y0 + 9*side;
        for(int i = 0; i < 16; i++) {
          g2.draw(new Line2D.Double(x1, y1, x1, y2 ));
          x1 += side;
        }
        // squares touching vertical midpoint line
        g2.draw(new Line2D.Double(x0 + 7*side, y0, x0 + 7*side, y0 + 16*side));
        g2.draw(new Line2D.Double(x0 + 9*side, y0, x0 + 9*side, y0 + 16*side));
        x1 = x0 + 7*side;
        y1 = y0;
        x2 = x0 + 9*side;
        for(int i = 0; i < 16; i++) {
          g2.draw(new Line2D.Double(x1, y1, x2, y1));
          y1 += side;
        }
      }
    };
    Frame f = new Frame();
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    f.add(panel);
    f.setSize(400,300);
    f.setLocation(300,300);
    f.setVisible(true);
  }
}


