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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.Analyzer;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.FinderFactory;
import org.netbeans.editor.TextBatchProcessor;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.ext.ExtSyntaxSupport;

/**
 * Support methods for syntax analyzes
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JavaSyntaxSupport extends ExtSyntaxSupport {

	// Internal java declaration token processor states
	static final int INIT = 0;
	static final int AFTER_TYPE = 1;
	static final int AFTER_VARIABLE = 2;
	static final int AFTER_COMMA = 3;
	static final int AFTER_DOT = 4;
	static final int AFTER_TYPE_LSB = 5;
	static final int AFTER_MATCHING_VARIABLE_LSB = 6;
	static final int AFTER_MATCHING_VARIABLE = 7;
	static final int AFTER_EQUAL = 8; // in decl after "var ="

	private static final TokenID[] COMMENT_TOKENS = new TokenID[] { JavaTokenContext.LINE_COMMENT, JavaTokenContext.BLOCK_COMMENT };

	private static final TokenID[] BRACKET_SKIP_TOKENS = new TokenID[] { JavaTokenContext.LINE_COMMENT, JavaTokenContext.BLOCK_COMMENT,
			JavaTokenContext.CHAR_LITERAL, JavaTokenContext.STRING_LITERAL };

	private static final char[] COMMAND_SEPARATOR_CHARS = new char[] { ';', '{', '}' };

	private JavaImport javaImport;

	public JavaSyntaxSupport(BaseDocument doc) {
		super(doc);

		tokenNumericIDsValid = true;
		javaImport = new JavaImport();
	}

	protected void documentModified(DocumentEvent evt) {
		super.documentModified(evt);
		javaImport.documentModifiedAtPosition(evt.getOffset());
	}

	public TokenID[] getCommentTokens() {
		return COMMENT_TOKENS;
	}

	public TokenID[] getBracketSkipTokens() {
		return BRACKET_SKIP_TOKENS;
	}

	/**
	 * Return the position of the last command separator before the given
	 * position.
	 */
	public int getLastCommandSeparator(int pos) throws BadLocationException {
		TextBatchProcessor tbp = new TextBatchProcessor() {
			public int processTextBatch(BaseDocument doc, int startPos, int endPos, boolean lastBatch) {
				try {
					int[] blks = getCommentBlocks(endPos, startPos);
					FinderFactory.CharArrayBwdFinder cmdFinder = new FinderFactory.CharArrayBwdFinder(COMMAND_SEPARATOR_CHARS);
					return findOutsideBlocks(cmdFinder, startPos, endPos, blks);
				} catch (BadLocationException e) {
					e.printStackTrace();
					return -1;
				}
			}
		};
		return getDocument().processText(tbp, pos, 0);
	}

	/**
	 * Get the class from name. The import sections are consulted to find the
	 * proper package for the name. If the search in import sections fails the
	 * method can ask the finder to search just by the given name.
	 * 
	 * @param className
	 *            name to resolve. It can be either the full name or just the
	 *            name without the package.
	 * @param searchByName
	 *            if true and the resolving through the import sections fails
	 *            the finder is asked to find the class just by the given name
	 */
	public JCClass getClassFromName(String className, boolean searchByName) {
		refreshJavaImport();
		JCClass ret = JavaCompletion.getPrimitiveClass(className);
		if (ret == null) {

			ret = javaImport.getClazz(className);
		}
		if (ret == null && searchByName) {
			if (isUnknownImport(className))
				return null;
			List clsList = JavaCompletion.getFinder().findClasses(null, className, true);
			if (clsList != null && clsList.size() > 0) {
				if (clsList.size() > 0) { // more matching classes
					ret = (JCClass) clsList.get(0); // get the first one
				}
			}

		}
		return ret;
	}

	protected boolean isUnknownImport(String className) {
		return javaImport.isUnknownImport(className);
	}

	/** Returns all imports that aren't in parser DB yet */
	protected List getUnknownImports() {
		return javaImport.getUnknownImports();
	}

	/**
	 * Returns true if the given class is in the import statement directly or
	 * indirectly (package.name.*)
	 */
	public boolean isImported(JCClass cls) {
		return javaImport.isImported(cls);
	}

	public void refreshJavaImport() {
		javaImport.update(getDocument());
	}

	protected void refreshClassInfo() {
	}

	/** Get the class that belongs to the given position */
	public JCClass getClass(int pos) {
		return null;
	}

	public boolean isStaticBlock(int pos) {
		return false;
	}

	protected DeclarationTokenProcessor createDeclarationTokenProcessor(String varName, int startPos, int endPos) {
		return new JavaDeclarationTokenProcessor(this, varName);
	}

	protected VariableMapTokenProcessor createVariableMapTokenProcessor(int startPos, int endPos) {
		return new JavaDeclarationTokenProcessor(this, null);
	}

	/** Checks, whether caret is inside method */
	private boolean insideMethod(JTextComponent textComp, int startPos) {
		try {
			int level = 0;
			BaseDocument doc = (BaseDocument) textComp.getDocument();
			for (int i = startPos - 1; i > 0; i--) {
				char ch = doc.getChars(i, 1)[0];
				if (ch == ';')
					return false;
				if (ch == ')')
					level++;
				if (ch == '(') {
					if (level == 0) {
						return true;
					}
					else {
						level--;
					}
				}
			}
			return false;
		} catch (BadLocationException e) {
			return false;
		}
	}

	/** Check and possibly popup, hide or refresh the completion */
	public int checkCompletion(JTextComponent target, String typedText, boolean visible) {
		if (!visible) { // pane not visible yet
			int dotPos = target.getCaret().getDot();
			switch (typedText.charAt(0)) {
			case ' ':
				BaseDocument doc = (BaseDocument) target.getDocument();

				if (dotPos >= 2) { // last char before inserted space
					int pos = Math.max(dotPos - 8, 0);
					try {
						String txtBeforeSpace = doc.getText(pos, dotPos - pos);

						if (txtBeforeSpace.endsWith("new ")) {
							if ((txtBeforeSpace.length() > 4) && (!Character.isJavaIdentifierPart(txtBeforeSpace.charAt(txtBeforeSpace.length() - 5)))) {
								return ExtSyntaxSupport.COMPLETION_POPUP;
							}
						}

						if (txtBeforeSpace.endsWith("import ") && !Character.isJavaIdentifierPart(txtBeforeSpace.charAt(0))) {
							return ExtSyntaxSupport.COMPLETION_POPUP;
						}

						if (txtBeforeSpace.endsWith(", ")) {
							// autoPopup completion only if caret is inside
							// method
							if (insideMethod(target, dotPos))
								return ExtSyntaxSupport.COMPLETION_POPUP;
						}
					} catch (BadLocationException e) {
					}
				}
				break;

			case '.':
				return ExtSyntaxSupport.COMPLETION_POPUP;
			case ',':
				// autoPopup completion only if caret is inside method
				if (insideMethod(target, dotPos))
					return ExtSyntaxSupport.COMPLETION_POPUP;
			}
			return ExtSyntaxSupport.COMPLETION_CANCEL;

		}
		else { // the pane is already visible
			switch (typedText.charAt(0)) {
			case '=':
			case '{':
			case ';':
				return ExtSyntaxSupport.COMPLETION_HIDE;
			default:
				return ExtSyntaxSupport.COMPLETION_POST_REFRESH;
			}
		}
	}

	public static class JavaDeclarationTokenProcessor implements DeclarationTokenProcessor, VariableMapTokenProcessor {

		protected JavaSyntaxSupport sup;

		/** Position of the begining of the declaration to be returned */
		int decStartPos = -1;

		int decArrayDepth;

		/** Starting position of the declaration type */
		int typeStartPos;

		/** Position of the end of the type */
		int typeEndPos;

		/** Offset of the name of the variable */
		int decVarNameOffset;

		/** Length of the name of the variable */
		int decVarNameLen;

		/** Currently inside parenthesis, i.e. comma delimits declarations */
		int parenthesisCounter;

		/** Depth of the array when there is an array declaration */
		int arrayDepth;

		char[] buffer;

		int bufferStartPos;

		String varName;

		int state;

		/** Map filled with the [varName, type] pairs */
		HashMap varMap;

		/**
		 * Construct new token processor
		 * 
		 * @param varName
		 *            it contains valid varName name or null to search for all
		 *            variables and construct the variable map.
		 */
		public JavaDeclarationTokenProcessor(JavaSyntaxSupport sup, String varName) {
			this.sup = sup;
			this.varName = varName;
			if (varName == null) {
				varMap = new HashMap();
			}
		}

		public int getDeclarationPosition() {
			return decStartPos;
		}

		public Map getVariableMap() {
			return varMap;
		}

		private void processDeclaration() {
			if (varName == null) { // collect all variables
				String decType = new String(buffer, typeStartPos - bufferStartPos, typeEndPos - typeStartPos);
				if (decType.indexOf(' ') >= 0) {
					decType = Analyzer.removeSpaces(decType);
				}
				String decVarName = new String(buffer, decVarNameOffset, decVarNameLen);
				JCClass cls = sup.getClassFromName(decType, true);
				if (cls != null) {
					varMap.put(decVarName, JavaCompletion.getType(cls, decArrayDepth));
				}
				else {
					// Maybe it's inner class. Stick an outerClass before it ...
					JCClass outerCls = sup.getClass(decVarNameOffset);
					if (outerCls != null) {
						String outerClassName = outerCls.getFullName();
						JCClass innerClass = JavaCompletion.getFinder().getExactClass(outerClassName + "." + decType);
						if (innerClass != null) {
							varMap.put(decVarName, JavaCompletion.getType(innerClass, decArrayDepth));
						}
					}
				}
			}
			else {
				decStartPos = typeStartPos;
			}
		}

		public boolean token(TokenID tokenID, TokenContextPath tokenContextPath, int tokenOffset, int tokenLen) {
			int pos = bufferStartPos + tokenOffset;

			// Check whether we are really recognizing the java tokens
			if (!tokenContextPath.contains(JavaTokenContext.contextPath)) {
				state = INIT;
				return true;
			}

			switch (tokenID.getNumericID()) {
			case JavaTokenContext.BOOLEAN_ID:
			case JavaTokenContext.BYTE_ID:
			case JavaTokenContext.CHAR_ID:
			case JavaTokenContext.DOUBLE_ID:
			case JavaTokenContext.FLOAT_ID:
			case JavaTokenContext.INT_ID:
			case JavaTokenContext.LONG_ID:
			case JavaTokenContext.SHORT_ID:
			case JavaTokenContext.VOID_ID:
				typeStartPos = pos;
				arrayDepth = 0;
				typeEndPos = pos + tokenLen;
				state = AFTER_TYPE;
				break;

			case JavaTokenContext.DOT_ID:
				switch (state) {
				case AFTER_TYPE: // allowed only inside type
					state = AFTER_DOT;
					typeEndPos = pos + tokenLen;
					break;

				case AFTER_EQUAL:
				case AFTER_VARIABLE:
					break;

				default:
					state = INIT;
					break;
				}
				break;

			case JavaTokenContext.LBRACKET_ID:
				switch (state) {
				case AFTER_TYPE:
					state = AFTER_TYPE_LSB;
					arrayDepth++;
					break;

				case AFTER_MATCHING_VARIABLE:
					state = AFTER_MATCHING_VARIABLE_LSB;
					decArrayDepth++;
					break;

				case AFTER_EQUAL:
					break;

				default:
					state = INIT;
					break;
				}
				break;

			case JavaTokenContext.RBRACKET_ID:
				switch (state) {
				case AFTER_TYPE_LSB:
					state = AFTER_TYPE;
					break;

				case AFTER_MATCHING_VARIABLE_LSB:
					state = AFTER_MATCHING_VARIABLE;
					break;

				case AFTER_EQUAL:
					break;

				default:
					state = INIT;
					break;
				}
				break; // both in type and varName

			case JavaTokenContext.LPAREN_ID:
				parenthesisCounter++;
				if (state != AFTER_EQUAL) {
					state = INIT;
				}
				break;

			case JavaTokenContext.RPAREN_ID:
				if (state == AFTER_MATCHING_VARIABLE) {
					processDeclaration();
				}
				if (parenthesisCounter > 0) {
					parenthesisCounter--;
				}
				if (state != AFTER_EQUAL) {
					state = INIT;
				}
				break;

			case JavaTokenContext.LBRACE_ID:
			case JavaTokenContext.RBRACE_ID:
				if (parenthesisCounter > 0) {
					parenthesisCounter--; // to tolerate opened parenthesis
				}
				state = INIT;
				break;

			case JavaTokenContext.COMMA_ID:
				if (parenthesisCounter > 0) { // comma is declaration
												// separator in parenthesis
					if (parenthesisCounter == 1 && state == AFTER_MATCHING_VARIABLE) {
						processDeclaration();
					}
					if (state != AFTER_EQUAL) {
						state = INIT;
					}
				}
				else { // not in parenthesis
					switch (state) {
					case AFTER_MATCHING_VARIABLE:
						processDeclaration();
						// let it flow to AFTER_VARIABLE
					case AFTER_VARIABLE:
					case AFTER_EQUAL:
						state = AFTER_COMMA;
						break;

					default:
						state = INIT;
						break;
					}
				}
				break;

			case JavaTokenContext.NEW_ID:
				if (state != AFTER_EQUAL) {
					state = INIT;
				}
				break;

			case JavaTokenContext.EQ_ID:
				switch (state) {
				case AFTER_MATCHING_VARIABLE:
					processDeclaration();
					// flow to AFTER_VARIABLE

				case AFTER_VARIABLE:
					state = AFTER_EQUAL;
					break;

				case AFTER_EQUAL:
					break;

				default:
					state = INIT;
				}
				break;

			case JavaTokenContext.SEMICOLON_ID:
				if (state == AFTER_MATCHING_VARIABLE) {
					processDeclaration();
				}
				state = INIT;
				break;

			case JavaTokenContext.IDENTIFIER_ID:
				switch (state) {
				case AFTER_TYPE:
				case AFTER_COMMA:
					if (varName == null || Analyzer.equals(varName, buffer, tokenOffset, tokenLen)) {
						decArrayDepth = arrayDepth;
						decVarNameOffset = tokenOffset;
						decVarNameLen = tokenLen;
						state = AFTER_MATCHING_VARIABLE;
					}
					else {
						state = AFTER_VARIABLE;
					}
					break;

				case AFTER_VARIABLE: // error
					state = INIT;
					break;

				case AFTER_EQUAL:
					break;

				case AFTER_DOT:
					typeEndPos = pos + tokenLen;
					state = AFTER_TYPE;
					break;

				case INIT:
					typeStartPos = pos;
					arrayDepth = 0;
					typeEndPos = pos + tokenLen;
					state = AFTER_TYPE;
					break;

				default:
					state = INIT;
					break;
				}
				break;

			case JavaTokenContext.WHITESPACE_ID: // whitespace ignored
				break;

			}

			return true;
		}

		public int eot(int offset) {
			return 0;
		}

		public void nextBuffer(char[] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
			this.buffer = buffer;
			bufferStartPos = startPos - offset;
		}

	}

}
