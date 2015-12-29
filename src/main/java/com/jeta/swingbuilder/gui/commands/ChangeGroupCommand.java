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

import javax.swing.undo.CannotRedoException;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.store.memento.FormGroupSet;

public class ChangeGroupCommand extends FormUndoableEdit {
	/**
	 * The new group to assign
	 */
	private Integer m_new_group;

	/**
	 * The currently assigned group
	 */
	private Integer m_old_group;

	/**
	 * The row or column that we are assigning to a group
	 */
	private int m_index;

	/**
	 * Set to true if we are assigning to a row group. Set to false for a column
	 * group.
	 */
	private boolean m_is_row;

	/**
	 * ctor
	 */
	public ChangeGroupCommand(FormComponent form, int newGroup, int oldGroup, int index, boolean row) {
		super(form);
		m_new_group = new Integer(newGroup);
		m_old_group = new Integer(oldGroup);
		m_index = index;
		m_is_row = row;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		if (m_is_row) {
			FormGroupSet group = getView().getRowGroups();
			group.removeAssignment(m_index);
			group.assignToGroup(m_new_group, m_index);
			getView().setRowGroups(group);
		}
		else {
			FormGroupSet group = getView().getColumnGroups();
			group.removeAssignment(m_index);
			group.assignToGroup(m_new_group, m_index);
			getView().setColumnGroups(group);
		}

	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		if (m_is_row) {
			FormGroupSet group = getView().getRowGroups();
			group.removeAssignment(m_index);
			group.assignToGroup(m_old_group, m_index);
			getView().setRowGroups(group);
		}
		else {
			FormGroupSet group = getView().getColumnGroups();
			group.removeAssignment(m_index);
			group.assignToGroup(m_old_group, m_index);
			getView().setColumnGroups(group);
		}
	}

	public String toString() {
		if (m_is_row) {
			return "ChangeGroupCommand  new_group: " + m_new_group + "   old_group: " + m_old_group + "  row: " + m_index;
		}
		else {
			return "ChangeGroupCommand  new_group: " + m_new_group + "   old_group: " + m_old_group + "  col: " + m_index;
		}
	}

}
