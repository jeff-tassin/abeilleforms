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

import java.util.List;

/**
 * Java completion finder
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public interface JCFinder extends JCClassProvider {

	/** Get the package from the package name */
	public JCPackage getExactPackage(String packageName);

	/** Get the class from full name of the class */
	public JCClass getExactClass(String classFullName);

	/**
	 * Get the list of packages that start with the given name
	 * 
	 * @param name
	 *            the start of the requested package(s) name
	 * @return list of the matching packages
	 */
	public List findPackages(String name, boolean exactMatch, boolean subPackages);

	/**
	 * Find classes by name and possibly in some package
	 * 
	 * @param pkg
	 *            package where the classes should be searched for. It can be
	 *            null
	 * @param begining
	 *            of the name of the class. The package name must be omitted.
	 * @param exactMatch
	 *            whether the given name is the exact requested name of the
	 *            class or not.
	 * @return list of the matching classes
	 */
	public List findClasses(JCPackage pkg, String name, boolean exactMatch);

	/**
	 * Find fields by name in a given class.
	 * 
	 * @param c
	 *            class which is searched for the fields.
	 * @param name
	 *            start of the name of the field
	 * @param exactMatch
	 *            whether the given name of the field is exact
	 * @param staticOnly
	 *            whether search for the static fields only
	 * @param inspectOuterClasses
	 *            if the given class is inner class of some outer class, whether
	 *            the fields of the outer class should be possibly added or not.
	 *            This should be false when searching for 'this.'
	 * @return list of the matching fields
	 */
	public List findFields(JCClass c, String name, boolean exactMatch, boolean staticOnly, boolean inspectOuterClasses);

	/**
	 * Find methods by name in a given class.
	 * 
	 * @param c
	 *            class which is searched for the methods.
	 * @param name
	 *            start of the name of the method
	 * @param exactMatch
	 *            whether the given name of the method is exact
	 * @param staticOnly
	 *            whether search for the static methods only
	 * @param inspectOuterClasses
	 *            if the given class is inner class of some outer class, whether
	 *            the methods of the outer class should be possibly added or
	 *            not. This should be false when searching for 'this.'
	 * @return list of the matching methods
	 */
	public List findMethods(JCClass c, String name, boolean exactMatch, boolean staticOnly, boolean inspectOuterClasses);

}
