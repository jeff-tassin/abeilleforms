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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import com.jeta.swingbuilder.gui.commands.FormUndoableEdit;
import com.jeta.swingbuilder.gui.editor.FormEditor;
import com.jeta.swingbuilder.gui.formmgr.AbstractFormManager;

/**
 * This is the UndoManager for an editor.
 * 
 * @author Jeff Tassin
 */
public class EditorUndoManager extends CompoundEdit {
	/**
	 * @directed
	 */
	private FormEditor m_editor;

	/**
	 * Keeps track of undo managers for each form.
	 */
	private AbstractFormManager m_formmgr;

	private int m_indexOfNextAdd;
	private int m_limit;

	/**
	 * ctor
	 */
	public EditorUndoManager(AbstractFormManager formmgr, FormEditor editor) {
		m_formmgr = formmgr;
		m_editor = editor;

		m_indexOfNextAdd = 0;
		m_limit = 100;
		edits.ensureCapacity(m_limit);
	}

	private boolean equals(FormUndoableEdit edit1, FormUndoableEdit edit2) {
		if (edit1 instanceof UndoableEditProxy) {
			if (edit2 instanceof UndoableEditProxy) {
				return ((UndoableEditProxy) edit1).getDelegate() == ((UndoableEditProxy) edit2).getDelegate();
			}
			else {
				return ((UndoableEditProxy) edit1).getDelegate() == edit2;
			}
		}
		else if (edit2 instanceof UndoableEditProxy) {
			UndoableEditProxy proxy = (UndoableEditProxy) edit2;
			return proxy.getDelegate() == edit1;
		}
		else {
			return edit1 == edit2;
		}
	}

	/**
	 * @return the number of edits in the queue
	 */
	public int size() {
		return edits.size();
	}

	/**
	 * This is for testing only
	 */
	public Collection getEdits() {
		return edits;
	}

	/**
	 * This should only be called during testing
	 */
	public int getIndexOfNextAdd() {
		return m_indexOfNextAdd;
	}

	/**
	 * Notifies this manager that an undo edit happened on another editor.
	 */
	public void undoHappened(FormEditor editor, FormUndoableEdit edit) {
		if (equals(edit, (FormUndoableEdit) editToBeUndone())) {
			undo(false);
		}
		else {
			/**
			 * We need to lock all edits with the same form id because we are
			 * not out of sync with the other editor. This can happen if we
			 * delete a nested form with edits from this editor and edit that
			 * form in aonther edit.
			 */
			Collection edits = getEdits();
			Iterator iter = edits.iterator();
			while (iter.hasNext()) {
				UndoableEditProxy myedit = (UndoableEditProxy) iter.next();
				if (edit.getFormId().equals(myedit.getFormId())) {
					myedit.lock(true);
				}
			}
		}
	}

	/**
	 * Notifies this manager that a redo edit happened on another editor.
	 */
	public void redoHappened(FormEditor editor, FormUndoableEdit edit) {
		if (equals(edit, (FormUndoableEdit) editToBeRedone())) {
			redo(false);
		}
		else {
			/**
			 * We need to lock all edits with the same form id because we are
			 * not out of sync with the other editor. This can happen if we
			 * delete a nested form with edits from this editor and edit that
			 * form in aonther edit.
			 */
			Collection edits = getEdits();
			Iterator iter = edits.iterator();
			while (iter.hasNext()) {
				UndoableEditProxy myedit = (UndoableEditProxy) iter.next();
				if (edit.getFormId().equals(myedit.getFormId())) {
					myedit.lock(true);
				}
			}
		}
	}

	public synchronized void discardAllEdits() {
		Enumeration cursor = edits.elements();
		while (cursor.hasMoreElements()) {
			UndoableEdit e = (UndoableEdit) cursor.nextElement();
			e.die();
		}
		edits = new Vector(m_limit);
		m_indexOfNextAdd = 0;
	}

	protected void trimForLimit() {
		if (m_limit > 0) {
			int size = edits.size();
			if (size > m_limit) {
				int halfLimit = m_limit / 2;
				int keepFrom = m_indexOfNextAdd - 1 - halfLimit;
				int keepTo = m_indexOfNextAdd - 1 + halfLimit;

				if (keepTo - keepFrom + 1 > m_limit) {
					keepFrom++;
				}

				if (keepFrom < 0) {
					keepTo -= keepFrom;
					keepFrom = 0;
				}
				if (keepTo >= size) {
					int delta = size - keepTo - 1;
					keepTo += delta;
					keepFrom += delta;
				}

				trimEdits(keepTo + 1, size - 1);
				trimEdits(0, keepFrom - 1);
			}
		}
	}

