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

package org.netbeans.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Default values for the settings. They are used by
 * <tt>BaseSettingsInitializer</tt> to initialize the settings with the
 * default values. They can be also used for substitution if the value of the
 * particular setting is unacceptable.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class SettingsDefaults {

	private static final Integer INTEGER_MAX_VALUE = Integer.MAX_VALUE;

	// Caret color
	public static final Color defaultCaretColor = Color.black;

	// Empty coloring - it doesn't change font or colors
	public static final Coloring emptyColoring = new Coloring(null, null, null);

	// Default coloring
	public static final Font defaultFont = new Font("Monospaced", Font.PLAIN, 12); // NOI18N
	public static final Color defaultForeColor = Color.black;
	public static final Color defaultBackColor = Color.white;
	public static final Coloring defaultColoring = new Coloring(defaultFont, defaultForeColor, defaultBackColor);
	// line number coloring
	public static final Color defaultLineNumberForeColor = new Color(128, 64, 64);
	public static final Color defaultLineNumberBackColor = new Color(224, 224, 224);
	public static final Coloring defaultLineNumberColoring = new Coloring(null, defaultLineNumberForeColor, defaultLineNumberBackColor);
	// caret selection coloring
	public static final Color defaultSelectionForeColor = Color.white;
	public static final Color defaultSelectionBackColor = Color.lightGray;
	public static final Coloring defaultSelectionColoring = new Coloring(null, defaultSelectionForeColor, defaultSelectionBackColor);
	// Highlight search coloring
	public static final Color defaultHighlightSearchForeColor = Color.black;
	public static final Color defaultHighlightSearchBackColor = new Color(255, 255, 128);
	public static final Coloring defaultHighlightSearchColoring = new Coloring(null, defaultHighlightSearchForeColor, defaultHighlightSearchBackColor);
	// Incremental search coloring
	public static final Color defaultIncSearchForeColor = Color.black;
	public static final Color defaultIncSearchBackColor = new Color(255, 107, 138);
	public static final Coloring defaultIncSearchColoring = new Coloring(null, defaultIncSearchForeColor, defaultIncSearchBackColor);
	// Bookmark coloring
	public static final Color defaultBookmarkForeColor = Color.black;
	public static final Color defaultBookmarkBackColor = new Color(184, 230, 230);
	public static final Coloring defaultBookmarkColoring = new Coloring(null, defaultBookmarkForeColor, defaultBookmarkBackColor);
	// Guarded blocks coloring
	public static final Color defaultGuardedForeColor = null;
	public static final Color defaultGuardedBackColor = new Color(225, 236, 247);
	public static final Coloring defaultGuardedColoring = new Coloring(null, defaultGuardedForeColor, defaultGuardedBackColor);

	public static final Color defaultStatusBarForeColor = null;
	public static final Color defaultStatusBarBackColor = UIManager.getColor("ScrollPane.background"); // NOI18N
	public static final Coloring defaultStatusBarColoring = new Coloring(null, defaultStatusBarForeColor, defaultStatusBarBackColor);

	public static final Color defaultStatusBarBoldForeColor = Color.white;
	public static final Color defaultStatusBarBoldBackColor = Color.red;
	public static final Coloring defaultStatusBarBoldColoring = new Coloring(null, defaultStatusBarBoldForeColor, defaultStatusBarBoldBackColor);

	public static final Integer defaultCaretBlinkRate = 300;
	public static final Integer defaultTabSize = 8;
	public static final Integer defaultSpacesPerTab = 4;
	public static final Integer defaultShiftWidth = 4; // usually
	// not used as there's a Evaluator for shift width

	public static final Integer defaultStatusBarCaretDelay = 200;

	public static final Color defaultTextLimitLineColor = new Color(255, 235, 235);
	public static final Integer defaultTextLimitWidth = 80;

	public static final Acceptor defaultIdentifierAcceptor = AcceptorFactory.LETTER_DIGIT;
	public static final Acceptor defaultWhitespaceAcceptor = AcceptorFactory.WHITESPACE;

	public static final Float defaultLineHeightCorrection = 1.0f;

	public static final Insets defaultLineNumberMargin = new Insets(0, 3, 0, 3);
	public static final Integer defaultTextLeftMarginWidth = 2;
	public static final Insets defaultScrollJumpInsets = new Insets(-5, -10, -5, -30);
	public static final Insets defaultScrollFindInsets = new Insets(0, -0, -10, -0);
	public static final Dimension defaultComponentSizeIncrement = new Dimension(-5, -30);

	public static final Integer defaultReadBufferSize = 16384;
	public static final Integer defaultWriteBufferSize = 16384;
	public static final Integer defaultReadMarkDistance = 180;
	public static final Integer defaultMarkDistance = 100;
	public static final Integer defaultMaxMarkDistance = 150;
	public static final Integer defaultMinMarkDistance = 50;
	public static final Integer defaultSyntaxUpdateBatchSize = defaultMarkDistance.intValue() * 7;
	public static final Integer defaultLineBatchSize = 2;

	public static final Boolean defaultExpandTabs = Boolean.TRUE;

	public static final String defaultCaretTypeInsertMode = BaseCaret.LINE_CARET;
	public static final String defaultCaretTypeOverwriteMode = BaseCaret.BLOCK_CARET;
	public static final Color defaultCaretColorInsertMode = Color.black;
	public static final Color defaultCaretColorOvwerwriteMode = Color.black;
	public static final Boolean defaultCaretItalicInsertMode = Boolean.FALSE;
	public static final Boolean defaultCaretItalicOverwriteMode = Boolean.FALSE;
	public static final Acceptor defaultAbbrevExpandAcceptor = AcceptorFactory.SPACE_NL;
	public static final Acceptor defaultAbbrevAddTypedCharAcceptor = AcceptorFactory.NL;
	public static final Acceptor defaultAbbrevResetAcceptor = AcceptorFactory.NON_JAVA_IDENTIFIER;
	public static final Map defaultAbbrevMap = new HashMap();

	public static final Map defaultMacroMap = new HashMap();

	public static final Boolean defaultStatusBarVisible = Boolean.TRUE;

	public static final Boolean defaultLineNumberVisible = Boolean.TRUE;
	public static final Boolean defaultPrintLineNumberVisible = Boolean.TRUE;
	public static final Boolean defaultTextLimitLineVisible = Boolean.TRUE;
	public static final Boolean defaultHomeKeyColumnOne = Boolean.FALSE;
	public static final Boolean defaultWordMoveNewlineStop = Boolean.TRUE;
	public static final Boolean defaultInputMethodsEnabled = Boolean.TRUE;
	public static final Boolean defaultFindHighlightSearch = Boolean.TRUE;
	public static final Boolean defaultFindIncSearch = Boolean.TRUE;
	public static final Boolean defaultFindBackwardSearch = Boolean.FALSE;
	public static final Boolean defaultFindWrapSearch = Boolean.TRUE;
	public static final Boolean defaultFindMatchCase = Boolean.FALSE;
	public static final Boolean defaultFindWholeWords = Boolean.FALSE;
	public static final Boolean defaultFindRegExp = Boolean.FALSE;
	public static final Integer defaultFindHistorySize = 30;
	public static final Integer defaultWordMatchSearchLen = INTEGER_MAX_VALUE;
	public static final Boolean defaultWordMatchWrapSearch = Boolean.TRUE;
	public static final Boolean defaultWordMatchMatchOneChar = Boolean.TRUE;
	public static final Boolean defaultWordMatchMatchCase = Boolean.FALSE;
	public static final Boolean defaultWordMatchSmartCase = Boolean.FALSE;

	public static final String[] defaultColoringNames = new String[] { SettingsNames.DEFAULT_COLORING, SettingsNames.LINE_NUMBER_COLORING,
			SettingsNames.GUARDED_COLORING, SettingsNames.SELECTION_COLORING, SettingsNames.HIGHLIGHT_SEARCH_COLORING, SettingsNames.INC_SEARCH_COLORING,
			SettingsNames.BOOKMARK_COLORING, SettingsNames.STATUS_BAR_COLORING, SettingsNames.STATUS_BAR_BOLD_COLORING };

	public static final MultiKeyBinding[] defaultKeyBindings = new MultiKeyBinding[] {
			new MultiKeyBinding((KeyStroke) null, // this assigns the default
													// action to keymap
					BaseKit.defaultKeyTypedAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), BaseKit.insertBreakAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), BaseKit.insertTabAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK), BaseKit.removeTabAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), BaseKit.deletePrevCharAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.SHIFT_MASK), BaseKit.deletePrevCharAction),
			/*
			 * new MultiKeyBinding( KeyStroke.getKeyStroke(KeyEvent.VK_H,
			 * InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
			 * BaseKit.deletePrevCharAction ),
			 */new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), BaseKit.deleteNextCharAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), BaseKit.forwardAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, 0), // keypad
																					// right
					BaseKit.forwardAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK), BaseKit.selectionForwardAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK), BaseKit.nextWordAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK), BaseKit.selectionNextWordAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), BaseKit.backwardAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, 0), // keypad
																				// left
					BaseKit.backwardAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK), BaseKit.selectionBackwardAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK), BaseKit.previousWordAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK), BaseKit.selectionPreviousWordAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), BaseKit.downAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), // keypad
																				// down
					BaseKit.downAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK), BaseKit.selectionDownAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK), BaseKit.scrollUpAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), BaseKit.upAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), // keypad
																				// up
					BaseKit.upAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK), BaseKit.selectionUpAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK), BaseKit.scrollDownAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), BaseKit.pageDownAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_MASK), BaseKit.selectionPageDownAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), BaseKit.pageUpAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.SHIFT_MASK), BaseKit.selectionPageUpAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), BaseKit.beginLineAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.SHIFT_MASK), BaseKit.selectionBeginLineAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK), BaseKit.beginAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK), BaseKit.selectionBeginAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), BaseKit.endLineAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.SHIFT_MASK), BaseKit.selectionEndLineAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK), BaseKit.endAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK), BaseKit.selectionEndAction),

			// clipboard bindings
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), BaseKit.copyAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK), BaseKit.cutAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), BaseKit.pasteAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), BaseKit.copyAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK), BaseKit.cutAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.SHIFT_MASK), BaseKit.pasteAction),

			// undo and redo bindings - handled at system level
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK), BaseKit.undoAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), BaseKit.redoAction),

			// other bindings
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), BaseKit.selectAllAction),
			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), },
					BaseKit.endWordAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK), BaseKit.removeWordAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK), BaseKit.removeLineBeginAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), BaseKit.removeLineAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), BaseKit.toggleTypingModeAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_MASK), BaseKit.toggleBookmarkAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), BaseKit.gotoNextBookmarkAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), BaseKit.findNextAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_MASK), BaseKit.findPreviousAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_MASK), BaseKit.findSelectionAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK), BaseKit.toggleHighlightSearchAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK), BaseKit.wordMatchNextAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK), BaseKit.wordMatchPrevAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK), BaseKit.shiftLineRightAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK), BaseKit.shiftLineLeftAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.SHIFT_MASK), BaseKit.abbrevResetAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), BaseKit.annotationsCyclingAction),

			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), },
					BaseKit.adjustWindowTopAction),
			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), },
					BaseKit.adjustWindowCenterAction),
			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_B, 0), },
					BaseKit.adjustWindowBottomAction),

			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_MASK | InputEvent.ALT_MASK), BaseKit.adjustCaretTopAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.SHIFT_MASK | InputEvent.ALT_MASK), BaseKit.adjustCaretCenterAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.SHIFT_MASK | InputEvent.ALT_MASK), BaseKit.adjustCaretBottomAction),

			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), BaseKit.formatAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_MASK), BaseKit.selectIdentifierAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.ALT_MASK), BaseKit.jumpListPrevAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_MASK), BaseKit.jumpListNextAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.SHIFT_MASK | InputEvent.ALT_MASK), BaseKit.jumpListPrevComponentAction),
			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.SHIFT_MASK | InputEvent.ALT_MASK), BaseKit.jumpListNextComponentAction),
			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_U, 0), },
					BaseKit.toUpperCaseAction),
			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), },
					BaseKit.toLowerCaseAction),
			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), },
					BaseKit.switchCaseAction),

			new MultiKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK), BaseKit.selectNextParameterAction),

			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), },
					BaseKit.startMacroRecordingAction),

			new MultiKeyBinding(new KeyStroke[] { KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_MASK), KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), },
					BaseKit.stopMacroRecordingAction), };
}
