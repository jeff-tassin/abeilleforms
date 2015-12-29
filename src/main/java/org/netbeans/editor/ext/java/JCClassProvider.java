/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor.ext.java;

import java.util.Iterator;

/**
 * Java completion class provider
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public interface JCClassProvider {

	public Iterator getClasses();

	/**
	 * @return true when append was successful or false when it failed or was
	 *         broken by added classprovider.
	 */
	public boolean append(JCClassProvider cp);

	public void reset();

	/**
	 * This method is executed by the target Class Provider to notify this
	 * provider about the class appending.
	 * 
	 * @param c
	 *            JC class that was appended
	 * @return true to continue building, false to stop build
	 */
	public boolean notifyAppend(JCClass c, boolean appendFinished);

}
