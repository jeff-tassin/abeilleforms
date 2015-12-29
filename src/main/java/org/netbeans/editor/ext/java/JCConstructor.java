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

/**
 * Java completion class constructor
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

/** Description of the declared constructors */
public interface JCConstructor extends Comparable {

	/** Get reference to class where this method resides */
	public JCClass getClazz();

	/** Get offset in source files */
	public int getTagOffset();

	/** Method modifiers */
	public int getModifiers();

	/** Parameters of the method */
	public JCParameter[] getParameters();

	/** Exceptions thrown by this method */
	public JCClass[] getExceptions();

}
