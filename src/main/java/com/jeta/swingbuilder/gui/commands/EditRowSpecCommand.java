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
import com.jgoodies.forms.layout.RowSpec;

public class EditRowSpecCommand extends FormUndoableEdit {
	/**
	 * @directed
	 */
	private RowSpec m_rowspec;

	private RowSpec m_oldspec;

	/**
	 * The index of the row to insert
	 */
	private int m_row;

	/**
	 * ctor
	 */
	public EditRowSpecCommand(FormComponent form, int row, RowSpec rowspec, RowSpec oldspec) {
		super(form);
		m_row = row;
		m_rowspec = rowspec;
		/**
		 * note that you must create a copy of the oldspec here because the
		 * reference passed to the constructor is probabaly bound to the view
		 * and will change
		 */
		m_oldspec = new RowSpec(oldspec.getDefaultAlignment(), oldspec.getSize(), oldspec.getResizeWeight());
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		getView().setRowSpec(m_row, m_rowspec);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		if (m_oldspec != null)
			getView().setRowSpec(m_row, m_oldspec);
	}

	public String toString() {
		return "EditRowSpec  row: " + m_row + "   spec: " + m_rowspec.toString();
	}

}
