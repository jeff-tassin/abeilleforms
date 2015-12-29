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

package org.netbeans.editor.ext;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsChangeEvent;
import org.netbeans.editor.SettingsChangeListener;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.Utilities;

/**
 * Pane displaying the completion view and accompanying components like label
 * for title etc.
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class ScrollCompletionPane extends JScrollPane implements CompletionPane, PropertyChangeListener, SettingsChangeListener {

	/** Additional dimension increase */
	private static final Dimension PLUS_SIZE = new Dimension(20, 20);

	/** Reserved space around the caret */
	private static final int CARET_THRESHOLD = 5;

	private ExtEditorUI extEditorUI;

	private JComponent view;

	private JLabel topLabel;

	private Dimension minSize;

	private Dimension maxSize;

	private FocusListener focusL;

	private ViewMouseListener viewMouseL;

	private Dimension scrollBarSize;

	public ScrollCompletionPane(ExtEditorUI extEditorUI) {
		this.extEditorUI = extEditorUI;

		// Compute size of the scrollbars
		Dimension smallSize = getPreferredSize();
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		scrollBarSize = getPreferredSize();
		scrollBarSize.width -= smallSize.width;
		scrollBarSize.height -= smallSize.height;
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

		// Make it invisible initially
		super.setVisible(false);

		// Add the title component
		installTitleComponent();

		// Add the completion view
		CompletionView completionView = extEditorUI.getCompletion().getView();
		if (completionView instanceof JComponent) {
			view = (JComponent) completionView;
			setViewportView(view);
		}

		// Prevent the bug with displaying without the scrollbar
		getViewport().setMinimumSize(new Dimension(4, 4));

		Settings.addSettingsChangeListener(this);

		focusL = new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (isVisible()) {
							JTextComponent component = ScrollCompletionPane.this.extEditorUI.getComponent();
							if (component != null) {
								java.awt.Window w = SwingUtilities.windowForComponent(component);
								Component focusOwner = (w == null) ? null : w.getFocusOwner();
								if (focusOwner == view) {
									// Forwarding focus back to EditorPane
									component.requestFocus();
								}
								else if (focusOwner != component) {
									setVisible(false); // both JC and component
														// don't own the focus
								}
							}
						}
					}
				});
			}
		};

		viewMouseL = new ViewMouseListener();
		synchronized (extEditorUI.getComponentLock()) {
			// if component already installed in ExtEditorUI simulate
			// installation
			JTextComponent component = extEditorUI.getComponent();
			if (component != null) {
				propertyChange(new PropertyChangeEvent(extEditorUI, ExtEditorUI.COMPONENT_PROPERTY, null, component));
			}

			extEditorUI.addPropertyChangeListener(this);
		}

		putClientProperty("HelpID", ScrollCompletionPane.class.getName()); // !!!
																			// NOI18N

	}

	public void settingsChange(SettingsChangeEvent evt) {
		Class kitClass = Utilities.getKitClass(extEditorUI.getComponent());

		if (kitClass != null) {
			minSize = (Dimension) SettingsUtil.getValue(kitClass, ExtSettingsNames.COMPLETION_PANE_MIN_SIZE, ExtSettingsDefaults.defaultCompletionPaneMinSize);
			maxSize = (Dimension) SettingsUtil.getValue(kitClass, ExtSettingsNames.COMPLETION_PANE_MAX_SIZE, ExtSettingsDefaults.defaultCompletionPaneMaxSize);
		}

	}

	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();

		if (ExtEditorUI.COMPONENT_PROPERTY.equals(propName)) {
			if (evt.getNewValue() != null) { // just installed
				JTextComponent component = extEditorUI.getComponent();

				settingsChange(null);

				if (view != null) {
					// Register escape key
					BaseKit kit = Utilities.getKit(component);
					view.registerKeyboardAction(kit.getActionByName(ExtKit.completionHideAction), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
							JComponent.WHEN_FOCUSED);
					// Add mouse listener
					view.addMouseListener(viewMouseL);
				}

				component.addFocusListener(focusL);

				installToRootPane(component);
			}
			else { // just deinstalled
				JTextComponent component = (JTextComponent) evt.getOldValue();

				if (view != null) {
					// Unregister Escape key
					view.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
					view.removeMouseListener(viewMouseL);
				}

				component.removeFocusListener(focusL);

				removeFromRootPane();
			}

		}
	}

	private void installToRootPane(JTextComponent component) {
		JRootPane rp = component.getRootPane();
		if (rp != null) {
			rp.getLayeredPane().add(this, JLayeredPane.POPUP_LAYER, 0);
		}
	}

	private void removeFromRootPane() {
		JRootPane rp = getRootPane();
		if (rp != null) {
			rp.getLayeredPane().remove(this);
		}
	}

	/** Set the pane to be visible. */
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			checkRootPane();
			refresh();
		}
		else { // making invisible
			JTextComponent component = extEditorUI.getComponent();
			if (component != null) {
				component.requestFocus();
			}
		}
	}

	private void checkRootPane() {
		JTextComponent component = extEditorUI.getComponent();
		if (component != null) {
			if (component.getRootPane() != getRootPane()) {
				removeFromRootPane();
				installToRootPane(component);
			}
		}
	}

	public void refresh() {
		if (view instanceof JList) {
			JList listView = (JList) view;
			listView.ensureIndexIsVisible(listView.getSelectedIndex());
		}

		SwingUtilities.invokeLater( // !!! ? is it needed
				new Runnable() {
					public void run() {
						if (isShowing()) { // #18810
							Rectangle bounds = getPreferredBounds();
							setBounds(bounds);
							revalidate();
						}
					}
				});
	}

	/** Set the title of the pane according to the completion query results. */
	public void setTitle(String title) {
		topLabel.setText(title);
	}

	protected void installTitleComponent() {
		topLabel = new JLabel();
		topLabel.setForeground(Color.blue);
		topLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		setColumnHeaderView(topLabel);
	}

	protected Dimension getTitleComponentPreferredSize() {
		return topLabel.getPreferredSize();
	}

	protected Rectangle getPreferredBounds() {
		Rectangle ret = null;

		if (view != null) {
			JTextComponent component = extEditorUI.getComponent();
			Rectangle extBounds = extEditorUI.getExtentBounds();
			Rectangle caretRect = (Rectangle) component.getCaret();

			// Compute available height above the caret
			int aboveCaretY = caretRect.y - CARET_THRESHOLD;
			int aboveCaretHeight = aboveCaretY - extBounds.y;

			// Compute available height below the caret
			int belowCaretY = caretRect.y + caretRect.height + CARET_THRESHOLD;
			int belowCaretHeight = (extBounds.y + extBounds.height) - belowCaretY;

			// Compute a maximum size the pane can occupy
			int maxWidth = Math.min(extBounds.width, maxSize.width);
			int maxHeight = Math.min(Math.max(aboveCaretHeight, belowCaretHeight), maxSize.height);

			// Compute preferred size of pane
			Dimension ps = getPreferredSize();

			/*
			 * Add size of the vertical scrollbar by default. This could be
			 * improved to be done only if the height exceeds the bounds.
			 */
			ps.width += scrollBarSize.width;
			ps.width = Math.max(Math.max(ps.width, minSize.width), getTitleComponentPreferredSize().width);

			if (ps.width > maxWidth) {
				ps.width = maxWidth;
				ps.height += scrollBarSize.height; // will show horizontal
													// scrollbar
			}

			ps.height = Math.min(Math.max(ps.height, minSize.height), maxHeight);

			ret = new Rectangle(ps);

			// issue 12763
			// Check the position of the JTextComponent from the left top
			// corner of the window. There might be toolbar or glyph gutter
			// and these sizes must be added to the final position of the
			// code completion
			Point correction = new Point();
			// compare the position of the JViewport (parent of
			// the JTextComponent) with the root pane
			correction.y = (component.getParent().getLocationOnScreen().y - component.getRootPane().getLocationOnScreen().y) - 1;
			correction.x = (component.getParent().getLocationOnScreen().x - component.getRootPane().getLocationOnScreen().x) - 1;

			ret.x = Math.min((caretRect.x - extBounds.x) + correction.x, extBounds.width - ret.width);

			// Now choose whether display the pane either above or below the
			// caret
			if (ret.height <= belowCaretHeight) { // display below caret
				ret.y = belowCaretY - extBounds.y;
			}
			else { // display above caret
				ret.y = aboveCaretY - ret.height - extBounds.y;
			}
			ret.y += correction.y;
		}

		return ret;
	}

	class ViewMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent evt) {
			if (SwingUtilities.isLeftMouseButton(evt)) {
				JTextComponent component = extEditorUI.getComponent();
				if (component != null && evt.getClickCount() == 2) {
					BaseKit kit = Utilities.getKit(component);
					if (kit != null) {
						Action a = kit.getActionByName(BaseKit.insertBreakAction);
						if (a != null) {
							a.actionPerformed(new ActionEvent(component, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
						}
					}
				}
			}
		}
	}

}
