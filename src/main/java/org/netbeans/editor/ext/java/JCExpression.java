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

import java.util.ArrayList;

import org.netbeans.editor.EditorDebug;
import org.netbeans.editor.TokenID;

/**
 * Expression generated by parsing text by java completion
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JCExpression {

	/** Invalid expression - this ID is used only internally */
	private static final int INVALID = -1;
	/** Constant - int/long/String/char etc. */
	public static final int CONSTANT = 0;
	/** Variable 'a' or 'a.b.c' */
	public static final int VARIABLE = 1;
	/** Operator '+' or '--' */
	public static final int OPERATOR = 2;
	/** Special value for unary operators */
	public static final int UNARY_OPERATOR = 3;
	/** Dot between method calls 'a().b()' or 'a().b.c.d(e, f)' */
	public static final int DOT = 4;
	/**
	 * Dot between method calls and dot at the end 'a().b().' or 'a().b.c.d(e,
	 * f).'
	 */
	public static final int DOT_OPEN = 5;
	/** Opened array 'a[0' or 'a.b.c[d.e' */
	public static final int ARRAY_OPEN = 6;
	/** Array 'a[0]' or 'a.b.c[d.e]' */
	public static final int ARRAY = 7;
	/** Left opened parentheses */
	public static final int PARENTHESIS_OPEN = 8;
	/** Closed parenthesis holding the subexpression or conversion */
	public static final int PARENTHESIS = 9;
	/** Opened method 'a(' or 'a.b.c(d, e' */
	public static final int METHOD_OPEN = 10;
	/** Method closed by right parentheses 'a()' or 'a.b.c(d, e, f)' */
	public static final int METHOD = 11;
	/**
	 * Constructor closed by right parentheses 'new String()' or 'new
	 * String("hello")'
	 */
	// NOI18N
	public static final int CONSTRUCTOR = 12;
	/** Conversion '(int)a.b()' */
	public static final int CONVERSION = 13;
	/** Data type */
	public static final int TYPE = 14;
	/** 'new' keyword */
	public static final int NEW = 15;
	/** 'instanceof' operator */
	public static final int INSTANCEOF = 16;

	private static final int javaTokenIDsLength = JavaTokenContext.context.getTokenIDs().length;

	/**
	 * Array that holds the precedence of the operator and whether it's right
	 * associative or not.
	 */
	private static final int[] OP = new int[javaTokenIDsLength + INSTANCEOF + 1];

	/** Is the operator right associative? */
	private static final int RIGHT_ASSOCIATIVE = 32;

	static {
		OP[JavaTokenContext.EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.LT_ID] = 10;
		OP[JavaTokenContext.GT_ID] = 10;
		OP[JavaTokenContext.LSHIFT_ID] = 11;
		OP[JavaTokenContext.RSSHIFT_ID] = 11;
		OP[JavaTokenContext.RUSHIFT_ID] = 11;
		OP[JavaTokenContext.PLUS_ID] = 12;
		OP[JavaTokenContext.MINUS_ID] = 12;
		OP[JavaTokenContext.MUL_ID] = 13;
		OP[JavaTokenContext.DIV_ID] = 13;
		OP[JavaTokenContext.AND_ID] = 8;
		OP[JavaTokenContext.OR_ID] = 6;
		OP[JavaTokenContext.XOR_ID] = 7;
		OP[JavaTokenContext.MOD_ID] = 13;
		OP[JavaTokenContext.NOT_ID] = 14;
		OP[JavaTokenContext.NEG_ID] = 14;

		OP[JavaTokenContext.EQ_EQ_ID] = 9;
		OP[JavaTokenContext.LT_EQ_ID] = 10;
		OP[JavaTokenContext.GT_EQ_ID] = 10;
		OP[JavaTokenContext.LSHIFT_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.RSSHIFT_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.RUSHIFT_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.PLUS_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.MINUS_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.MUL_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.DIV_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.AND_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.OR_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.XOR_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.MOD_EQ_ID] = 2 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.NOT_EQ_ID] = 9;

		OP[JavaTokenContext.DOT_ID] = 15;
		OP[JavaTokenContext.COLON_ID] = 3 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.QUESTION_ID] = 3 | RIGHT_ASSOCIATIVE;
		OP[JavaTokenContext.LBRACKET_ID] = 15;
		OP[JavaTokenContext.RBRACKET_ID] = 0; // stop
		OP[JavaTokenContext.PLUS_PLUS_ID] = 15;
		OP[JavaTokenContext.MINUS_MINUS_ID] = 15;
		OP[JavaTokenContext.AND_AND_ID] = 5;
		OP[JavaTokenContext.OR_OR_ID] = 4;

		OP[JavaTokenContext.COMMA_ID] = 0; // stop
		OP[JavaTokenContext.SEMICOLON_ID] = 0; // not-recognized
		OP[JavaTokenContext.LPAREN_ID] = 16;
		OP[JavaTokenContext.RPAREN_ID] = 0; // not-recognized
		OP[JavaTokenContext.LBRACE_ID] = 0; // not-recognized
		OP[JavaTokenContext.RBRACE_ID] = 0; // not-recognized

		OP[javaTokenIDsLength + INVALID] = 0;
		OP[javaTokenIDsLength + CONSTANT] = 1;
		OP[javaTokenIDsLength + VARIABLE] = 1;
		OP[javaTokenIDsLength + UNARY_OPERATOR] = 15;
		OP[javaTokenIDsLength + DOT] = 1;
		OP[javaTokenIDsLength + DOT_OPEN] = 0; // stop
		OP[javaTokenIDsLength + ARRAY_OPEN] = 0; // stop
		OP[javaTokenIDsLength + ARRAY] = 1;
		OP[javaTokenIDsLength + PARENTHESIS_OPEN] = 0; // stop
		OP[javaTokenIDsLength + PARENTHESIS] = 1;
		OP[javaTokenIDsLength + METHOD_OPEN] = 0; // stop
		OP[javaTokenIDsLength + METHOD] = 1;
		OP[javaTokenIDsLength + CONSTRUCTOR] = 1;
		OP[javaTokenIDsLength + CONVERSION] = 1;
		OP[javaTokenIDsLength + TYPE] = 0; // stop
		OP[javaTokenIDsLength + NEW] = 0; // stop
		OP[javaTokenIDsLength + INSTANCEOF] = 10;

	}

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	private JCExpression parent;

	/** ID of the expression */
	private int expID;

	/** Result type */
	private JCType type;

	/** Current token count * 3 */
	private int tokenCountM3;

	/**
	 * token info blocks containing tokenID token's text and the position of the
	 * token in the document
	 */
	private Object[] tokenBlocks = EMPTY_OBJECT_ARRAY;

	/** List of parameters */
	private ArrayList prmList;

	JCExpression(int expID) {
		this.expID = expID;
	}

	/** Create empty variable. */
	static JCExpression createEmptyVariable(int pos) {
		JCExpression empty = new JCExpression(VARIABLE);
		empty.addToken(JavaTokenContext.IDENTIFIER, pos, "");
		return empty;
	}

	/**
	 * Return id of the operator or 'new' or 'instance' keywords or -1 for the
	 * rest.
	 */
	static int getOperatorID(TokenID tokenID) {
		int id = -1;

		if (tokenID.getCategory() == JavaTokenContext.OPERATORS) {
			id = tokenID.getNumericID();

		}
		else {
			switch (tokenID.getNumericID()) {
			case JavaTokenContext.NEW_ID:
				id = javaTokenIDsLength + NEW;
				break;

			case JavaTokenContext.INSTANCEOF_ID:
				id = javaTokenIDsLength + INSTANCEOF;
				break;

			}
		}

		return id;
	}

	static int getOperatorID(JCExpression exp) {
		int expID = (exp != null) ? exp.getExpID() : INVALID;
		switch (expID) {
		case OPERATOR:
			return exp.getTokenID(0).getNumericID();
		}
		return javaTokenIDsLength + expID;
	}

	static int getOperatorPrecedence(int opID) {
		return OP[opID] & 31;
	}

	static boolean isOperatorRightAssociative(int opID) {
		return (OP[opID] & RIGHT_ASSOCIATIVE) != 0;
	}

	/**
	 * Is the expression a valid type. It can be either datatype or array.
	 */
	static boolean isValidType(JCExpression exp) {
		switch (exp.getExpID()) {
		case ARRAY:
			if (exp.getParameterCount() == 1) {
				return isValidType(exp.getParameter(0));
			}
			return false;

		case DOT:
			int prmCnt = exp.getParameterCount();
			for (int i = 0; i < prmCnt; i++) {
				if (exp.getParameter(i).getExpID() != VARIABLE) {
					return false;
				}
			}
			return true;

		case TYPE:
		case VARIABLE:
			return true;
		}

		return false;
	}

	/** Get expression ID */
	public int getExpID() {
		return expID;
	}

	/** Set expression ID */
	void setExpID(int expID) {
		this.expID = expID;
	}

	public JCExpression getParent() {
		return parent;
	}

	void setParent(JCExpression parent) {
		this.parent = parent;
	}

	public JCType getType() {
		return type;
	}

	void setType(JCType type) {
		this.type = type;
	}

	public int getTokenCount() {
		return tokenCountM3 / 3;
	}

	public String getTokenText(int tokenInd) {
		tokenInd *= 3;
		return (String) tokenBlocks[tokenInd + 2];
	}

	public int getTokenOffset(int tokenInd) {
		tokenInd *= 3;
		return ((Integer) tokenBlocks[tokenInd + 1]).intValue();
	}

	public int getTokenLength(int tokenInd) {
		tokenInd *= 3;
		return ((String) tokenBlocks[tokenInd + 2]).length();
	}

	public TokenID getTokenID(int tokenInd) {
		tokenInd *= 3;
		return (TokenID) tokenBlocks[tokenInd];
	}

	void addToken(TokenID tokenID, int tokenOffset, String tokenText) {
		if (tokenCountM3 == tokenBlocks.length) {
			Object[] tmp = new Object[Math.max(3, tokenBlocks.length * 2)];
			if (tokenBlocks.length > 0) {
				System.arraycopy(tokenBlocks, 0, tmp, 0, tokenBlocks.length);
			}
			tokenBlocks = tmp;
		}

		tokenBlocks[tokenCountM3++] = tokenID;
		tokenBlocks[tokenCountM3++] = tokenOffset;
		tokenBlocks[tokenCountM3++] = tokenText;
	}

	public int getParameterCount() {
		return (prmList != null) ? prmList.size() : 0;
	}

	public JCExpression getParameter(int index) {
		return (JCExpression) prmList.get(index);
	}

	void addParameter(JCExpression prm) {
		if (prmList == null) {
			prmList = new ArrayList();
		}
		prm.setParent(this);
		prmList.add(prm);
	}

	void swapOperatorParms() {
		if ((expID == OPERATOR || expID == INSTANCEOF) && getParameterCount() == 2) {
			JCExpression exp1 = (JCExpression) prmList.remove(0);
			prmList.add(exp1);
			exp1.swapOperatorParms();
			((JCExpression) prmList.get(0)).swapOperatorParms();
		}
	}

	private static String getIndentString(int indent) {
		StringBuffer sb = new StringBuffer();
		while (indent-- > 0) {
			sb.append(' ');
		}
		return sb.toString();
	}

	static String getIDName(int expID) {
		switch (expID) {
		case CONSTANT:
			return "CONSTANT"; // NOI18N
		case VARIABLE:
			return "VARIABLE"; // NOI18N
		case OPERATOR:
			return "OPERATOR"; // NOI18N
		case UNARY_OPERATOR:
			return "UNARY_OPERATOR"; // NOI18N
		case DOT:
			return "DOT"; // NOI18N
		case DOT_OPEN:
			return "DOT_OPEN"; // NOI18N
		case ARRAY:
			return "ARRAY"; // NOI18N
		case ARRAY_OPEN:
			return "ARRAY_OPEN"; // NOI18N
		case PARENTHESIS_OPEN:
			return "PARENTHESIS_OPEN"; // NOI18N
		case PARENTHESIS:
			return "PARENTHESIS"; // NOI18N
		case METHOD_OPEN:
			return "METHOD_OPEN"; // NOI18N
		case METHOD:
			return "METHOD"; // NOI18N
		case CONSTRUCTOR:
			return "CONSTRUCTOR"; // NOI18N
		case CONVERSION:
			return "CONVERSION"; // NOI18N
		case TYPE:
			return "TYPE"; // NOI18N
		case NEW:
			return "NEW"; // NOI18N
		case INSTANCEOF:
			return "INSTANCEOF"; // NOI18N
		default:
			return "Unknown expID " + expID; // NOI18N
		}
	}

	public String toString(int indent) {
		String indentStr = getIndentString(indent);
		StringBuffer sb = new StringBuffer();
		sb.append("expID=" + getIDName(expID)); // NOI18N

		if (type != null) {
			sb.append(", result type="); // NOI18N
			sb.append(type);
		}

		// Debug tokens
		int tokenCnt = getTokenCount();
		sb.append(", token count="); // NOI18N
		sb.append(tokenCnt);
		if (tokenCnt > 0) {
			for (int i = 0; i < tokenCountM3;) {
				TokenID tokenID = (TokenID) tokenBlocks[i++];
				int tokenOffset = ((Integer) tokenBlocks[i++]).intValue();
				String tokenText = (String) tokenBlocks[i++];
				sb.append(", token" + (i / 3 - 1) + "='" + EditorDebug.debugString(tokenText) + "'"); // NOI18N
			}
		}

		// Debug parameters
		int parmCnt = getParameterCount();
		sb.append(", parm count="); // NOI18N
		sb.append(parmCnt);
		if (parmCnt > 0) {
			for (int i = 0; i < parmCnt; i++) {
				sb.append('\n');
				sb.append(indentStr);
				sb.append("parm" + i + "=[" + getParameter(i).toString(indent + 2) + "]"); // NOI18N
			}
		}
		return sb.toString();
	}

	public String toString() {
		return toString(0);
	}
}
