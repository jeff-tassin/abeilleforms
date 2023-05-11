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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.KeyStroke;

import org.netbeans.editor.Acceptor;
import org.netbeans.editor.AcceptorFactory;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.MultiKeyBinding;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.ext.ExtSettingsDefaults;

/**
 * Default settings values for Java.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JavaSettingsDefaults extends ExtSettingsDefaults {

	public static final Boolean defaultCaretSimpleMatchBrace = Boolean.FALSE;
	public static final Boolean defaultHighlightMatchingBracket = Boolean.TRUE;

	public static final Acceptor defaultIdentifierAcceptor = AcceptorFactory.JAVA_IDENTIFIER;
	public static final Acceptor defaultAbbrevResetAcceptor = AcceptorFactory.NON_JAVA_IDENTIFIER;
	public static final Boolean defaultWordMatchMatchCase = Boolean.TRUE;

	// Formatting
	public static final Boolean defaultJavaFormatSpaceBeforeParenthesis = Boolean.FALSE;
	public static final Boolean defaultJavaFormatSpaceAfterComma = Boolean.TRUE;
	public static final Boolean defaultJavaFormatNewlineBeforeBrace = Boolean.FALSE;
	public static final Boolean defaultJavaFormatLeadingSpaceInComment = Boolean.FALSE;
	public static final Boolean defaultJavaFormatLeadingStarInComment = Boolean.TRUE;

	/** @deprecated */
	@Deprecated
	public static final Boolean defaultFormatSpaceBeforeParenthesis = defaultJavaFormatSpaceBeforeParenthesis;
	/** @deprecated */
	@Deprecated
	public static final Boolean defaultFormatSpaceAfterComma = defaultJavaFormatSpaceAfterComma;
	/** @deprecated */
	@Deprecated
	public static final Boolean defaultFormatNewlineBeforeBrace = defaultJavaFormatNewlineBeforeBrace;
	/** @deprecated */
	@Deprecated
	public static final Boolean defaultFormatLeadingSpaceInComment = defaultJavaFormatLeadingSpaceInComment;

	public static final Acceptor defaultIndentHotCharsAcceptor = new Acceptor() {
		public boolean accept(char ch) {
			switch (ch) {
			case '{':
			case '}':
				return true;
			}

			return false;
		}
	};

	public static final String defaultWordMatchStaticWords = "Exception IntrospectionException FileNotFoundException IOException" // NOI18N
			+ " ArrayIndexOutOfBoundsException ClassCastException ClassNotFoundException" // NOI18N
			+ " CloneNotSupportedException NullPointerException NumberFormatException" // NOI18N
			+ " SQLException IllegalAccessException IllegalArgumentException"; // NOI18N

	public static Map getJavaAbbrevMap() {
		Map javaAbbrevMap = new TreeMap();
		javaAbbrevMap.put("sout", "System.out.println(\"|\");"); // NOI18N
		javaAbbrevMap.put("serr", "System.err.println(\"|\");"); // NOI18N

		javaAbbrevMap.put("impa", "import java.awt."); // NOI18N
		javaAbbrevMap.put("impb", "import java.beans."); // NOI18N
		javaAbbrevMap.put("impd", "import org.netbeans."); // NOI18N
		javaAbbrevMap.put("impj", "import java."); // NOI18N
		javaAbbrevMap.put("imps", "import javax.swing."); // NOI18N
		javaAbbrevMap.put("impS", "import com.sun.java.swing."); // NOI18N
		javaAbbrevMap.put("impq", "import javax.sql."); // NOI18N

		javaAbbrevMap.put("psf", "private static final "); // NOI18N
		javaAbbrevMap.put("psfi", "private static final int "); // NOI18N
		javaAbbrevMap.put("psfs", "private static final String "); // NOI18N
		javaAbbrevMap.put("psfb", "private static final boolean "); // NOI18N
		javaAbbrevMap.put("Psf", "public static final "); // NOI18N
		javaAbbrevMap.put("Psfi", "public static final int "); // NOI18N
		javaAbbrevMap.put("Psfs", "public static final String "); // NOI18N
		javaAbbrevMap.put("Psfb", "public static final boolean "); // NOI18N

		javaAbbrevMap.put("ab", "abstract "); // NOI18N
		javaAbbrevMap.put("bo", "boolean "); // NOI18N
		javaAbbrevMap.put("br", "break"); // NOI18N
		javaAbbrevMap.put("ca", "catch ("); // NOI18N
		javaAbbrevMap.put("cl", "class "); // NOI18N
		javaAbbrevMap.put("cn", "continue"); // NOI18N
		javaAbbrevMap.put("df", "default:"); // NOI18N
		javaAbbrevMap.put("ex", "extends "); // NOI18N
		javaAbbrevMap.put("fa", "false"); // NOI18N
		javaAbbrevMap.put("fi", "final "); // NOI18N
		javaAbbrevMap.put("fl", "float "); // NOI18N
		javaAbbrevMap.put("fy", "finally "); // NOI18N
		javaAbbrevMap.put("im", "implements "); // NOI18N
		javaAbbrevMap.put("ir", "import "); // NOI18N
		javaAbbrevMap.put("iof", "instanceof "); // NOI18N
		javaAbbrevMap.put("ie", "interface "); // NOI18N
		javaAbbrevMap.put("pr", "private "); // NOI18N
		javaAbbrevMap.put("pe", "protected "); // NOI18N
		javaAbbrevMap.put("pu", "public "); // NOI18N
		javaAbbrevMap.put("re", "return "); // NOI18N
		javaAbbrevMap.put("sh", "short "); // NOI18N
		javaAbbrevMap.put("st", "static "); // NOI18N
		javaAbbrevMap.put("sw", "switch ("); // NOI18N
		javaAbbrevMap.put("sy", "synchronized "); // NOI18N
		javaAbbrevMap.put("tr", "transient "); // NOI18N
		javaAbbrevMap.put("th", "throws "); // NOI18N
		javaAbbrevMap.put("tw", "throw "); // NOI18N
		javaAbbrevMap.put("twn", "throw new "); // NOI18N
		javaAbbrevMap.put("twni", "throw new InternalError();"); // NOI18N
		javaAbbrevMap.put("twne", "throw new Error();"); // NOI18N
		javaAbbrevMap.put("wh", "while ("); // NOI18N

		javaAbbrevMap.put("eq", "equals"); // NOI18N
		javaAbbrevMap.put("le", "length"); // NOI18N

		javaAbbrevMap.put("En", "Enumeration"); // NOI18N
		javaAbbrevMap.put("Ex", "Exception"); // NOI18N
		javaAbbrevMap.put("Gr", "Graphics"); // NOI18N
		javaAbbrevMap.put("Ob", "Object"); // NOI18N
		javaAbbrevMap.put("Re", "Rectangle"); // NOI18N
		javaAbbrevMap.put("St", "String"); // NOI18N
		javaAbbrevMap.put("Ve", "Vector"); // NOI18N

		javaAbbrevMap.put("pst", "printStackTrace();"); // NOI18N
		javaAbbrevMap.put("tds", "Thread.dumpStack();"); // NOI18N

		return javaAbbrevMap;
	}

	public static MultiKeyBinding[] getJavaKeyBindings() {
		return new MultiKeyBinding[] {
				new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_D, 0) },
						"macro-debug-var"),
				new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), ExtKit.commentAction),
				new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), ExtKit.uncommentAction) };
	}

	public static Map getJavaMacroMap() {
		Map javaMacroMap = new HashMap();
		javaMacroMap.put("debug-var", "select-identifier copy-to-clipboard " + "caret-up caret-end-line insert-break \"System.err.println(\\\"\""
				+ "paste-from-clipboard \" = \\\" + \" paste-from-clipboard \" );");

		return javaMacroMap;
	}

	static class JavaTokenColoringInitializer extends SettingsUtil.TokenColoringInitializer {

		Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
		Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
		Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
		Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
		Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

		Coloring commentColoring = new Coloring(italicFont, Coloring.FONT_MODE_APPLY_STYLE, new Color(0, 92, 0), null);

		Coloring numbersColoring = new Coloring(null, new Color(120, 0, 0), null);

		public JavaTokenColoringInitializer() {
			super(JavaTokenContext.context);
		}

		public Object getTokenColoring(TokenContextPath tokenContextPath, TokenCategory tokenIDOrCategory, boolean printingSet) {
			if (!printingSet) {
				switch (tokenIDOrCategory.getNumericID()) {
				case JavaTokenContext.WHITESPACE_ID:
				case JavaTokenContext.IDENTIFIER_ID:
				case JavaTokenContext.OPERATORS_ID:
					return SettingsDefaults.emptyColoring;

				case JavaTokenContext.ERRORS_ID:
					return new Coloring(null, Color.white, Color.red);

				case JavaTokenContext.KEYWORDS_ID:
					return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE, new Color(0, 0, 153), null);

				case JavaTokenContext.LINE_COMMENT_ID:
				case JavaTokenContext.BLOCK_COMMENT_ID:
					return commentColoring;

				case JavaTokenContext.CHAR_LITERAL_ID:
					return new Coloring(null, new Color(0, 111, 0), null);

				case JavaTokenContext.STRING_LITERAL_ID:
					return new Coloring(null, Color.blue, null);

				case JavaTokenContext.NUMERIC_LITERALS_ID:
					return numbersColoring;

				}

			}
			else { // printing set
				switch (tokenIDOrCategory.getNumericID()) {
				case JavaTokenContext.LINE_COMMENT_ID:
				case JavaTokenContext.BLOCK_COMMENT_ID:
					return lightGraySubst; // print fore color will be gray

				default:
					return SettingsUtil.defaultPrintColoringEvaluator;
				}

			}

			return null;

		}

	}

	static class JavaLayerTokenColoringInitializer extends SettingsUtil.TokenColoringInitializer {

		Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
		Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);

		public JavaLayerTokenColoringInitializer() {
			super(JavaLayerTokenContext.context);
		}

		public Object getTokenColoring(TokenContextPath tokenContextPath, TokenCategory tokenIDOrCategory, boolean printingSet) {
			if (!printingSet) {
				switch (tokenIDOrCategory.getNumericID()) {
				case JavaLayerTokenContext.METHOD_ID:
					return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE, null, null);

				}

			}
			else { // printing set
				switch (tokenIDOrCategory.getNumericID()) {
				case JavaLayerTokenContext.METHOD_ID:
					return italicSubst;

				default:
					return SettingsUtil.defaultPrintColoringEvaluator;
				}

			}

			return null;
		}

	}

}
