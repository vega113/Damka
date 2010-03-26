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
package mystuff.checkers.base;

import mystuff.checkers.interfaces.IBaseBean;

public abstract class AbstractBean implements IBaseBean {
	/* (non-Javadoc)
	 * @see mystuff.checkers.base.IBaseBean#validateInput()
	 */
	protected void validateInput(){};
	/* (non-Javadoc)
	 * @see mystuff.checkers.base.IBaseBean#validateOutput()
	 */
	protected void validateOutput(){};
	/* (non-Javadoc)
	 * @see mystuff.checkers.base.IBaseBean#preExecute()
	 */
	protected void preExecute(){};
	/* (non-Javadoc)
	 * @see mystuff.checkers.base.IBaseBean#execute()
	 */
	final public void execute(){
		validateInput();
		preExecute();
		executeImp();
		postExecute();
		validateOutput();
	};
	/* (non-Javadoc)
	 * @see mystuff.checkers.base.IBaseBean#executeImp()
	 */
	protected void executeImp(){}
	
	protected void postExecute() {
		// TODO Auto-generated method stub
		
	};
}
