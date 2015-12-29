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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

/**
 * Keymap that is capable to work with MultiKeyBindings
 * 
 * @author Miloslav Metelka
 * @version 0.10
 */

public class MultiKeymap implements Keymap {

	/** Action that does nothing */
	public static final Action EMPTY_ACTION = new AbstractAction() {
		public void actionPerformed(ActionEvent evt) {
		}
	};

	/** Action that beeps. Used for wrong shortcut by default */
	public static final Action BEEP_ACTION = new DefaultEditorKit.BeepAction();

	/** JTextComponent.DefaultKeymap to be used for processing by this keymap */
	private Keymap delegate;

	/** Context keymap or null for base context */
	private Keymap context;

	/** Ignore possible keyTyped events after context reset */
	private boolean ignoreNextTyped = false;

	/**
	 * Action to return when there's no action for incoming key in some context.
	 * This action doesn't occur when no action is found in base context.
	 */
	private Action contextKeyNotFoundAction = BEEP_ACTION;

	/**
	 * Construct new keymap.
	 * 
	 * @param name
	 *            name of new keymap
	 */
	public MultiKeymap(String name) {
		delegate = JTextComponent.addKeymap(name, null);
	}

	/** Set the context keymap */
	void setContext(Keymap contextKeymap) {
		context = contextKeymap;
	}

	/** Reset keymap to base context */
	public void resetContext() {
		context = null;
	}

	/** What to do when key is not resolved for context */
	public void setContextKeyNotFoundAction(Action a) {
		contextKeyNotFoundAction = a;
	}

	/**
	 * Loads the key to action mappings into this keymap in similar way as
	 * JTextComponent.loadKeymap() does. This method is able to handle
	 * MultiKeyBindings but for compatibility it expects
	 * JTextComponent.KeyBinding array.
	 */
	public void load(JTextComponent.KeyBinding[] bindings, Action[] actions) {
		Map h = new HashMap(bindings.length);
		// add actions to map to resolve by names quickly
		for (int i = 0; i < actions.length; i++) {
			Action a = actions[i];
			String value = (String) a.getValue(Action.NAME);
			h.put((value != null ? value : ""), a); // NOI18N
		}
		load(bindings, h);
	}

	/**
	 * Loads key to action mappings into this keymap
	 * 
	 * @param bindings
	 *            array of bindings
	 * @param actions
	 *            map of [action_name, action] pairs
	 */
	public void load(JTextComponent.KeyBinding[] bindings, Map actions) {
		// now create bindings in keymap(s)
		for (int i = 0; i < bindings.length; i++) {
			Action a = (Action) actions.get(bindings[i].actionName);
			if (a != null) {
				boolean added = false;
				if (bindings[i] instanceof MultiKeyBinding) {
					MultiKeyBinding mb = (MultiKeyBinding) bindings[i];
					if (mb.keys != null) {
						Keymap cur = delegate;
						for (int j = 0; j < mb.keys.length; j++) {
							if (j == mb.keys.length - 1) { // last keystroke in
															// sequence
								cur.addActionForKeyStroke(mb.keys[j], a);
							}
							else { // not the last keystroke
								Action sca = cur.getAction(mb.keys[j]);
								if (!(sca instanceof KeymapSetContextAction)) {
									sca = new KeymapSetContextAction(JTextComponent.addKeymap(null, null));
									cur.addActionForKeyStroke(mb.keys[j], sca);
								}
								cur = ((KeymapSetContextAction) sca).contextKeymap;
							}
						}
						added = true;
					}
				}
				if (!added) {
					if (bindings[i].key != null) {
						delegate.addActionForKeyStroke(bindings[i].key, a);
					}
					else { // key is null -> set default action
						setDefaultAction(a);
					}
				}
			}
		}
	}

	public String getName() {
		return (context != null) ? context.getName() : delegate.getName();
	}

	/**
	 * Get default action of this keymap or parent keymap if this one doesn't
	 * have one. Context keymap can have default action but it will be not used.
	 */
	public Action getDefaultAction() {
		return delegate.getDefaultAction();
	}

	public void setDefaultAction(Action a) {
		if (context != null) {
			context.setDefaultAction(a);
		}
		else {
			delegate.setDefaultAction(a);
		}
	}

	Action getActionImpl(KeyStroke key) {
		Action a = null;
		if (context != null) {
			a = context.getAction(key);
			if (a == null) { // possibly ignore modifier keystrokes
				switch (key.getKeyCode()) {
				case KeyEvent.VK_SHIFT:
				case KeyEvent.VK_CONTROL:
				case KeyEvent.VK_ALT:
				case KeyEvent.VK_META:
					return EMPTY_ACTION;
				}
				if (key.isOnKeyRelease() || (key.getKeyChar() != 0 && key.getKeyChar() != KeyEvent.CHAR_UNDEFINED)) {
					return EMPTY_ACTION; // ignore releasing and typed events
				}
			}
		}
		else {
			a = delegate.getAction(key);
		}

		return a;
	}

