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

import com.jeta.forms.gui.form.ComponentConstraints;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.ReadOnlyConstraints;

/**
 * Command that sets the CellConstraints for a component in the view
 * 
 * @author Jeff Tassin
 */
public class SetConstraintsCommand extends FormUndoableEdit {
	/**
	 * The grid component whose constraints we will set
	 */
	private GridComponent m_gc;

	/**
	 * The cell constraints to set
	 */
	private ComponentConstraints m_cc;

	/**
	 * The old constraints.
	 */
	private ComponentConstraints m_old_cc;

	/**
	 * ctor
	 */
	public SetConstraintsCommand(FormComponent form, GridComponent gc, ComponentConstraints cc) {
		super(form);
		m_gc = gc;
		m_cc = cc;
		m_old_cc = new ReadOnlyConstraints(gc.getConstraints());
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		getView().setConstraints(m_gc, m_cc);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		getView().setConstraints(m_gc, m_old_cc);
	}

	public String toString() {
		return "SetConstraintsCommand    col: " + m_gc.getColumn() + "  row: " + m_gc.getRow() + "  constraints: " + m_cc.createCellConstraints().toString();
	}
}
