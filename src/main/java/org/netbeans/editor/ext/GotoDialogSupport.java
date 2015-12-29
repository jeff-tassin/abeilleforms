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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.DialogSupport;
import org.netbeans.editor.EditorState;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.Utilities;

/**
 * Support for displaying goto dialog
 * 
 * @author Miloslav Metelka, Petr Nejedly
 * @version 1.00
 */

public class GotoDialogSupport implements ActionListener {

	/** The EditorSettings key storing the last location of the dialog. */
	private static final String BOUNDS_KEY = "GotoDialogSupport.bounds-goto-line"; // NOI18N

	private JButton[] gotoButtons;
	private GotoDialogPanel gotoPanel;
	private static Dialog gotoDialog;

	public GotoDialogSupport() {
		JButton gotoButton = new JButton(LocaleSupport.getString("goto-button-goto")); // NOI18N
		JButton cancelButton = new JButton(LocaleSupport.getString("goto-button-cancel")); // NOI18N
		gotoButton.getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_goto-button-goto")); // NOI18N
		cancelButton.getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_goto-button-cancel")); // NOI18N
		/*
		 * gotoButton.setMnemonic(
		 * LocaleSupport.getChar("goto-button-goto-mnemonic", 'G') ); //NOI18N
		 */

		gotoButtons = new JButton[] { gotoButton, cancelButton };
		gotoPanel = new GotoDialogPanel();

		gotoPanel.getGotoCombo().getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent evt) {
			}

			public void keyReleased(KeyEvent evt) {
			}

			public void keyTyped(KeyEvent evt) {
				if (evt.getKeyChar() == '\n') {
					actionPerformed(new ActionEvent(gotoButtons[0], 0, null));
				}
			}
		});

	}

	protected synchronized Dialog createGotoDialog() {
		if (gotoDialog == null) {
			gotoDialog = DialogSupport.createDialog(LocaleSupport.getString("goto-title"), // NOI18N
					gotoPanel, false, // non-modal
					gotoButtons, false, // sidebuttons,
					0, // defaultIndex = 0 => gotoButton
					1, // cancelIndex = 1 => cancelButton
					this // listener
					);

			gotoDialog.pack();

			// Position the dialog according to the history
			Rectangle lastBounds = (Rectangle) EditorState.get(BOUNDS_KEY);
			if (lastBounds != null) {
				gotoDialog.setBounds(lastBounds);
			}
			else { // no history, center it on the screen
				Dimension dim = gotoDialog.getPreferredSize();
				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
				int x = Math.max(0, (screen.width - dim.width) / 2);
				int y = Math.max(0, (screen.height - dim.height) / 2);
				gotoDialog.setLocation(x, y);
			}

			return gotoDialog;
		}
		else {
			gotoDialog.toFront();
			return null;
		}
	}

	protected synchronized void disposeGotoDialog() {
		if (gotoDialog != null) {
			EditorState.put(BOUNDS_KEY, gotoDialog.getBounds());
			gotoDialog.dispose();
			Utilities.returnFocus();
		}

		gotoDialog = null;
	}

	public void showGotoDialog() {
		Dialog dialog = createGotoDialog();
		if (dialog == null) { // already visible
			// TODO:beep()
			return;
		}
		gotoPanel.popupNotify();
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();
		if (src == gotoButtons[0] || src == gotoPanel) { // Find button
			if (performGoto()) {
				gotoPanel.updateHistory(); // A.N.: support for history
				disposeGotoDialog();
			}
		}
		else { // Cancel button
			disposeGotoDialog();
		}
	}

	/**
	 * Perform the goto operation.
	 * 
	 * @return whether the dialog should be made invisible or not
	 */
	protected boolean performGoto() {
		JTextComponent c = Utilities.getLastActiveComponent();
		if (c != null) {
			try {
				int line = Integer.parseInt((String) gotoPanel.getValue());

				BaseDocument doc = Utilities.getDocument(c);
				if (doc != null) {
					// Obtain the offset where to jump
					int pos = Utilities.getRowStartFromLineOffset(doc, line - 1);

					BaseKit kit = Utilities.getKit(c);
					if (kit != null) {
						Action a = kit.getActionByName(ExtKit.gotoAction);
						if (a instanceof ExtKit.GotoAction) {
							pos = ((ExtKit.GotoAction) a).getOffsetFromLine(doc, line - 1);
						}
					}

					if (pos != -1) {
						c.getCaret().setDot(pos);
					}
					else {
						c.getToolkit().beep();
						return false;
					}
				}
			} catch (NumberFormatException e) {
				c.getToolkit().beep();
				return false;
			}
		}
		return true;
	}

}
