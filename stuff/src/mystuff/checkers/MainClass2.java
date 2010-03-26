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
package mystuff.checkers;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import mystuff.checkers.MainClass.GameLoop;

/**
 * @author Vega
 *
 */
public class MainClass2 extends MainClass{

	/**
	 * @param args
	 */
	public static void main(String[] args) {

    	Handler handler = null;;
		try {
			handler = new FileHandler("OutFile.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.global.addHandler(handler);
		
		try {
			handler = new FileHandler("OutFileFiner.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Handler[] parentHandlers = Logger.getLogger("Finer").getParent().getHandlers();
		Logger.getLogger("Finer").getParent().removeHandler(parentHandlers[0]);
		Logger.getLogger("Finer").addHandler(handler);
		Logger.global.addHandler(parentHandlers[0]);

       
        threadMessage("Starting GameLoop thread");
        long startTime = System.currentTimeMillis();
        GameLoop gameLoop = new GameLoop();
        (new Thread(gameLoop)).start();

        threadMessage("Waiting for GameLoop thread to finish");
        //loop until GameLoop thread exits
        threadMessage("Finally!");
       
	}

}
