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
import java.util.List;

import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;

/**
 * Token processor that parses the text and produces jc expressions.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

class JCTokenProcessor implements TokenProcessor {

	private static final int CONSTANT = JCExpression.CONSTANT;
	private static final int VARIABLE = JCExpression.VARIABLE;
	private static final int OPERATOR = JCExpression.OPERATOR;
	private static final int UNARY_OPERATOR = JCExpression.UNARY_OPERATOR;
	private static final int DOT = JCExpression.DOT;
	private static final int DOT_OPEN = JCExpression.DOT_OPEN;
	private static final int ARRAY_OPEN = JCExpression.ARRAY_OPEN;
	private static final int ARRAY = JCExpression.ARRAY;
	private static final int PARENTHESIS_OPEN = JCExpression.PARENTHESIS_OPEN;
	private static final int PARENTHESIS = JCExpression.PARENTHESIS;
	private static final int METHOD_OPEN = JCExpression.METHOD_OPEN;
	private static final int METHOD = JCExpression.METHOD;
	private static final int CONSTRUCTOR = JCExpression.CONSTRUCTOR;
	private static final int CONVERSION = JCExpression.CONVERSION;
	private static final int TYPE = JCExpression.TYPE;
	private static final int NEW = JCExpression.NEW;
	private static final int INSTANCEOF = JCExpression.INSTANCEOF;

	private static final int NO_EXP = -1;

	/** Buffer that is scanned */
	private char[] buffer;

	/** Start position of the buffer in the document */
	private int bufferStartPos;

	/**
	 * Delta of the token processor buffer offsets against the offsets given in
	 * the source buffer.
	 */
	private int bufferOffsetDelta;

	/** The scanning was stopped by request by the token processor */
	private boolean stopped;

	/** Stack of the expressions. */
	private ArrayList expStack = new ArrayList();

	/** TokenID of the last found token except Syntax.EOT and Syntax.EOL */
	private TokenID lastValidTokenID;

	/** Text of the last found token except Syntax.EOT and Syntax.EOL */
	private String lastValidTokenText;

	// helper variables
	private TokenID curTokenID;
	private int curTokenPosition;
	private String curTokenText;

	JCTokenProcessor() {
	}

	/** Get the expression stack from the bottom to top */
	final List getStack() {
		return expStack;
	}

	/**
	 * Get the last token that was processed that wasn't either Syntax.EOT or
	 * Syntax.EOL.
	 */
	final TokenID getLastValidTokenID() {
		return lastValidTokenID;
	}

	final String getLastValidTokenText() {
		return lastValidTokenText;
	}

	/** Was the scanning stopped by request by the token processor */
	final boolean isStopped() {
		return stopped;
	}

	final JCExpression getResultExp() {
		return peekExp();
	}

	private void clearStack() {
		expStack.clear();
	}

	/** Push exp to top of stack */
	private void pushExp(JCExpression exp) {
		expStack.add(exp);
	}

	/** Pop exp from top of stack */
	private JCExpression popExp() {
		int cnt = expStack.size();
		return (cnt > 0) ? (JCExpression) expStack.remove(cnt - 1) : null;
	}

	/** Look at the exp at top of stack */
	private JCExpression peekExp() {
		int cnt = expStack.size();
		return (cnt > 0) ? (JCExpression) expStack.get(cnt - 1) : null;
	}

	/** Look at the second exp on stack */
	private JCExpression peekExp2() {
		int cnt = expStack.size();
		return (cnt > 1) ? (JCExpression) expStack.get(cnt - 2) : null;
	}

	/** Look at the third exp on stack */
	private JCExpression peekExp(int ind) {
		int cnt = expStack.size();
		return (cnt >= ind) ? (JCExpression) expStack.get(cnt - ind) : null;
	}

	private JCExpression createTokenExp(int id) {
		JCExpression exp = new JCExpression(id);
		addTokenTo(exp);
		return exp;
	}

	/** Add the token to a given expression */
	private void addTokenTo(JCExpression exp) {
		exp.addToken(curTokenID, curTokenPosition, curTokenText);
	}

	private int getValidExpID(JCExpression exp) {
		return (exp != null) ? exp.getExpID() : NO_EXP;
	}

	/**
	 * Check whether there can be any joining performed for current expressions
	 * on the stack.
	 * 
	 * @param tokenID
	 *            tokenID of the current token
	 * @return true to continue, false if errorneous construction found
	 */
	private boolean checkJoin(TokenID tokenID) {
		boolean ret = true;

		boolean cont = true;
		while (cont) {
			cont = false;
			JCExpression top = peekExp();
			JCExpression top2 = peekExp2();
			int top2ID = getValidExpID(top2);

			switch (getValidExpID(top)) {
			case CONSTANT:
			case VARIABLE:
			case METHOD:
			case CONSTRUCTOR:
			case ARRAY:
			case DOT:
			case PARENTHESIS:
			case OPERATOR: // operator on top of stack
				switch (top2ID) {
				case UNARY_OPERATOR:
					switch (tokenID.getNumericID()) {
					case JavaTokenContext.DOT_ID:
					case JavaTokenContext.LPAREN_ID:
					case JavaTokenContext.LBRACKET_ID:
					case JavaTokenContext.PLUS_PLUS_ID:
					case JavaTokenContext.MINUS_MINUS_ID:
						break;

					default: // Join
						if (top2.getParameterCount() == 0) {
							popExp(); // pop top
							top2.addParameter(top);
						}
						break;
					}
					break;

				case DOT_OPEN:
					if (tokenID.getCategory() == JavaTokenContext.OPERATORS) {
						switch (tokenID.getNumericID()) {
						case JavaTokenContext.LPAREN_ID:
							break;
						default:
							popExp();
							top2.addParameter(top);
							top2.setExpID(DOT);
						}
					}
					break;

				case CONVERSION:
					if (tokenID.getCategory() == JavaTokenContext.OPERATORS) {
						switch (tokenID.getNumericID()) {
						case JavaTokenContext.RPAREN_ID:
						case JavaTokenContext.COMMA_ID:
							JCExpression top3 = peekExp(3);
							if (top3 != null) {
								switch (top3.getExpID()) {
								case JCExpression.PARENTHESIS_OPEN:
								case JCExpression.METHOD_OPEN:
									popExp(); // pop top
									top2.addParameter(top); // add last to
															// conversion
									break;
								}
							}
							break;
						}
					}
					break;

				}
				break;
			}
		}

		int leftOpID = JCExpression.getOperatorID(tokenID);

		if (leftOpID >= 0) {
			switch (JCExpression.getOperatorPrecedence(leftOpID)) {
			case 0: // stop ID - try to join the exps on stack
				JCExpression lastVar = null;
				JCExpression rightOp = peekExp();
				int rightOpID = -1;
				rightOpID = JCExpression.getOperatorID(rightOp);
				switch (JCExpression.getOperatorPrecedence(rightOpID)) {
				case 0: // stop - nothing to join
					rightOp = null;
					break;

				case 1: // single item - move to next and add this one
					lastVar = rightOp;
					rightOp = peekExp2();
					rightOpID = JCExpression.getOperatorID(rightOp);
					switch (JCExpression.getOperatorPrecedence(rightOpID)) {
					case 0: // stop - only one item on the stack
						rightOp = null;
						break;

					case 1: // two items without operator - error
						ret = false;
						rightOp = null;
						break;

					default:
						popExp(); // pop item
						rightOp.addParameter(lastVar); // add item as parameter
						lastVar = null;
					}
					break;
				}

				if (rightOp != null) {
					popExp(); // pop rightOp
					cont = true;
					ArrayList opStack = new ArrayList(); // operator stack
					JCExpression leftOp = null;
					do {
						if (leftOp == null) {
							leftOp = popExp();
							if (leftOp == null) {
								break;
							}
							leftOpID = JCExpression.getOperatorID(leftOp);
						}
						switch (JCExpression.getOperatorPrecedence(leftOpID)) {
						case 0: // stop here
							pushExp(leftOp); // push last exp back to stack
							cont = false;
							break;

						case 1: // item found
							lastVar = leftOp;
							leftOp = null; // ask for next pop
							break;

						default: // operator found
							int leftOpPrec = JCExpression.getOperatorPrecedence(leftOpID);
							int rightOpPrec = JCExpression.getOperatorPrecedence(rightOpID);
							boolean rightPrec;
							if (leftOpPrec > rightOpPrec) { // left has greater
															// priority
								rightPrec = false;
							}
							else if (leftOpPrec < rightOpPrec) { // right has
																	// greater
																	// priority
								rightPrec = true;
							}
							else { // equal priorities
								rightPrec = JCExpression.isOperatorRightAssociative(rightOpID);
							}

							if (rightPrec) { // right operator has precedence
								if (lastVar != null) {
									rightOp.addParameter(lastVar);
								}
								if (opStack.size() > 0) { // at least one
															// right stacked op
									lastVar = rightOp; // rightOp becomes item
									rightOp = (JCExpression) opStack.remove(opStack.size() - 1); // get
																									// stacked
																									// op
									rightOpID = rightOp.getOperatorID(rightOp);
								}
								else { // shift the leftOp to rightOp
									leftOp.addParameter(rightOp);
									lastVar = null;
									rightOp = leftOp;
									rightOpID = leftOpID;
									leftOp = null; // ask for next poping
								}
							}
							else { // left operator has precedence
								if (lastVar != null) {
									leftOp.addParameter(lastVar);
									lastVar = null;
								}
								opStack.add(rightOp); // push right operator
														// to stack
								// rightOp.addParameter(leftOp);
								rightOp = leftOp; // shift left op to right op
								rightOpID = leftOpID;
								leftOp = null;
							}
						}
					} while (cont);

					// add possible valid last item
					if (lastVar != null) {
						rightOp.addParameter(lastVar);
					}

					// pop the whole stack adding the current right op to the
					// stack exp
					for (int i = opStack.size() - 1; i >= 0; i--) {
						JCExpression op = (JCExpression) opStack.get(i);
						op.addParameter(rightOp);
						rightOp = op;
					}

					rightOp.swapOperatorParms();
					pushExp(rightOp); // push the top operator
				}
				break;
			}
		}

		return ret;
	}

	public boolean token(TokenID tokenID, TokenContextPath tokenContextPath, int tokenOffset, int tokenLen) {

		tokenOffset += bufferOffsetDelta;
		// assign helper variables
		if (tokenID != null) {
			lastValidTokenID = tokenID;
		}

		curTokenID = tokenID;
		curTokenPosition = bufferStartPos + tokenOffset;
		curTokenText = new String(buffer, tokenOffset, tokenLen);
		boolean err = false; // whether the parser cannot understand given
								// tokens

		checkJoin(tokenID);

		JCExpression top = peekExp(); // exp at top of stack
		int topID = getValidExpID(top); // id of the exp at top of stack

		JCExpression constExp = null; // possibly assign constant into this
										// exp
		JCType kwdType = null; // keyword constant type (used in conversions)

		if (tokenID == null) { // invalid token-id
			err = true;

		}
		else { // valid token-id
			switch (tokenID.getNumericID()) { // test the token ID
			case JavaTokenContext.BOOLEAN_ID:
				kwdType = JavaCompletion.BOOLEAN_TYPE;
				break;
			case JavaTokenContext.BYTE_ID:
				kwdType = JavaCompletion.BYTE_TYPE;
				break;
			case JavaTokenContext.CHAR_ID:
				kwdType = JavaCompletion.CHAR_TYPE;
				break;
			case JavaTokenContext.DOUBLE_ID:
				kwdType = JavaCompletion.DOUBLE_TYPE;
				break;
			case JavaTokenContext.FLOAT_ID:
				kwdType = JavaCompletion.FLOAT_TYPE;
				break;
			case JavaTokenContext.INT_ID:
				kwdType = JavaCompletion.INT_TYPE;
				break;
			case JavaTokenContext.LONG_ID:
				kwdType = JavaCompletion.LONG_TYPE;
				break;
			case JavaTokenContext.SHORT_ID:
				kwdType = JavaCompletion.SHORT_TYPE;
				break;

			case JavaTokenContext.TRUE_ID:
			case JavaTokenContext.FALSE_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.BOOLEAN_TYPE);
				break;

			case JavaTokenContext.NULL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.NULL_TYPE);
				break;

			case JavaTokenContext.CLASS_ID:
				if (topID == DOT_OPEN) {
					pushExp(createTokenExp(VARIABLE));
				}
				else {
					err = true;
				}
				break;

			case JavaTokenContext.NEW_ID:
				switch (topID) {
				case VARIABLE:
				case NEW:
					err = true;
					break;

				case DOT_OPEN:
					pushExp(createTokenExp(VARIABLE));
					break;

				default:
					pushExp(createTokenExp(NEW));
					break;
				}
				break;

			case JavaTokenContext.SUPER_ID:
			case JavaTokenContext.THIS_ID:
				pushExp(createTokenExp(VARIABLE));
				break;

			case JavaTokenContext.INSTANCEOF_ID:
				switch (topID) {
				case CONSTANT:
				case VARIABLE:
				case METHOD:
				case CONSTRUCTOR:
				case ARRAY:
				case DOT:
				case PARENTHESIS:
					pushExp(createTokenExp(INSTANCEOF));
					break;
				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.VOID_ID:
			case JavaTokenContext.ABSTRACT_ID:
			case JavaTokenContext.BREAK_ID:
			case JavaTokenContext.CASE_ID:
			case JavaTokenContext.CATCH_ID:
			case JavaTokenContext.CONST_ID:
			case JavaTokenContext.CONTINUE_ID:
			case JavaTokenContext.DEFAULT_ID:
			case JavaTokenContext.DO_ID:
			case JavaTokenContext.ELSE_ID:
			case JavaTokenContext.EXTENDS_ID:
			case JavaTokenContext.FINAL_ID:
			case JavaTokenContext.FINALLY_ID:
			case JavaTokenContext.FOR_ID:
			case JavaTokenContext.GOTO_ID:
			case JavaTokenContext.IF_ID:
			case JavaTokenContext.IMPLEMENTS_ID:
			case JavaTokenContext.IMPORT_ID:
			case JavaTokenContext.INTERFACE_ID:
			case JavaTokenContext.NATIVE_ID:
			case JavaTokenContext.PACKAGE_ID:
			case JavaTokenContext.PRIVATE_ID:
			case JavaTokenContext.PROTECTED_ID:
			case JavaTokenContext.PUBLIC_ID:
			case JavaTokenContext.RETURN_ID:
			case JavaTokenContext.STATIC_ID:
			case JavaTokenContext.SWITCH_ID:
			case JavaTokenContext.SYNCHRONIZED_ID:
			case JavaTokenContext.THROW_ID:
			case JavaTokenContext.THROWS_ID:
			case JavaTokenContext.TRANSIENT_ID:
			case JavaTokenContext.TRY_ID:
			case JavaTokenContext.VOLATILE_ID:
			case JavaTokenContext.WHILE_ID:
				err = true;
				break;

			case JavaTokenContext.IDENTIFIER_ID: // identifier found e.g. 'a'
			{
				switch (topID) {
				case OPERATOR:
				case DOT_OPEN:
				case ARRAY_OPEN:
				case PARENTHESIS_OPEN:
				case METHOD_OPEN:
				case NEW:
				case CONVERSION:
				case UNARY_OPERATOR:
				case INSTANCEOF:
				case NO_EXP:
					pushExp(createTokenExp(VARIABLE));
					break;

				default:
					err = true;
					break;
				}
			}
				break;

			case JavaTokenContext.EQ_ID: // Assignment operators
			case JavaTokenContext.PLUS_EQ_ID:
			case JavaTokenContext.MINUS_EQ_ID:
			case JavaTokenContext.MUL_EQ_ID:
			case JavaTokenContext.DIV_EQ_ID:
			case JavaTokenContext.AND_EQ_ID:
			case JavaTokenContext.OR_EQ_ID:
			case JavaTokenContext.XOR_EQ_ID:
			case JavaTokenContext.MOD_EQ_ID:
			case JavaTokenContext.LSHIFT_EQ_ID:
			case JavaTokenContext.RSSHIFT_EQ_ID:
			case JavaTokenContext.RUSHIFT_EQ_ID:

			case JavaTokenContext.LT_ID: // Binary, result is boolean
			case JavaTokenContext.GT_ID:
			case JavaTokenContext.LT_EQ_ID:
			case JavaTokenContext.GT_EQ_ID:
			case JavaTokenContext.EQ_EQ_ID:
			case JavaTokenContext.NOT_EQ_ID:

			case JavaTokenContext.AND_AND_ID: // Binary, result is boolean
			case JavaTokenContext.OR_OR_ID:

			case JavaTokenContext.LSHIFT_ID: // Always binary
			case JavaTokenContext.RSSHIFT_ID:
			case JavaTokenContext.RUSHIFT_ID:
			case JavaTokenContext.MUL_ID:
			case JavaTokenContext.DIV_ID:
			case JavaTokenContext.AND_ID:
			case JavaTokenContext.OR_ID:
			case JavaTokenContext.XOR_ID:
			case JavaTokenContext.MOD_ID:

			case JavaTokenContext.QUESTION_ID:
			case JavaTokenContext.COLON_ID:

				// Operator handling
				switch (topID) {
				case CONSTANT:
				case VARIABLE:
				case METHOD:
				case CONSTRUCTOR:
				case ARRAY:
				case DOT:
				case PARENTHESIS:
				case OPERATOR:
				case UNARY_OPERATOR:
					pushExp(createTokenExp(OPERATOR));
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.PLUS_PLUS_ID: // Prefix or postfix
			case JavaTokenContext.MINUS_MINUS_ID:
				switch (topID) {
				case METHOD_OPEN:
				case ARRAY_OPEN:
				case PARENTHESIS_OPEN:
				case OPERATOR:
				case UNARY_OPERATOR:
				case NO_EXP:
					// Prefix operator
					JCExpression opExp = createTokenExp(UNARY_OPERATOR);
					pushExp(opExp); // add operator as new exp
					break;

				case VARIABLE: // is it only one permitted?
					// Postfix operator
					opExp = createTokenExp(UNARY_OPERATOR);
					popExp(); // pop top
					opExp.addParameter(top);
					pushExp(opExp);
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.PLUS_ID: // Can be unary or binary
			case JavaTokenContext.MINUS_ID:
				switch (topID) {
				case CONSTANT:
				case VARIABLE:
				case METHOD:
				case CONSTRUCTOR:
				case ARRAY:
				case DOT:
				case PARENTHESIS:
				case UNARY_OPERATOR:
					JCExpression opExp = createTokenExp(OPERATOR);
					pushExp(opExp);
					break;

				case METHOD_OPEN:
				case ARRAY_OPEN:
				case PARENTHESIS_OPEN:
				case OPERATOR:
				case NO_EXP:
					// Unary operator
					opExp = createTokenExp(UNARY_OPERATOR);
					pushExp(opExp); // add operator as new exp
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.NEG_ID: // Always unary
			case JavaTokenContext.NOT_ID:
				switch (topID) {
				case METHOD_OPEN:
				case ARRAY_OPEN:
				case PARENTHESIS_OPEN:
				case OPERATOR:
				case UNARY_OPERATOR:
				case NO_EXP:
					// Unary operator
					JCExpression opExp = createTokenExp(UNARY_OPERATOR);
					pushExp(opExp); // add operator as new exp
					break;

				default:
					err = true;
					break;
				}

			case JavaTokenContext.DOT_ID: // '.' found
				switch (topID) {
				case CONSTANT:
				case VARIABLE:
				case ARRAY:
				case METHOD:
				case CONSTRUCTOR:
				case PARENTHESIS:
					popExp();
					JCExpression opExp = createTokenExp(DOT_OPEN);
					opExp.addParameter(top);
					pushExp(opExp);
					break;

				case DOT:
					addTokenTo(top);
					top.setExpID(DOT_OPEN);
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.COMMA_ID: // ',' found
				switch (topID) {
				case ARRAY:
				case DOT:
				case TYPE:
				case CONSTANT:
				case VARIABLE:
				case CONSTRUCTOR:
				case CONVERSION:
				case PARENTHESIS:
				case OPERATOR:
				case UNARY_OPERATOR:
				case INSTANCEOF:
					JCExpression top2 = peekExp2();
					switch (getValidExpID(top2)) {
					case METHOD_OPEN:
						popExp();
						top2.addParameter(top);
						top = top2;
						break;
					default:
						err = true;
						break;
					}
					break;

				case METHOD_OPEN:
					addTokenTo(top);
					break;

				default:
					err = true;
					break;

				}
				break;

			case JavaTokenContext.SEMICOLON_ID:
				err = true;
				break;

			case JavaTokenContext.LPAREN_ID:
				switch (topID) {
				case VARIABLE:
					top.setExpID(METHOD_OPEN);
					addTokenTo(top);
					break;

				case ARRAY: // a[0](
					popExp();
					JCExpression mtdExp = createTokenExp(METHOD);
					mtdExp.addParameter(top);
					pushExp(mtdExp);
					break;

				case ARRAY_OPEN: // a[(
				case PARENTHESIS_OPEN: // ((
				case METHOD_OPEN: // a((
				case NO_EXP:
				case OPERATOR: // 3+(
				case CONVERSION: // (int)(
					pushExp(createTokenExp(PARENTHESIS_OPEN));
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.RPAREN_ID:
				boolean mtd = false;
				switch (topID) {
				case CONSTANT:
				case VARIABLE:
				case ARRAY:
				case DOT:
				case TYPE:
				case CONSTRUCTOR:
				case CONVERSION:
				case PARENTHESIS:
				case OPERATOR:
				case UNARY_OPERATOR:
				case INSTANCEOF:
					JCExpression top2 = peekExp2();
					switch (getValidExpID(top2)) {
					case PARENTHESIS_OPEN:
						popExp();
						top2.addParameter(top);
						top2.setExpID(JCExpression.isValidType(top) ? CONVERSION : PARENTHESIS);
						addTokenTo(top2);
						break;

					case METHOD_OPEN:
						popExp();
						top2.addParameter(top);
						top = top2;
						mtd = true;
						break;
					case CONVERSION:
						popExp();
						top2.addParameter(top);
						top = top2;
						top2 = peekExp2();
						if (getValidExpID(top2) == PARENTHESIS_OPEN) {
							popExp();
							top2.addParameter(top);
							top2.setExpID(PARENTHESIS);
							top = top2;
						}

						break;

					default:
						err = true;
						break;
					}
					break;

				case METHOD_OPEN:
					mtd = true;
					break;

				// case PARENTHESIS_OPEN: // empty parenthesis
				default:
					err = true;
					break;
				}

				if (mtd) {
					addTokenTo(top);
					top.setExpID(METHOD);
					JCExpression top2 = peekExp2();
					switch (getValidExpID(top2)) {
					case DOT_OPEN:
						JCExpression top3 = peekExp(3);
						if (getValidExpID(top3) == NEW) {
							popExp(); // pop top
							top2.addParameter(top); // add METHOD to DOT
							top2.setExpID(DOT);
							popExp(); // pop top2
							top3.setExpID(CONSTRUCTOR);
							top3.addParameter(top2); // add DOT to
														// CONSTRUCTOR
						}
						break;

					case NEW:
						top2.setExpID(CONSTRUCTOR);
						top2.addParameter(top);
						popExp(); // pop top
						break;
					}
				}
				break;

			case JavaTokenContext.LBRACKET_ID:
				switch (topID) {
				case VARIABLE:
				case METHOD:
				case DOT:
				case ARRAY:
				case TYPE: // ... int[ ...
					popExp(); // top popped
					JCExpression arrExp = createTokenExp(ARRAY_OPEN);
					addTokenTo(arrExp);
					arrExp.addParameter(top);
					pushExp(arrExp);
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.RBRACKET_ID:
				switch (topID) {
				case VARIABLE:
				case METHOD:
				case DOT:
				case ARRAY:
				case PARENTHESIS:
				case CONSTANT:
				case OPERATOR:
				case UNARY_OPERATOR:
				case INSTANCEOF:
					JCExpression top2 = peekExp2();
					switch (getValidExpID(top2)) {
					case ARRAY_OPEN:
						popExp(); // top popped
						top2.addParameter(top);
						top2.setExpID(ARRAY);
						addTokenTo(top2);
						break;

					default:
						err = true;
						break;
					}
					break;

				case ARRAY_OPEN:
					top.setExpID(ARRAY);
					addTokenTo(top);
					break;

				default:
					err = true;
					break;
				}
				break;

			case JavaTokenContext.LBRACE_ID:
				err = true;
				break;

			case JavaTokenContext.RBRACE_ID:
				err = true;
				break;

			case JavaTokenContext.LINE_COMMENT_ID:
				// Skip line comment
				break;

			case JavaTokenContext.BLOCK_COMMENT_ID:
				// Skip block comment
				break;

			case JavaTokenContext.CHAR_LITERAL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.CHAR_TYPE);
				break;

			case JavaTokenContext.STRING_LITERAL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.STRING_TYPE);
				break;

			case JavaTokenContext.INT_LITERAL_ID:
			case JavaTokenContext.HEX_LITERAL_ID:
			case JavaTokenContext.OCTAL_LITERAL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.INT_TYPE);
				break;

			case JavaTokenContext.LONG_LITERAL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.LONG_TYPE);
				break;

			case JavaTokenContext.FLOAT_LITERAL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.FLOAT_TYPE);
				break;

			case JavaTokenContext.DOUBLE_LITERAL_ID:
				constExp = createTokenExp(CONSTANT);
				constExp.setType(JavaCompletion.DOUBLE_TYPE);
				break;

			} // end of testing keyword type
		}

		// Check whether a constant or data type keyword was found
		if (constExp != null) {
			switch (topID) {
			case DOT_OPEN:
				err = true;
				break;

			case ARRAY_OPEN:
			case PARENTHESIS_OPEN:
			case PARENTHESIS: // can be conversion
			case METHOD_OPEN:
			case OPERATOR:
			case UNARY_OPERATOR:
			case CONVERSION:
			case NO_EXP:
				pushExp(constExp);
				break;

			default:
				err = true;
				break;
			}
		}

		if (kwdType != null) { // keyword constant (in conversions)
			switch (topID) {
			case PARENTHESIS_OPEN: // conversion
				JCExpression kwdExp = createTokenExp(TYPE);
				addTokenTo(kwdExp);
				kwdExp.setType(kwdType);
				pushExp(kwdExp);
				break;

			default: // otherwise not recognized
				err = true;
				break;
			}
		}

		if (err) {
			clearStack();

			if (tokenID == JavaTokenContext.IDENTIFIER) {
				pushExp(createTokenExp(VARIABLE));
			}
		}

		return !stopped;
	}

	public int eot(int offset) {
		// Check for joins
		boolean reScan = true;
		while (reScan) {
			reScan = false;
			JCExpression top = peekExp();
			JCExpression top2 = peekExp2();
			int top2ID = getValidExpID(top2);
			if (top != null) {
				switch (getValidExpID(top)) {
				case VARIABLE:
					switch (top2ID) {
					case DOT_OPEN:
						popExp();
						top2.addParameter(top);
						top2.setExpID(DOT);
						reScan = true;
						break;
					case NEW:
						popExp();
						top2.addParameter(top);
						top2.setExpID(CONSTRUCTOR);
						reScan = true;
						break;
					}
					break;

				case METHOD_OPEN:
					// let it flow to METHOD
				case METHOD:
					switch (top2ID) {
					case DOT_OPEN:
						popExp();
						top2.addParameter(top);
						top2.setExpID(DOT);
						reScan = true;
						break;
					case NEW:
						popExp();
						top2.addParameter(top);
						top2.setExpID(CONSTRUCTOR);
						reScan = true;
						break;
					}
					break;

				case DOT:
				case DOT_OPEN:
					switch (top2ID) {
					case NEW:
						popExp();
						top2.addParameter(top);
						top2.setExpID(CONSTRUCTOR);
						reScan = true;
						break;
					}
				}
			}
			else { // nothing on the stack, create empty variable
				pushExp(JCExpression.createEmptyVariable(bufferStartPos + bufferOffsetDelta + offset));
			}
		}
		// System.out.println(this);
		return 0;
	}

	public void nextBuffer(char[] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
		this.buffer = new char[len + preScan];
		System.arraycopy(buffer, offset - preScan, this.buffer, 0, len + preScan);
		bufferOffsetDelta = preScan - offset;
		this.bufferStartPos = startPos - preScan;
	}

	public String toString() {
		int cnt = expStack.size();
		StringBuffer sb = new StringBuffer();
		if (stopped) {
			sb.append("Parsing STOPPED by request.\n"); // NOI18N
		}
		sb.append("Stack size is " + cnt + "\n"); // NOI18N
		if (cnt > 0) {
			sb.append("Stack expressions:\n"); // NOI18N
			for (int i = 0; i < cnt; i++) {
				JCExpression e = (JCExpression) expStack.get(i);
				sb.append("Stack["); // NOI18N
				sb.append(i);
				sb.append("]: "); // NOI18N
				sb.append(e.toString(0));
				sb.append('\n');
			}
		}
		return sb.toString();
	}

}