	public Action getAction(KeyStroke key) {

		Action ret = null;

		// Explicit patches of the keyboard problems
		if (ignoreNextTyped) {
			if (key.isOnKeyRelease()) { // ignore releasing here
				ret = EMPTY_ACTION;
			}
			else { // either pressed or typed
				ignoreNextTyped = false;
			}
			if (key.getKeyChar() != 0 && key.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
				ret = EMPTY_ACTION; // prevent using defaultAction
			}
		}

		if (ret == null) {
			ret = getActionImpl(key);
			if (ret != EMPTY_ACTION) { // key that should be ignored
				if (!(ret instanceof KeymapSetContextAction)) {
					if (context != null) {
						ignoreNextTyped = true;
					}
					else if ( // Explicit patch for the keyTyped sent after
								// Alt+key
					(key.getModifiers() & InputEvent.ALT_MASK) != 0 // Alt
																	// pressed
							&& (key.getModifiers() & InputEvent.CTRL_MASK) == 0 // Ctrl
																				// not
																				// pressed
					) {
						boolean patch = true;
						if (key.getKeyChar() == 0 || key.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
							switch (key.getKeyCode()) {
							case KeyEvent.VK_ALT: // don't patch single Alt
							case KeyEvent.VK_KANJI:
							case KeyEvent.VK_KATAKANA:
							case KeyEvent.VK_HIRAGANA:
							case KeyEvent.VK_JAPANESE_KATAKANA:
							case KeyEvent.VK_JAPANESE_HIRAGANA:
							case 0x0107: // KeyEvent.VK_INPUT_METHOD_ON_OFF:
											// - in 1.3 only
							case KeyEvent.VK_NUMPAD0: // Alt+NumPad keys
							case KeyEvent.VK_NUMPAD1:
							case KeyEvent.VK_NUMPAD2:
							case KeyEvent.VK_NUMPAD3:
							case KeyEvent.VK_NUMPAD4:
							case KeyEvent.VK_NUMPAD5:
							case KeyEvent.VK_NUMPAD6:
							case KeyEvent.VK_NUMPAD7:
							case KeyEvent.VK_NUMPAD8:
							case KeyEvent.VK_NUMPAD9:
								patch = false;
								break;
							}
						}

						if (patch) {
							ignoreNextTyped = true;
						}
					}
					else if ((key.getModifiers() & InputEvent.META_MASK) != 0) { // Explicit
																					// patch
																					// for
																					// the
																					// keyTyped
																					// sent
																					// after
																					// Meta+key
																					// for
																					// Mac
																					// OS X
						ignoreNextTyped = true;
					}
					resetContext(); // reset context when resolved
				}

				if (context != null && ret == null) { // no action found when
														// in context
					ret = contextKeyNotFoundAction;
				}
			}
		}

		// Explicit patch for Ctrl+Space - eliminating the additional KEY_TYPED
		// sent
		if (key == KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK)) {
			ignoreNextTyped = true;
		}

		/*
		 * System.out.println("key=" + key + ", keyChar=" +
		 * (int)key.getKeyChar() + ", keyCode=" + key.getKeyCode() + ",
		 * keyModifiers=" + key.getModifiers() // NOI18N + ", ignoreNextTyped=" +
		 * ignoreNextTyped + ", context=" + context // NOI18N + ", returning
		 * action=" + ((ret == EMPTY_ACTION) ? "EMPTY_ACTION" : ((ret == null) ?
		 * "null" : ((ret instanceof javax.swing.text.TextAction) // NOI18N ?
		 * ret.getValue(javax.swing.Action.NAME) : ret.getClass()))));
		 */

		return ret;
	}

	public KeyStroke[] getBoundKeyStrokes() {
		return (context != null) ? context.getBoundKeyStrokes() : delegate.getBoundKeyStrokes();
	}

	public Action[] getBoundActions() {
		return (context != null) ? context.getBoundActions() : delegate.getBoundActions();
	}

	public KeyStroke[] getKeyStrokesForAction(Action a) {
		return (context != null) ? context.getKeyStrokesForAction(a) : delegate.getKeyStrokesForAction(a);
	}

	public boolean isLocallyDefined(KeyStroke key) {
		return (context != null) ? context.isLocallyDefined(key) : delegate.isLocallyDefined(key);
	}

	public void addActionForKeyStroke(KeyStroke key, Action a) {
		if (context != null) {
			context.addActionForKeyStroke(key, a);
		}
		else {
			delegate.addActionForKeyStroke(key, a);
		}
	}

	public void removeKeyStrokeBinding(KeyStroke key) {
		if (context != null) {
			context.removeKeyStrokeBinding(key);
		}
		else {
			delegate.removeKeyStrokeBinding(key);
		}
	}

	public void removeBindings() {
		if (context != null) {
			context.removeBindings();
		}
		else {
			delegate.removeBindings();
		}
	}

	public Keymap getResolveParent() {
		return (context != null) ? context.getResolveParent() : delegate.getResolveParent();
	}

	public void setResolveParent(Keymap parent) {
		if (context != null) {
			context.setResolveParent(parent);
		}
		else {
			delegate.setResolveParent(parent);
		}
	}

	public String toString() {
		return "MK: name=" + getName(); // NOI18N
	}

	/** Internal class used to set the context */
	class KeymapSetContextAction extends AbstractAction {

		Keymap contextKeymap;

		static final long serialVersionUID = 1034848289049566148L;

		KeymapSetContextAction(Keymap contextKeymap) {
			this.contextKeymap = contextKeymap;
		}

		public void actionPerformed(ActionEvent evt) {
			setContext(contextKeymap);
		}

	}

}
