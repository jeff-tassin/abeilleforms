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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsChangeEvent;
import org.netbeans.editor.SettingsChangeListener;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.WeakTimerListener;

/**
 * General Completion display formatting and services
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class Completion implements PropertyChangeListener, SettingsChangeListener, ActionListener {

	/** Editor UI supporting this completion */
	protected ExtEditorUI extEditorUI;

	/** Completion query providing query support for this completion */
	private CompletionQuery query;

	/**
	 * Last result retrieved for completion. It can become null if the document
	 * was modified so the replacement position would be invalid.
	 */
	private CompletionQuery.Result lastResult;

	/** Completion view component displaying the completion help */
	private CompletionView view;

	/**
	 * Component (usually scroll-pane) holding the view and the title and
	 * possibly other necessary components.
	 */
	private CompletionPane pane;

	private boolean autoPopup;

	private int autoPopupDelay;

	private int refreshDelay;

	Timer timer;

	private DocumentListener docL;
	private CaretListener caretL;

	private PropertyChangeListener docChangeL;

	private int caretPos = -1;

	public Completion(ExtEditorUI extEditorUI) {
		this.extEditorUI = extEditorUI;

		// Initialize timer
		timer = new Timer(0, new WeakTimerListener(this)); // delay will be set
															// later
		timer.setRepeats(false);

		// Create document listener
		docL = new DocumentListener() {
			public void insertUpdate(DocumentEvent evt) {
				if (evt.getLength() > 0) {
					invalidateLastResult();
					refresh(false);
				}
			}

			public void removeUpdate(DocumentEvent evt) {
				if (evt.getLength() > 0) {
					invalidateLastResult();
					refresh(false);
				}
			}

			public void changedUpdate(DocumentEvent evt) {
			}
		};

		caretL = new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if (!isPaneVisible()) {

					// cancel timer if caret moved
					cancelRequest();
				}
				else {

					// refresh completion only if a pane is already visible
					refresh(true);
				}
			}
		};

		Settings.addSettingsChangeListener(this);

		synchronized (extEditorUI.getComponentLock()) {
			// if component already installed in ExtEditorUI simulate
			// installation
			JTextComponent component = extEditorUI.getComponent();
			if (component != null) {
				propertyChange(new PropertyChangeEvent(extEditorUI, ExtEditorUI.COMPONENT_PROPERTY, null, component));
			}

			extEditorUI.addPropertyChangeListener(this);
		}
	}

	public void settingsChange(SettingsChangeEvent evt) {
		Class kitClass = Utilities.getKitClass(extEditorUI.getComponent());

		if (kitClass != null) {
			autoPopup = SettingsUtil.getBoolean(kitClass, ExtSettingsNames.COMPLETION_AUTO_POPUP, ExtSettingsDefaults.defaultCompletionAutoPopup);

			autoPopupDelay = SettingsUtil.getInteger(kitClass, ExtSettingsNames.COMPLETION_AUTO_POPUP_DELAY,
					ExtSettingsDefaults.defaultCompletionAutoPopupDelay);

			refreshDelay = SettingsUtil.getInteger(kitClass, ExtSettingsNames.COMPLETION_REFRESH_DELAY, ExtSettingsDefaults.defaultCompletionRefreshDelay);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();

		if (ExtEditorUI.COMPONENT_PROPERTY.equals(propName)) {
			JTextComponent component = (JTextComponent) evt.getNewValue();
			if (component != null) { // just installed

				settingsChange(null);

				BaseDocument doc = Utilities.getDocument(component);
				if (doc != null) {
					doc.addDocumentListener(docL);
				}

				component.addCaretListener(caretL);
			}
			else { // just deinstalled
				component = (JTextComponent) evt.getOldValue();

				BaseDocument doc = Utilities.getDocument(component);
				if (doc != null) {
					doc.removeDocumentListener(docL);
				}

				if (component != null) {
					component.removeCaretListener(caretL);
				}
			}

		}
		else if ("document".equals(propName)) { // NOI18N
			if (evt.getOldValue() instanceof BaseDocument) {
				((BaseDocument) evt.getOldValue()).removeDocumentListener(docL);
			}
			if (evt.getNewValue() instanceof BaseDocument) {
				((BaseDocument) evt.getNewValue()).addDocumentListener(docL);
			}

		}

	}

	public CompletionPane getPane() {
		if (pane == null) {
			pane = new ScrollCompletionPane(extEditorUI);
		}
		return pane;
	}

	protected CompletionView createView() {
		return new ListCompletionView();
	}

	public final CompletionView getView() {
		if (view == null) {
			view = createView();
		}
		return view;
	}

	protected CompletionQuery createQuery() {
		return null;
	}

	public final CompletionQuery getQuery() {
		if (query == null) {
			query = createQuery();
		}
		return query;
	}

	/**
	 * Get the result of the last valid completion query or null if there's no
	 * valid result available.
	 */
	public synchronized final CompletionQuery.Result getLastResult() {
		return lastResult;
	}

	/**
	 * Reset the result of the last valid completion query. This is done for
	 * example after the document was modified.
	 */
	public synchronized final void invalidateLastResult() {
		lastResult = null;
	}

	public synchronized Object getSelectedValue() {
		if (lastResult != null) {
			int index = getView().getSelectedIndex();
			if (index >= 0) {
				return lastResult.getData().get(index);
			}
		}
		return null;
	}

	/** Return true if the completion should popup automatically */
	public boolean isAutoPopupEnabled() {
		return autoPopup;
	}

	/**
	 * Return true when the pane exists and is visible. This is the preferred
	 * method of testing the visibility of the pane instead of
	 * <tt>getPane().isVisible()</tt> that forces the creation of the pane.
	 */
	public boolean isPaneVisible() {
		return (pane != null && pane.isVisible());
	}

	/**
	 * Set the visibility of the view. This method should be used mainly for
	 * hiding the completion pane. If used with visible set to true it calls the
	 * <tt>popup(false)</tt>.
	 */
	public void setPaneVisible(boolean visible) {

		if (visible) {
			if (extEditorUI.getComponent() != null) {
				popup(false);
			}
		}
		else {
			if (pane != null) {
				cancelRequest();
				invalidateLastResult();
				pane.setVisible(false);
				caretPos = -1;
			}
		}
	}

	/**
	 * Refresh the contents of the view if it's currently visible.
	 * 
	 * @param postRequest
	 *            post the request instead of refreshing the view immediately.
	 *            The <tt>ExtSettingsNames.COMPLETION_REFRESH_DELAY</tt>
	 *            setting stores the number of milliseconds before the view is
	 *            refreshed.
	 */
	public synchronized void refresh(boolean postRequest) {
		final boolean post = postRequest;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (isPaneVisible()) {
					timer.stop();
					if (post) {
						timer.setInitialDelay(refreshDelay);
						timer.setDelay(refreshDelay);
						timer.start();
					}
					else {
						actionPerformed(null);
					}
				}
			}
		});
	}

	/**
	 * Get the help and show it in the view. If the view is already visible
	 * perform the refresh of the view.
	 * 
	 * @param postRequest
	 *            post the request instead of displaying the view immediately.
	 *            The <tt>ExtSettingsNames.COMPLETION_AUTO_POPUP_DELAY</tt>
	 *            setting stores the number of milliseconds before the view is
	 *            displayed. If the user presses a key until the delay expires
	 *            nothing is shown. This guarantees that the user which knows
	 *            what to write will not be annoyed with the unnecessary help.
	 */
	public synchronized void popup(boolean postRequest) {
		if (isPaneVisible()) {

			refresh(postRequest);
		}
		else {
			timer.stop();
			if (postRequest) {

				timer.setInitialDelay(autoPopupDelay);
				timer.setDelay(autoPopupDelay);
				timer.start();
			}
			else {
				actionPerformed(null);
			}
		}
	}

	/**
	 * Cancel last request for either displaying or refreshing the pane. It
	 * resets the internal timer.
	 */
	public synchronized void cancelRequest() {

		timer.stop();
		caretPos = -1;
	}

	/**
	 * Called to do either displaying or refreshing of the view. This method can
	 * be called either directly or because of the timer has fired.
	 * 
	 * @param evt
	 *            event describing the timer firing or null if the method was
	 *            called directly because of the synchronous showing/refreshing
	 *            the view.
	 */
	public synchronized void actionPerformed(ActionEvent evt) {
		JTextComponent component = extEditorUI.getComponent();
		BaseDocument doc = Utilities.getDocument(component);

		if (component != null && doc != null) {
			if (evt != null) {
				// AutoPopup performed, check whether the sources are prepared
				// for completion
				ExtSyntaxSupport sup = (ExtSyntaxSupport) doc.getSyntaxSupport().get(ExtSyntaxSupport.class);
				if (sup != null) {

					if (!sup.isPrepared()) {

						return;
					}
				}
			}

			try {

				if (caretPos > doc.getLength())
					caretPos = doc.getLength();

				// System.out.println( "caretPos = " + caretPos + "
				// component.getCaret().getDot() = " +
				// component.getCaret().getDot() );
				// System.out.println( " Utilities.getRowStart1 = " +
				// Utilities.getRowStart(component,component.getCaret().getDot()
				// ) );
				// if ( caretPos != -1 )
				// System.out.println( " Utilities.getRowStart2 = " +
				// Utilities.getRowStart(component,caretPos ) );

				if ((caretPos != -1) && (Utilities.getRowStart(component, component.getCaret().getDot()) != Utilities.getRowStart(component, caretPos))) {

					getPane().setVisible(false);
					caretPos = -1;
					return;
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}

			caretPos = component.getCaret().getDot();
			lastResult = getQuery().query(component, caretPos, doc.getSyntaxSupport());

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					CompletionQuery.Result res = lastResult;
					if (res != null) {
						getPane().setTitle(res.getTitle());
						getView().setResult(res);
						if (isPaneVisible()) {
							getPane().refresh();
						}
						else {
							getPane().setVisible(true);
						}
					}
					else {
						getPane().setVisible(false);
						caretPos = -1;
					}
				}
			});
		}
		else {
			System.out.println("Completion.actionPerformed null component or document ");
		}
	}

	/**
	 * Substitute the document's text with the text that is appopriate for the
	 * selection in the view. This function is usually triggered upon pressing
	 * the Enter key.
	 * 
	 * @return true if the substitution was performed false if not.
	 */
	public synchronized boolean substituteText(boolean shift) {
		if (lastResult != null) {
			int index = getView().getSelectedIndex();
			if (index >= 0) {
				lastResult.substituteText(index, shift);
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Substitute the text with the longest common part of all the entries
	 * appearing in the view. This function is usually triggered upon pressing
	 * the Tab key.
	 * 
	 * @return true if the substitution was performed false if not.
	 */
	public synchronized boolean substituteCommonText() {
		if (lastResult != null) {
			int index = getView().getSelectedIndex();
			if (index >= 0) {
				lastResult.substituteCommonText(index);
			}
			return true;
		}
		else {
			return false;
		}
	}

}
