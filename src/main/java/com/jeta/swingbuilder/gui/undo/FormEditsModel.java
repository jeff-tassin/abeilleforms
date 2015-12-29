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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import com.jeta.swingbuilder.gui.commands.FormUndoableEdit;

/**
 * This class is a table model for the UndoManagerView. It gets the list of
 * forms from the FormManager and stores them in this model. This is mainly used
 * to help debugging.
 * 
 * @author Jeff Tassin
 */
public class FormEditsModel extends AbstractTableModel {
	/** an array of Trigger object */
	private ArrayList m_data;

	/** an array of column names for the table */
	private String[] m_colnames;

	/** an array of column types for the table */
	private Class[] m_coltypes;

	/** column definitions */
	static final int CAN_UNDO_COLUMN = 0;
	static final int CAN_REDO_COLUMN = 1;
	static final int EDIT_NAME_COLUMN = 2;

	/**
	 * ctor.
	 */
	public FormEditsModel(EditorUndoManager undomgr) {
		super();

		m_data = new ArrayList();

		String[] values = { "CanUndo", "CanRedo", "Form UndoableEdit" };

		m_colnames = values;

		Class[] types = { Boolean.class, Boolean.class, String.class };
		m_coltypes = types;
		reload(undomgr);
	}

	/**
	 * Adds the given trigger object to the table
	 */
	public void addRow(FormUndoableEdit edit) {
		if (m_data == null)
			m_data = new ArrayList();

		m_data.add(edit);
		fireTableRowsInserted(m_data.size() - 1, m_data.size() - 1);
	}

	/**
	 * @return the number of columns in this model
	 */
	public int getColumnCount() {
		return m_colnames.length;
	}

	/**
	 * @return the number of rows objects in this model
	 */
	public int getRowCount() {
		return m_data.size();
	}

	/**
	 * @return the name of a column at a given index
	 */
	public String getColumnName(int column) {
		return m_colnames[column];
	}

	/**
	 * @return the type of column at a given index
	 */
	public Class getColumnClass(int column) {
		return m_coltypes[column];
	}

	/**
	 * @return the object at the given row in the model
	 */
	public FormUndoableEdit getRow(int row) {
		if (row >= 0 && row < m_data.size())
			return (FormUndoableEdit) m_data.get(row);
		else
			return null;
	}

	/**
	 * @return the column value at the given row
	 */
	public Object getValueAt(int row, int column) {
		/** "Edit" */
		FormUndoableEdit edit = getRow(row);
		if (edit == null) {
			return null;
		}
		else if (column == CAN_UNDO_COLUMN) {
			return Boolean.valueOf(edit.canUndo());
		}
		else if (column == CAN_REDO_COLUMN) {
			return Boolean.valueOf(edit.canRedo());
		}
		else if (column == EDIT_NAME_COLUMN) {
			return edit.toString();
		}
		else
			return null;
	}

	/**
	 * Reload the model
	 */
	public void reload(EditorUndoManager undomgr) {
		System.out.println("FormEditsModel.indexOfNextAdd: " + undomgr.getIndexOfNextAdd());
		removeAll();
		Collection edits = undomgr.getEdits();
		Iterator iter = edits.iterator();
		while (iter.hasNext()) {
			FormUndoableEdit edit = (FormUndoableEdit) iter.next();
			addRow(edit);
		}
		addRow(null);
	}

	/**
	 * Remove all data items from this model
	 */
	public void removeAll() {
		m_data.clear();
	}

}