	protected void trimEdits(int from, int to) {
		if (from <= to) {
			for (int i = to; from <= i; i--) {
				UndoableEdit e = (UndoableEdit) edits.elementAt(i);
				e.die();
				edits.removeElementAt(i);
			}

			if (m_indexOfNextAdd > to) {
				m_indexOfNextAdd -= to - from + 1;
			}
			else if (m_indexOfNextAdd >= from) {
				m_indexOfNextAdd = from;
			}
		}
	}

	public UndoableEdit editToBeUndone() {
		int i = m_indexOfNextAdd;
		while (i > 0) {
			UndoableEdit edit = (UndoableEdit) edits.elementAt(--i);
			if (edit.isSignificant()) {
				return edit;
			}
		}
		return null;
	}

	public UndoableEdit editToBeRedone() {
		int count = edits.size();
		int i = m_indexOfNextAdd;

		while (i < count) {
			UndoableEdit edit = (UndoableEdit) edits.elementAt(i++);
			if (edit.isSignificant()) {
				return edit;
			}
		}

		return null;
	}

	protected void undoTo(UndoableEdit edit, boolean shouldUndo) throws CannotUndoException {
		boolean done = false;
		while (!done) {
			UndoableEdit next = (UndoableEdit) edits.elementAt(--m_indexOfNextAdd);
			if (shouldUndo)
				next.undo();

			done = next == edit;
		}
	}

	protected void redoTo(UndoableEdit edit, boolean shouldRedo) throws CannotRedoException {
		boolean done = false;
		while (!done) {
			UndoableEdit next = (UndoableEdit) edits.elementAt(m_indexOfNextAdd++);
			if (shouldRedo)
				next.redo();
			done = next == edit;
		}
	}

	public synchronized void undoOrRedo() throws CannotRedoException, CannotUndoException {
		if (m_indexOfNextAdd == edits.size()) {
			undo();
		}
		else {
			redo();
		}
	}

	public synchronized boolean canUndoOrRedo() {
		if (m_indexOfNextAdd == edits.size()) {
			return canUndo();
		}
		else {
			return canRedo();
		}
	}

	public synchronized void undo() throws CannotUndoException {
		undo(true);
	}

	/**
	 * Undoes the current edit.
	 * 
	 * @param shouldUndo
	 *            flag that indicates if the undo on the edit should be invoked.
	 *            If false, this operation only changes the indexOfNextAdd
	 *            pointer.
	 */
	public synchronized void undo(boolean shouldUndo) throws CannotUndoException {
		if (isInProgress()) {
			UndoableEdit edit = editToBeUndone();
			if (edit == null) {
				throw new CannotUndoException();
			}
			undoTo(edit, shouldUndo);
		}
		else {
			super.undo();
		}
	}

	public synchronized boolean canUndo() {
		if (isInProgress()) {
			UndoableEdit edit = editToBeUndone();
			return edit != null && edit.canUndo();
		}
		else {
			return super.canUndo();
		}
	}

	public synchronized void redo() throws CannotRedoException {
		redo(true);
	}

	/**
	 * Redoes the current edit.
	 * 
	 * @param shouldRedo
	 *            flag that indicates if the redo on the edit should be invoked.
	 *            If false, this operation only changes the indexOfNextAdd
	 *            pointer.
	 */
	public synchronized void redo(boolean shouldRedo) throws CannotRedoException {
		if (isInProgress()) {
			UndoableEdit edit = editToBeRedone();
			if (edit == null) {
				throw new CannotRedoException();
			}
			redoTo(edit, shouldRedo);
		}
		else {
			super.redo();
		}
	}

	public synchronized boolean canRedo() {
		if (isInProgress()) {
			UndoableEdit edit = editToBeRedone();
			return edit != null && edit.canRedo();
		}
		else {
			return super.canRedo();
		}
	}

	public synchronized boolean addEdit(UndoableEdit anEdit) {
		assert (!(anEdit instanceof UndoableEditProxy));
		boolean retVal;

		trimEdits(m_indexOfNextAdd, edits.size() - 1);

		retVal = super.addEdit(new UndoableEditProxy((FormUndoableEdit) anEdit));
		if (isInProgress()) {
			retVal = true;
		}

		m_indexOfNextAdd = edits.size();
		trimForLimit();

		return retVal;
	}

	public synchronized void end() {
		super.end();
		trimEdits(m_indexOfNextAdd, edits.size() - 1);
	}

}
