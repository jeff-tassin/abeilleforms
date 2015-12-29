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

package com.jeta.swingbuilder.gui.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.swingbuilder.gui.commands.FormUndoableEdit;

/**
 * This class acts as a front end to a FormUndoableEdit. It behaves in exactly
 * the same way as the delegate however, we add the concept of a locked state.
 * Since we share edit objects between opened editors, we can put the locked
 * flag in the delegate.
 * 
 * We need to lock an edit when we are editing a form in two different editors.
 * This can happen if the form is nested. If the undo managers for the editors
 * get out of sync (which can easily happen) then we need to lock the edits that
 * are out of sync.
 * 
 * @author Jeff Tassin
 */
public class UndoableEditProxy extends FormUndoableEdit {
	/**
	 * Flag that indicates if this edit has been locked. This means it cannot be
	 * redone/undone regardless of the canRedo/canUndo result of the delegate
	 */
	private boolean m_locked;

	/**
	 * The delegate we are wrapping
	 */
	private FormUndoableEdit m_delegate;

	/**
	 * ctor
	 */
	public UndoableEditProxy(FormUndoableEdit delegate) {
		super(delegate.getForm());
		assert (!(delegate instanceof UndoableEditProxy));
		m_delegate = delegate;
	}

	/**
	 * UndoableEdit implementation
	 */
	public boolean canRedo() {
		if (m_locked)
			return false;
		else
			return m_delegate.canRedo();
	}

	/**
	 * UndoableEdit implementation
	 */
	public boolean canUndo() {
		if (m_locked)
			return false;
		else
			return m_delegate.canUndo();
	}

	public FormUndoableEdit getDelegate() {
		return m_delegate;
	}

	/**
	 * @return the form associated with this edit.
	 */
	public FormComponent getForm() {
		return m_delegate.getForm();
	}

	/**
	 * @return the id of form associated with this edit.
	 */
	public String getFormId() {
		return m_delegate.getFormId();
	}

	/**
	 * @return the child view associated with this form.
	 */
	public GridView getView() {
		return m_delegate.getView();
	}

	/**
	 * Locks this edit so that it cannot be undone/redone.
	 */
	public void lock(boolean lock) {
		m_locked = lock;
	}

	public void die() {
		m_delegate.die();
	}

	public void undo() throws CannotUndoException {
		m_delegate.undo();
	}

	public void redo() throws CannotRedoException {
		m_delegate.redo();
	}

	public boolean addEdit(UndoableEdit anEdit) {
		return m_delegate.addEdit(anEdit);
	}

	public boolean replaceEdit(UndoableEdit anEdit) {
		return m_delegate.replaceEdit(anEdit);
	}

	public boolean isSignificant() {
		return m_delegate.isSignificant();
	}

	public String toString() {
		return m_delegate.toString();
	}
}
