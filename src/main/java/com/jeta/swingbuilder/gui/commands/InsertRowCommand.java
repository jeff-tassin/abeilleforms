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

import com.jeta.forms.gui.components.ComponentFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jgoodies.forms.layout.RowSpec;

public class InsertRowCommand extends FormUndoableEdit {

	/**
	 * @directed
	 */
	private RowSpec m_rowspec;

	/**
	 * The index of the row to insert
	 */
	private int m_row;

	/**
	 * Factory used to create the default component for each cell in the new
	 * row.
	 * 
	 * @directed
	 */
	private ComponentFactory m_compfactory;

	/**
	 * ctor
	 */
	public InsertRowCommand(FormComponent form, int row, RowSpec rowspec, ComponentFactory factory) {
		super(form);
		m_row = row;
		m_rowspec = rowspec;
		m_compfactory = factory;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		try {
			getView().insertRow(m_row, m_rowspec, m_compfactory);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		try {
			getView().removeRow(m_row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return "InsertRowCommand  row: " + m_row;
	}

}
