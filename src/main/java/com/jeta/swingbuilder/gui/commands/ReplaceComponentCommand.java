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
import com.jeta.forms.gui.form.GridComponent;

/**
 * Command that replaces a component in a view
 * 
 * @author Jeff Tassin
 */
public class ReplaceComponentCommand extends FormUndoableEdit {
	/**
	 * The new grid component to insert
	 */
	private GridComponent m_newcomp;

	/**
	 * The old grid component to replace
	 */
	private GridComponent m_oldcomp;

	/**
	 * ctor
	 */
	public ReplaceComponentCommand(GridComponent newcomp, GridComponent oldcomp, FormComponent form) {
		super(form);
		m_newcomp = newcomp;
		m_oldcomp = oldcomp;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		getView().replaceComponent(m_newcomp, m_oldcomp);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		getView().replaceComponent(m_oldcomp, m_newcomp);
	}

	public String toString() {
		return "ReplaceComponentCommand  newcomp: " + CommandUtils.getBeanDelegate(m_newcomp) + "    oldcomp: " + CommandUtils.getBeanDelegate(m_oldcomp);
	}

}
