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

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.EmptyComponentFactory;
import com.jeta.forms.gui.form.ComponentInfo;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;
import com.jeta.forms.store.memento.FormGroupSet;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Command that deletes a row from a view.
 * 
 * @author Jeff Tassin
 */
public class DeleteRowCommand extends FormUndoableEdit {
	/**
	 * The component source
	 */
	private ComponentSource m_compsrc;

	/**
	 * The row to delete
	 */
	private int m_row;

	/**
	 * A list of components and their associated cell constraints (ComponentInfo
	 * objects)
	 */
	private LinkedList m_components = new LinkedList();

	/**
	 * The row spec of the row we deleted
	 */
	private RowSpec m_oldspec;

	private ChangeGroupCommand m_change_group_cmd = null;

	/**
	 * ctor
	 */
	public DeleteRowCommand(FormComponent form, int row, ComponentSource compsrc) {
		super(form);
		m_compsrc = compsrc;
		m_row = row;

		/** set the group to zero before deleting the row */
		GridView view = getView();
		FormGroupSet gset = view.getRowGroups();
		Integer id = gset.getGroupId(row);
		int group_id = (id == null ? 0 : id.intValue());
		if (group_id > 0) {
			m_change_group_cmd = new ChangeGroupCommand(form, 0, group_id, row, true);
		}

	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();

		if (m_oldspec == null) {
			try {
				if (m_change_group_cmd != null)
					m_change_group_cmd.redo();
			} catch (Exception e) {
				e.printStackTrace();
			}

			m_oldspec = getView().getRowSpec(m_row);
			assert (m_oldspec != null);
			m_components.clear();
			for (int col = 1; col <= getView().getColumnCount(); col++) {
				GridComponent gc = getView().getGridComponent(col, m_row);
				assert (gc != null);
				CellConstraints cc = gc.getConstraints().createCellConstraints();
				ComponentInfo info = new ComponentInfo(gc, cc);
				m_components.add(info);
			}
			getView().removeRow(m_row);
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotUndoException {
		super.undo();

		try {
			getView().insertRow(m_row, m_oldspec, new EmptyComponentFactory(m_compsrc));
			Iterator iter = m_components.iterator();
			while (iter.hasNext()) {
				ComponentInfo info = (ComponentInfo) iter.next();
				assert (info.getRow() == m_row);
				int col = info.getColumn();
				int row = info.getRow();
				GridComponent oldcomp = getView().getGridComponent(col, row);
				getView().replaceComponent(info.getGridComponent(), oldcomp);
				getView().setConstraints(info.getGridComponent(), info.getCellConstraints());
			}

			if (m_change_group_cmd != null)
				m_change_group_cmd.undo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return "DeleteRowCommand  row = " + String.valueOf(m_row);
	}

}
