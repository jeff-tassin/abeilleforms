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

package org.netbeans.editor.ext.html;

import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

/**
 * HTML token-context defines token-ids and token-categories used in HTML
 * language.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class HTMLTokenContext extends TokenContext {

	// Token categories

	// Numeric-ids for token-ids
	public static final int TEXT_ID = 1;
	public static final int WS_ID = 2;
	public static final int ERROR_ID = 3;
	public static final int TAG_ID = 4;
	public static final int ARGUMENT_ID = 5;
	public static final int OPERATOR_ID = 6;
	public static final int VALUE_ID = 7;
	public static final int BLOCK_COMMENT_ID = 8;
	public static final int SGML_COMMENT_ID = 9;
	public static final int DECLARATION_ID = 10;
	public static final int CHARACTER_ID = 11;

	public static final int EOL_ID = 12;

	// Token-ids
	/** Plain text */
	public static final BaseTokenID TEXT = new BaseTokenID("text", TEXT_ID);
	/** Erroneous Text */
	public static final BaseTokenID WS = new BaseTokenID("ws", WS_ID);
	/** Plain Text */
	public static final BaseTokenID ERROR = new BaseTokenID("error", ERROR_ID);
	/** Html Tag */
	public static final BaseTokenID TAG = new BaseTokenID("tag", TAG_ID);
	/** Argument of a tag */
	public static final BaseTokenID ARGUMENT = new BaseTokenID("argument", ARGUMENT_ID);
	/** Operators - '=' between arg and value */
	public static final BaseTokenID OPERATOR = new BaseTokenID("operator", OPERATOR_ID);
	/** Value - value of an argument */
	public static final BaseTokenID VALUE = new BaseTokenID("value", VALUE_ID);
	/** Block comment */
	public static final BaseTokenID BLOCK_COMMENT = new BaseTokenID("block-comment", BLOCK_COMMENT_ID);
	/** SGML comment - e.g. in DOCTYPE */
	public static final BaseTokenID SGML_COMMENT = new BaseTokenID("sgml-comment", SGML_COMMENT_ID);
	/** SGML declaration in HTML document - e.g. <!DOCTYPE> */
	public static final BaseTokenID DECLARATION = new BaseTokenID("sgml-declaration", DECLARATION_ID);
	/** Character reference, e.g. &amp;lt; = &lt; */
	public static final BaseTokenID CHARACTER = new BaseTokenID("character", CHARACTER_ID);

	/** End of line */
	public static final BaseTokenID EOL = new BaseTokenID("EOL", EOL_ID);

	// Context instance declaration
	public static final HTMLTokenContext context = new HTMLTokenContext();

	public static final TokenContextPath contextPath = context.getContextPath();

	private HTMLTokenContext() {
		super("html-");

		try {
			addDeclaredTokenIDs();
		} catch (Exception e) {
			if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
				e.printStackTrace();
			}
		}

	}

}
