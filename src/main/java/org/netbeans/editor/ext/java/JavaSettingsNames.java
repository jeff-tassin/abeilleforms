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

import org.netbeans.editor.ext.ExtSettingsNames;

/**
 * Names of the java editor settings.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JavaSettingsNames extends ExtSettingsNames {

	/**
	 * Whether insert extra space before the parenthesis or not. Values:
	 * java.lang.Boolean instances Effect: function(a) becomes (when set to
	 * true) function (a)
	 */
	public static final String JAVA_FORMAT_SPACE_BEFORE_PARENTHESIS = "java-format-space-before-parenthesis"; // NOI18N

	/**
	 * Whether insert space after the comma inside the parameter list. Values:
	 * java.lang.Boolean instances Effect: function(a,b) becomes (when set to
	 * true) function(a, b)
	 * 
	 */
	public static final String JAVA_FORMAT_SPACE_AFTER_COMMA = "java-format-space-after-comma"; // NOI18N

	/**
	 * Whether insert extra new-line before the compound bracket or not. Values:
	 * java.lang.Boolean instances Effect: if (test) { function(); } becomes
	 * (when set to true) if (test) { function(); }
	 */
	public static final String JAVA_FORMAT_NEWLINE_BEFORE_BRACE = "java-format-newline-before-brace"; // NOI18N

	/**
	 * Add one more space to the begining of each line in the multi-line comment
	 * if it's not already there. Values: java.lang.Boolean Effect: For example
	 * in java:
	 *  /* this is * multiline comment *\/
	 * 
	 * becomes (when set to true)
	 *  /* this is * multiline comment *\/
	 */
	public static final String JAVA_FORMAT_LEADING_SPACE_IN_COMMENT = "java-format-leading-space-in-comment"; // NOI18N

	/**
	 * Whether the '*' should be added at the new line in comment.
	 */
	public static final String JAVA_FORMAT_LEADING_STAR_IN_COMMENT = "java-format-leading-star-in-comment"; // NOI18N

}
