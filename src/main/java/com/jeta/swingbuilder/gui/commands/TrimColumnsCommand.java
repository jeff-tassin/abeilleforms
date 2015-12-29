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

import java.awt.Component;
import java.util.LinkedList;

import javax.swing.undo.CannotRedoException;

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.GridView;

public class TrimColumnsCommand extends FormUndoableEdit {
	/**
	 * Factory used to create the default component for each cell in the new
	 * row.
	 * 
	 * @directed
	 */
	private ComponentSource m_compsrc;

	/**
	 * A list of DeleteColumnCommands
	 */
	private LinkedList m_delete_commands = new LinkedList();

	/**
	 * ctor
	 */
	public TrimColumnsCommand(FormComponent form, ComponentSource compsrc) {
		super(form);
		m_compsrc = compsrc;
	}

	/**
	 * UndoableEdit implementation
	 */
	public boolean canUndo() {
		return false;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		FormComponent form = getForm();
		GridView view = form.getChildView();
		// first, do left to right
		int col_count = view.getColumnCount();
		for (int index = 0; index < col_count; index++) {
			if (view.getColumnCount() == 1)
				break;

			boolean bempty = true;
			for (int row = 1; row <= view.getRowCount(); row++) {
				GridComponent gc = view.getGridComponent(1, row);
				Component bean_delegate = gc.getBeanDelegate();
				if (gc != null && (bean_delegate != null)) {
					bempty = false;
					// System.out.println( "TrimColumnsCommand found non-empty
					// column at 1: bean delegate: " + bean_delegate );
					break;
				}
			}

			if (bempty) {
				DeleteColumnCommand cmd = new DeleteColumnCommand(form, 1, m_compsrc);
				cmd.redo();
				m_delete_commands.add(cmd);
			}
			else {
				break;
			}
		}

		// now, do right to left
		// first, do left to right
		col_count = view.getColumnCount();
		for (int index = 0; index < col_count; index++) {
			if (view.getColumnCount() == 1)
				break;

			int col = view.getColumnCount();
			boolean bempty = true;
			for (int row = 1; row <= view.getRowCount(); row++) {
				GridComponent gc = view.getGridComponent(col, row);
				Component bean_delegate = gc.getBeanDelegate();
				if (gc != null && (bean_delegate != null)) {
					bempty = false;
					break;
				}
			}

			if (bempty) {
				DeleteColumnCommand cmd = new DeleteColumnCommand(form, col, m_compsrc);
				cmd.redo();
				m_delete_commands.add(cmd);
			}
			else {
				break;
			}
		}
	}

}
