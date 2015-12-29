/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.gui.commands;

import java.util.Collection;
import java.util.Iterator;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.EditorManager;
import com.jeta.swingbuilder.gui.formmgr.FormManagerDesignUtils;
import com.jeta.swingbuilder.gui.undo.EditorUndoManager;

public class CommandUtils {
	/**
	 * Safely returns the classname of the bean delegate associated with this
	 * grid component. Null is returned if there is no delegate associated with
	 * the component.
	 */
	public static String getBeanDelegate(GridComponent gc) {
		if (gc == null)
			return "null";

		Object comp = gc.getBeanDelegate();
		if (comp == null)
			return "null";

		return comp.getClass().getName();
	}

	/**
	 * Invokes an action for the first time
	 */
	public static void invoke(FormUndoableEdit edit, FormEditor editor) {
		if (editor == null)
			return;

		/** don't allow edits to top level form */
		FormComponent fc = edit.getForm();
		if (fc != null && fc.isTopLevelForm())
			return;

		/**
		 * Special case for SetPropertyCommand because we can get repeated
		 * commands for the same property value given the way the property
		 * editor works.
		 */
		if (edit instanceof SetPropertyCommand) {
			EditorUndoManager undomgr = editor.getUndoManager();
			if (edit.equals(undomgr.editToBeUndone()))
				return;
		}

		edit.redo();
		editor.editHappened(edit);

		/**
		 * Now, post the edit to all other editors that contain the form.
		 */
		EditorManager emgr = (EditorManager) JETARegistry.lookup(EditorManager.COMPONENT_ID);
		Collection editors = emgr.getEditors();
		Iterator iter = editors.iterator();
		while (iter.hasNext()) {
			FormEditor other_editor = (FormEditor) iter.next();
			if (other_editor != editor) {
				if (FormManagerDesignUtils.containsForm(other_editor.getTopParent(), edit.getFormId())) {
					other_editor.editHappened(edit);
				}
			}
		}
		emgr.updateModifiedStatus();
		editor.repaint();
	}

	/**
	 * Redoes and edit for the given form. If the redo is successful, it is
	 * propagated to all other editors that have the same form.
	 */
	public static FormUndoableEdit redoEdit(FormEditor editor) {
		if (editor == null)
			return null;

		EditorUndoManager undomgr = editor.getUndoManager();
		if (undomgr.canRedo()) {
			FormUndoableEdit edit = (FormUndoableEdit) undomgr.editToBeRedone();
			undomgr.redo();

			/**
			 * Now, post the edit to all other editors that contain the form.
			 */
			EditorManager emgr = (EditorManager) JETARegistry.lookup(EditorManager.COMPONENT_ID);
			Collection editors = emgr.getEditors();
			Iterator iter = editors.iterator();
			while (iter.hasNext()) {
				FormEditor other_editor = (FormEditor) iter.next();
				if (other_editor != editor) {

					undomgr = other_editor.getUndoManager();
					undomgr.redoHappened(editor, edit);
				}
			}
			emgr.updateModifiedStatus();
			return edit;
		}
		return null;
	}

	/**
	 * Undoes and edit for the given form. If the undo is successful, it is
	 * propagated to all other editors that have the same form.
	 */
	public static FormUndoableEdit undoEdit(FormEditor editor) {
		if (editor == null)
			return null;

		EditorUndoManager undomgr = editor.getUndoManager();
		if (undomgr.canUndo()) {
			FormUndoableEdit edit = (FormUndoableEdit) undomgr.editToBeUndone();
			undomgr.undo();

			/**
			 * Now, post the edit to all other editors that contain the form so
			 * they can add their undo managers can mark this edit as undone.
			 */
			EditorManager emgr = (EditorManager) JETARegistry.lookup(EditorManager.COMPONENT_ID);
			Collection editors = emgr.getEditors();
			Iterator iter = editors.iterator();
			while (iter.hasNext()) {
				FormEditor other_editor = (FormEditor) iter.next();
				if (other_editor != editor) {

					undomgr = other_editor.getUndoManager();
					undomgr.undoHappened(editor, edit);
				}
			}
			emgr.updateModifiedStatus();
			return edit;
		}
		return null;
	}
}
