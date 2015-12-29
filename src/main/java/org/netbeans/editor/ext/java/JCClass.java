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
 * Java completion class
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public interface JCClass extends Comparable {

	/** Get name of the class without package specification */
	public String getName();

	/** Get package where the class is placed */
	public String getPackageName();

	/** Get full name consisting of class name and package name */
	public String getFullName();

	/** Get offset in source files */
	public int getTagOffset();

	/** Is this class an interface? */
	public boolean isInterface();

	/** Get modifiers for this class */
	public int getModifiers();

	/** Get superclass of this class */
	public JCClass getSuperclass();

	/**
	 * Get interfaces this class implements or the interfaces this interface
	 * extends.
	 */
	public JCClass[] getInterfaces();

	/** Get fields that this class contains */
	public JCField[] getFields();

	/** Get constructors that this class contains */
	public JCConstructor[] getConstructors();

	/** Get methods that this class contains */
	public JCMethod[] getMethods();

}
