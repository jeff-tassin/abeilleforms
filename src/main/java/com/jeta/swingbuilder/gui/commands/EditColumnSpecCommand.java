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
import com.jgoodies.forms.layout.ColumnSpec;

public class EditColumnSpecCommand extends FormUndoableEdit {
	/**
	 * @directed
	 */
	private ColumnSpec m_columnspec;

	private ColumnSpec m_oldspec;

	/**
	 * The index of the column whose specification we are changing
	 */
	private int m_col;

	/**
	 * ctor
	 */
	public EditColumnSpecCommand(FormComponent form, int col, ColumnSpec colspec, ColumnSpec oldspec) {
		super(form);
		m_col = col;
		m_columnspec = colspec;
		/**
		 * note that you must create a copy of the oldspec here because the
		 * reference passed to the constructor is probabaly bound to the view
		 * and will change
		 */
		m_oldspec = new ColumnSpec(oldspec.getDefaultAlignment(), oldspec.getSize(), oldspec.getResizeWeight());
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		getView().setColumnSpec(m_col, m_columnspec);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		if (m_oldspec != null)
			getView().setColumnSpec(m_col, m_oldspec);
	}

	public String toString() {
		return "EditColumnSpec  column: " + m_col + "   spec: " + m_columnspec.toString();
	}

}
