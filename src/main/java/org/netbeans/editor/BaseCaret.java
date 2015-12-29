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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Caret implementation
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class BaseCaret extends Rectangle implements Caret, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener, DocumentListener,
		ActionListener, SettingsChangeListener {

	/** Caret type representing block covering current character */
	public static final String BLOCK_CARET = "block-caret"; // NOI18N

	/** Default caret type */
	public static final String LINE_CARET = "line-caret"; // NOI18N

	/** One dot thin line compatible with Swing default caret */
	public static final String THIN_LINE_CARET = "thin-line-caret"; // NOI18N

	private static final boolean debugCaretFocus = Boolean.getBoolean("netbeans.debug.editor.caret.focus"); // NOI18N

	/** Component this caret is bound to */
	protected JTextComponent component;

	/**
	 * Position of the caret on the screen. This helps to compute caret position
	 * on the next after jump.
	 */
	Point magicCaretPosition;

	/** Draw mark designating the position of the caret. */
	MarkFactory.DrawMark caretMark = new MarkFactory.CaretMark();

	/** Draw mark that supports caret mark in creating selection */
	MarkFactory.DrawMark selectionMark = new MarkFactory.DrawMark(DrawLayerFactory.CARET_LAYER_NAME, null);

	/** Is the caret visible */
	boolean visible;

	/**
	 * Caret is visible and the blink is visible. Both must be true in order to
	 * show the caret.
	 */
	boolean blinkVisible;

	/** Is the selection currently visible? */
	boolean selectionVisible;

	/** Listeners */
	protected EventListenerList listenerList = new EventListenerList();

	/** Timer used for blinking the caret */
	protected Timer flasher;

	/** Type of the caret */
	String type;

	/** Is the caret italic for italic fonts */
	boolean italic;

	private int xPoints[] = new int[4];
	private int yPoints[] = new int[4];
	private Action selectWordAction;
	private Action selectLineAction;

	/**
	 * Change event. Only one instance needed because it has only source
	 * property
	 */
	protected ChangeEvent changeEvent;

	private static char emptyDotChar[] = { ' ' };

	/** Dot array of one character under caret */
	protected char dotChar[] = emptyDotChar;

	private boolean overwriteMode;

	/**
	 * Remembering document on which caret listens avoids duplicate listener
	 * addition to SwingPropertyChangeSupport due to the bug 4200280
	 */
	private BaseDocument listenDoc;

	/** Caret draw graphics */
	CaretDG caretDG = new CaretDG();

	/**
	 * Font of the text underlying the caret. It can be used in caret painting.
	 */
	protected Font afterCaretFont;

	/** Font of the text right before the caret */
	protected Font beforeCaretFont;

	/**
	 * Foreground color of the text underlying the caret. It can be used in
	 * caret painting.
	 */
	protected Color textForeColor;

	/**
	 * Background color of the text underlying the caret. It can be used in
	 * caret painting.
	 */
	protected Color textBackColor;

	private transient FocusListener focusListener;

	private transient boolean nextPaintUpdate;
	private transient Rectangle nextPaintScrollRect;
	private transient int nextPaintScrollPolicy;

	static final long serialVersionUID = -9113841520331402768L;

	public BaseCaret() {
		Settings.addSettingsChangeListener(this);
	}

	/**
	 * Called when settings were changed. The method is called also in
	 * constructor, so the code must count with the evt being null.
	 */
	public void settingsChange(SettingsChangeEvent evt) {
		if (evt != null && SettingsNames.CARET_BLINK_RATE.equals(evt.getSettingName())) {
			Object value = evt.getNewValue();
			if (value instanceof Integer) {
				setBlinkRate(((Integer) value).intValue());
			}
		}
		updateType();
	}

	void updateType() {
		JTextComponent c = component;
		if (c != null) {
			Class kitClass = Utilities.getKitClass(c);
			String newType;
			boolean newItalic;
			Color caretColor;
			if (overwriteMode) {
				newType = SettingsUtil.getString(kitClass, SettingsNames.CARET_TYPE_OVERWRITE_MODE, LINE_CARET);
				newItalic = SettingsUtil.getBoolean(kitClass, SettingsNames.CARET_ITALIC_OVERWRITE_MODE, false);
				caretColor = getColor(kitClass, SettingsNames.CARET_COLOR_OVERWRITE_MODE, SettingsDefaults.defaultCaretColorOvwerwriteMode);

			}
			else { // insert mode
				newType = SettingsUtil.getString(kitClass, SettingsNames.CARET_TYPE_INSERT_MODE, LINE_CARET);
				newItalic = SettingsUtil.getBoolean(kitClass, SettingsNames.CARET_ITALIC_INSERT_MODE, false);
				caretColor = getColor(kitClass, SettingsNames.CARET_COLOR_INSERT_MODE, SettingsDefaults.defaultCaretColorInsertMode);
			}

			this.type = newType;
			this.italic = newItalic;
			c.setCaretColor(caretColor);

			dispatchUpdate();
		}
	}

	private static Color getColor(Class kitClass, String settingName, Color defaultValue) {
		Object value = Settings.getValue(kitClass, settingName);
		return (value instanceof Color) ? (Color) value : defaultValue;
	}

	/** Called when UI is being installed into JTextComponent */
	public void install(JTextComponent c) {
		component = c;
		component.addPropertyChangeListener(this);
		focusListener = new FocusHandler(this);
		component.addFocusListener(focusListener);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		EditorUI editorUI = Utilities.getEditorUI(component);
		editorUI.addLayer(new DrawLayerFactory.CaretLayer(), DrawLayerFactory.CARET_LAYER_VISIBILITY);
		caretMark.setEditorUI(editorUI);
		selectionMark.setEditorUI(editorUI);
		editorUI.addPropertyChangeListener(this);

		BaseDocument doc = Utilities.getDocument(c);
		if (doc != null) {
			modelChanged(null, doc);
		}

		if (component.hasFocus()) {
			focusGained(null); // emulate focus gained
			if (debugCaretFocus) {
				System.err.println("Component has focus, calling focusGained() on doc=" + component.getDocument().getProperty(Document.TitleProperty));
			}

		}
	}

	/** Called when UI is being removed from JTextComponent */
	public void deinstall(JTextComponent c) {
		component = null; // invalidate

		if (flasher != null) {
			setBlinkRate(0);
		}

		Utilities.getEditorUI(c).removeLayer(DrawLayerFactory.CARET_LAYER_NAME);

		c.removeMouseMotionListener(this);
		c.removeMouseListener(this);
		if (focusListener != null) {
			c.removeFocusListener(focusListener);
			focusListener = null;
		}
		c.removePropertyChangeListener(this);

		modelChanged(listenDoc, null);
	}

	protected void modelChanged(BaseDocument oldDoc, BaseDocument newDoc) {
		// [PENDING] !!! this body looks strange because of the bug 4200280
		if (oldDoc != null && listenDoc == oldDoc) {
			oldDoc.removeDocumentListener(this);

			try {
				caretMark.remove();
				selectionMark.remove();
			} catch (InvalidMarkException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			}

			listenDoc = null;
		}

		if (newDoc != null) {
			if (listenDoc != null) {
				// deinstall from the listenDoc first
				modelChanged(listenDoc, null);
			}

			newDoc.addDocumentListener(this);
			listenDoc = newDoc;

			try {
				Utilities.insertMark(newDoc, caretMark, 0);
				Utilities.insertMark(newDoc, selectionMark, 0);
			} catch (InvalidMarkException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			} catch (BadLocationException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			}

			settingsChange(null); // update settings

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateType();
				}
			});

		}
	}

	/** Renders the caret */
	public void paint(Graphics g) {
		if (nextPaintUpdate) { // need to update caret first
			// running in AWT -> no dispatching
			nextPaintUpdate = false; // no further updating
			update(nextPaintScrollRect, nextPaintScrollPolicy);

			// fix for issue #13049
			nextPaintScrollRect = null;

		}

		if (visible && blinkVisible) {
			paintCustomCaret(g);
		}
	}

	protected void paintCustomCaret(Graphics g) {
		JTextComponent c = component;
		if (c != null) {
			EditorUI editorUI = Utilities.getEditorUI(c);
			if (THIN_LINE_CARET.equals(type)) { // thin line caret
				g.setColor(c.getCaretColor());
				int upperX = x;
				if (beforeCaretFont != null && beforeCaretFont.isItalic() && italic) {
					upperX += Math.tan(beforeCaretFont.getItalicAngle()) * height;
				}
				g.drawLine((int) upperX, y, x, (y + height - 1));

			}
			else if (BLOCK_CARET.equals(type)) { // block caret
				g.setColor(c.getCaretColor());
				g.setFont(afterCaretFont);
				if (afterCaretFont.isItalic() && italic) { // paint italic
															// caret
					int upperX = (int) (x + Math.tan(afterCaretFont.getItalicAngle()) * height);
					xPoints[0] = upperX;
					yPoints[0] = y;
					xPoints[1] = upperX + width;
					yPoints[1] = y;
					xPoints[2] = x + width;
					yPoints[2] = y + height - 1;
					xPoints[3] = x;
					yPoints[3] = y + height - 1;
					g.fillPolygon(xPoints, yPoints, 4);

				}
				else { // paint non-italic caret
					g.fillRect(x, y, width, height);
				}

				if (!Character.isWhitespace(dotChar[0])) {
					g.setColor(Color.white);
					int ascent = FontMetricsCache.getFontMetrics(afterCaretFont, c).getAscent();
					g.drawChars(dotChar, 0, 1, x, y + editorUI.getLineAscent());
				}

			}
			else { // two dot line caret
				g.setColor(c.getCaretColor());
				int blkWidth = 2;
				if (beforeCaretFont != null && beforeCaretFont.isItalic() && italic) {
					int upperX = (int) (x + Math.tan(beforeCaretFont.getItalicAngle()) * height);
					xPoints[0] = upperX;
					yPoints[0] = y;
					xPoints[1] = upperX + blkWidth;
					yPoints[1] = y;
					xPoints[2] = x + blkWidth;
					yPoints[2] = y + height - 1;
					xPoints[3] = x;
					yPoints[3] = y + height - 1;
					g.fillPolygon(xPoints, yPoints, 4);
				}
				else { // paint non-italic caret
					g.fillRect(x, y, blkWidth, height - 1);
				}
			}
		}
	}

	/** Update the caret's visual position */
	void dispatchUpdate() {
		dispatchUpdate(null, EditorUI.SCROLL_MOVE);
	}

	void dispatchUpdate(final Rectangle scrollRect, final int scrollPolicy) {
		JTextComponent c = component;
		EditorUI editorUI = Utilities.getEditorUI(c);
		if (!editorUI.isFontsInited()) { // fonts not yet initialized
			if (scrollRect != null) { // do not hide the "really" scrolling
										// requests
				nextPaintScrollRect = scrollRect;
			}
			nextPaintScrollPolicy = scrollPolicy;
			nextPaintUpdate = true;
			return;
		}

		/*
		 * part of fix of #18860 - Using runInEventDispatchThread() in AWT
		 * thread means that the code is executed immediately which can lead to
		 * problems once the insert/remove in document is performed because the
		 * update() uses views to find out the visual position and if the views
		 * doc listener is added AFTER the caret's listener then the views are
		 * not updated yet. Using SwingUtilities.invokeLater() should solve the
		 * problem although the view extent could flip once the extent would be
		 * explicitely scrolled to area that does not cover the caret's
		 * rectangle. It needs to be tested so that it does not happen.
		 */
		// Utilities.runInEventDispatchThread(
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JTextComponent c2 = component;
				if (c2 != null) {
					BaseDocument doc = Utilities.getDocument(c2);
					if (doc != null) {
						doc.readLock();
						try {
							update(scrollRect, scrollPolicy);
						} finally {
							doc.readUnlock();
						}
					}
				}

			}
		});
	}

	/**
	 * Update the caret. The document is read-locked while calling this method.
	 * 
	 * @param scrollRect
	 *            rectangle that should be visible after the updating of the
	 *            caret. It can be null to do no scrolling (only update caret)
	 *            or it can be caret rectangle to update the caret and make it
	 *            visible or some other rectangle to guarantee that the
	 *            rectangle will be visible.
	 * @param scrollPolicy
	 *            scrolling policy as defined in EditorUI. It has no meaning if
	 *            <code>scrollRect</code> is null.
	 */
	protected void update(Rectangle scrollRect, int scrollPolicy) {
		JTextComponent c = component;
		if (c != null) {
			BaseTextUI ui = (BaseTextUI) c.getUI();
			EditorUI editorUI = ui.getEditorUI();
			BaseDocument doc = Utilities.getDocument(c);
			if (doc != null) {
				Rectangle oldCaretRect = new Rectangle(this);
				if (italic) { // caret is italic - add char height to the
								// width of the rect
					oldCaretRect.width += oldCaretRect.height;
				}

				int dot = getDot();
				try {
					ui.modelToViewDG(dot, caretDG);
				} catch (BadLocationException e) {
					if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
						e.printStackTrace();
					}
				}
				resetBlink();

				if (scrollRect == null || !editorUI.scrollRectToVisibleFragile(scrollRect, scrollPolicy)) {
					oldCaretRect.add(this); // adds the NEW caret rect -
											// important!
					c.repaint(oldCaretRect);
				}
			}
		}
	}

	/**
	 * Redefine to Object.equals() to prevent defaulting to Rectangle.equals()
	 * which would cause incorrect firing
	 */
	public boolean equals(Object o) {
		return (this == o);
	}

	/** Adds listener to track when caret position was changed */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	/** Removes listeners to caret position changes */
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	/** Notifies listeners that caret position has changed */
	protected void fireStateChanged() {
		Object listeners[] = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	/** Is the caret currently visible */
	public final boolean isVisible() {
		return visible;
	}

	protected void setVisibleImpl(boolean v) {
		synchronized (this) {
			Timer t = flasher;
			if (t != null) {
				if (visible) {
					t.stop();
				}
				if (v) {
					t.start();
				}
				else {
					t.stop();
				}
			}
			visible = v;
		}
		JTextComponent c = component;
		if (c != null) {
			Rectangle repaintRect = this;
			if (italic) {
				repaintRect = new Rectangle(this);
				repaintRect.width += repaintRect.height;
			}
			c.repaint(repaintRect);
		}
	}

	synchronized void resetBlink() {
		Timer t = flasher;
		if (t != null) {
			t.stop();
			blinkVisible = true;
			if (isVisible()) {
				t.start();
			}
		}
	}

	/** Sets the caret visibility */
	public void setVisible(final boolean v) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisibleImpl(v);
			}
		});
	}

	/** Is the selection visible? */
	public final boolean isSelectionVisible() {
		return selectionVisible;
	}

	/** Sets the selection visibility */
	public void setSelectionVisible(boolean v) {
		if (selectionVisible == v) {
			return;
		}
		JTextComponent c = component;
		if (c != null) {
			selectionVisible = v;
			if (selectionVisible) {
				int caretPos = getDot();
				int selPos = getMark();
				boolean selMarkFirst = (selPos < caretPos);
				selectionMark.activateLayer = selMarkFirst;
				caretMark.activateLayer = !selMarkFirst && !(selPos == caretPos);
			}
			else { // make selection invisible
				caretMark.activateLayer = false;
				selectionMark.activateLayer = false;
			}

			// repaint the block
			BaseTextUI ui = (BaseTextUI) c.getUI();
			try {
				ui.getEditorUI().repaintBlock(caretMark.getOffset(), selectionMark.getOffset());
			} catch (BadLocationException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			} catch (InvalidMarkException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Saves the current caret position. This is used when caret up or down
	 * actions occur, moving between lines that have uneven end positions.
	 * 
	 * @param p
	 *            the Point to use for the saved position
	 */
	public void setMagicCaretPosition(Point p) {
		magicCaretPosition = p;
	}

	/** Get position used to mark begining of the selected block */
	public final Point getMagicCaretPosition() {
		return magicCaretPosition;
	}

	/**
	 * Sets the caret blink rate.
	 * 
	 * @param rate
	 *            blink rate in milliseconds, 0 means no blink
	 */
	public synchronized void setBlinkRate(int rate) {
		if (flasher == null && rate > 0) {
			flasher = new Timer(rate, new WeakTimerListener(this));
		}
		if (flasher != null) {
			if (rate > 0) {
				if (flasher.getDelay() != rate) {
					flasher.setDelay(rate);
				}
			}
			else { // zero rate - don't blink
				flasher.stop();
				flasher.removeActionListener(this);
				flasher = null;
			}
		}
	}

	/** Returns blink rate of the caret or 0 if caret doesn't blink */
	public synchronized int getBlinkRate() {
		return (flasher != null) ? flasher.getDelay() : 0;
	}

	/** Gets the current position of the caret */
	public int getDot() {
		if (component != null) {
			try {
				return caretMark.getOffset();
			} catch (InvalidMarkException e) {
			}
		}
		return 0;
	}

	/**
	 * Gets the current position of the selection mark. If there's a selection
	 * this position will be different from the caret position.
	 */
	public int getMark() {
		if (component != null) {
			if (selectionVisible) {
				try {
					return selectionMark.getOffset();
				} catch (InvalidMarkException e) {
				}
			}
			else { // selection not visible
				return getDot(); // must return same position as dot
			}
		}
		return 0;
	}

	public void setDot(int offset) {
		setDot(offset, this, EditorUI.SCROLL_DEFAULT);
	}

	/**
	 * Sets the caret position to some position. This causes removal of the
	 * active selection.
	 */
	public void setDot(int offset, Rectangle scrollRect, int scrollPolicy) {
		JTextComponent c = component;
		if (c != null) {
			setSelectionVisible(false);
			BaseDocument doc = (BaseDocument) c.getDocument();
			if (doc != null) {
				try {
					Utilities.moveMark(doc, caretMark, offset);
				} catch (BadLocationException e) {
					// setting the caret to wrong position leaves it at current
					// position
				} catch (InvalidMarkException e) {
					// Caret not installed or inside the initial-read
				}
			}
			fireStateChanged();
			dispatchUpdate(scrollRect, scrollPolicy);
		}
	}

	public void moveDot(int offset) {
		moveDot(offset, this, EditorUI.SCROLL_MOVE);
	}

	/** Makes selection by moving dot but leaving mark */
	public void moveDot(int offset, Rectangle scrollRect, int scrollPolicy) {
		JTextComponent c = component;
		if (c != null) {
			BaseDocument doc = (BaseDocument) c.getDocument();
			try {
				int oldCaretPos = getDot();
				if (offset == oldCaretPos) { // no change
					return;
				}
				int selPos; // current position of selection mark

				if (selectionVisible) {
					selPos = selectionMark.getOffset();
				}
				else {
					Utilities.moveMark(doc, selectionMark, oldCaretPos);
					selPos = oldCaretPos;
				}

				Utilities.moveMark(doc, caretMark, offset);
				if (selectionVisible) { // selection already visible
					boolean selMarkFirst = (selPos < offset);
					selectionMark.activateLayer = selMarkFirst;
					caretMark.activateLayer = !selMarkFirst && !(selPos == offset);
					Utilities.getEditorUI(c).repaintBlock(oldCaretPos, offset);
					if (selPos == offset) { // same positions -> invisible
											// selection
						setSelectionVisible(false);
					}

				}
				else { // selection not yet visible
					setSelectionVisible(true);
				}
			} catch (BadLocationException e) {
				// position is incorrect
			} catch (InvalidMarkException e) {
				if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
					e.printStackTrace();
				}
			}
			fireStateChanged();
			dispatchUpdate(scrollRect, scrollPolicy);
		}
	}

	// DocumentListener methods
	public void insertUpdate(DocumentEvent evt) {
		JTextComponent c = component;
		if (c != null) {
			BaseDocument doc = (BaseDocument) component.getDocument();
			BaseDocumentEvent bevt = (BaseDocumentEvent) evt;
			if ((bevt.isInUndo() || bevt.isInRedo()) && component == Utilities.getLastActiveComponent()) {
				// in undo mode and current component
				setDot(evt.getOffset() + evt.getLength());
			}
			else {
				fireStateChanged();
				if (evt.getLength() == 0) {
					updateType();
					setVisible(false);
					setVisible(c.isEnabled() && c.hasFocus());
				}

				// Scroll to caret only for component with focus
				dispatchUpdate(c.hasFocus() ? this : null, EditorUI.SCROLL_MOVE);
			}
		}
	}

	public void removeUpdate(DocumentEvent evt) {
		JTextComponent c = component;
		if (c != null) {
			BaseDocument doc = (BaseDocument) c.getDocument();
			// make selection invisible if removal shrinked block to zero size
			if (selectionVisible && (getDot() == getMark())) {
				setSelectionVisible(false);
			}

			BaseDocumentEvent bevt = (BaseDocumentEvent) evt;
			if ((bevt.isInUndo() || bevt.isInRedo()) && c == Utilities.getLastActiveComponent()) {
				// in undo mode and current component
				setDot(evt.getOffset());
			}
			else {
				fireStateChanged();
				// Scroll to caret only for component with focus
				dispatchUpdate(c.hasFocus() ? this : null, EditorUI.SCROLL_MOVE);
			}
		}
	}

	public void changedUpdate(DocumentEvent evt) {
		dispatchUpdate();
	}

	// FocusListener methods
	public void focusGained(FocusEvent evt) {
		if (debugCaretFocus) {
			System.err.println("BaseCaret.focusGained() in doc=" + component.getDocument().getProperty(Document.TitleProperty));
		}

		JTextComponent c = component;
		if (c != null) {
			updateType();
			setVisible(c.isEnabled()); // invisible caret if disabled
		}
	}

	public void focusLost(FocusEvent evt) {
		if (debugCaretFocus) {
			System.err.println("BaseCaret.focusLost() in doc=" + component.getDocument().getProperty(Document.TitleProperty));
		}

		setVisible(false);
	}

	// MouseListener methods
	public void mouseClicked(MouseEvent evt) {
		JTextComponent c = component;
		if (c != null) {
			if (SwingUtilities.isLeftMouseButton(evt)) {
				if (evt.getClickCount() == 2) {
					if (selectWordAction == null) {
						BaseTextUI ui = (BaseTextUI) c.getUI();
						selectWordAction = ((BaseKit) ui.getEditorKit(c)).getActionByName(BaseKit.selectWordAction);
					}
					selectWordAction.actionPerformed(null);
				}
				else if (evt.getClickCount() == 3) {
					if (selectLineAction == null) {
						BaseTextUI ui = (BaseTextUI) c.getUI();
						selectLineAction = ((BaseKit) ui.getEditorKit(c)).getActionByName(BaseKit.selectLineAction);
					}
					selectLineAction.actionPerformed(null);
				}
			}
		}
	}

	public void mousePressed(MouseEvent evt) {
		JTextComponent c = component;
		if (c != null) {
			Utilities.getEditorUI(c).getWordMatch().clear(); // [PENDING]
																// should be
																// done cleanly

			// Position the cursor at the appropriate place in the document
			if ((SwingUtilities.isLeftMouseButton(evt) && (evt.getModifiers() & (InputEvent.META_MASK | InputEvent.ALT_MASK)) == 0) || !isSelectionVisible()) {
				int offset = ((BaseTextUI) c.getUI()).viewToModel(c, evt.getX(), evt.getY());
				if (offset >= 0) {
					if ((evt.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
						moveDot(offset);
					}
					else {
						setDot(offset);
					}
					setMagicCaretPosition(null);
				}
				if (c.isEnabled()) {
					c.requestFocus();
				}
			}
		}
	}

	public void mouseReleased(MouseEvent evt) {
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	// MouseMotionListener methods
	public void mouseDragged(MouseEvent evt) {
		JTextComponent c = component;
		if (SwingUtilities.isLeftMouseButton(evt)) {
			if (c != null) {
				int offset = ((BaseTextUI) c.getUI()).viewToModel(c, evt.getX(), evt.getY());
				// fix for #15204
				if (offset == -1)
					offset = 0;
				moveDot(offset);
			}
		}
	}

	public void mouseMoved(MouseEvent evt) {
	}

	// PropertyChangeListener methods
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if ("document".equals(propName)) {
			BaseDocument oldDoc = (evt.getOldValue() instanceof BaseDocument) ? (BaseDocument) evt.getOldValue() : null;
			BaseDocument newDoc = (evt.getNewValue() instanceof BaseDocument) ? (BaseDocument) evt.getNewValue() : null;
			modelChanged(oldDoc, newDoc);
		}
		else if (EditorUI.OVERWRITE_MODE_PROPERTY.equals(propName)) {
			Boolean b = (Boolean) evt.getNewValue();
			overwriteMode = (b != null) ? b.booleanValue() : false;
			updateType();
		}
	}

	// ActionListener methods
	/** Fired when blink timer fires */
	public void actionPerformed(ActionEvent evt) {
		JTextComponent c = component;
		if (c != null) {
			blinkVisible = !blinkVisible;
			Rectangle repaintRect = this;
			if (italic) {
				repaintRect = new Rectangle(this);
				repaintRect.width += repaintRect.height;
			}
			c.repaint(repaintRect);
		}
	}

	/**
	 * Caret draw graphics used to update the caret position and the character
	 * the caret sits on.
	 */
	final class CaretDG extends DrawGraphics.SimpleDG {

		Font previousFont;

		public void setFont(Font font) {
			previousFont = getFont(); // get the current font before change
			super.setFont(font);
		}

		public boolean targetOffsetReached(int offset, char ch, int x, int charWidth, DrawContext ctx) {

			JTextComponent c = BaseCaret.this.component;
			if (c != null) {
				BaseCaret.this.beforeCaretFont = (offset == ctx.getFragmentOffset()) ? previousFont : ctx.getFont();
				BaseCaret.this.afterCaretFont = ctx.getFont();

				BaseCaret.this.x = x;
				BaseCaret.this.y = this.getY();
				BaseCaret.this.width = charWidth;
				BaseCaret.this.height = Utilities.getEditorUI(c).getLineHeight();
				BaseCaret.this.textForeColor = ctx.getForeColor();
				BaseCaret.this.textBackColor = ctx.getBackColor();
				BaseCaret.this.dotChar[0] = ch;
			}
			return false;
		}

	}

	private static class FocusHandler implements FocusListener {
		private transient FocusListener fl;

		FocusHandler(FocusListener fl) {
			this.fl = fl;
		}

		public void focusGained(FocusEvent e) {
			fl.focusGained(e);
		}

		public void focusLost(FocusEvent e) {
			fl.focusLost(e);
		}
	}

}
