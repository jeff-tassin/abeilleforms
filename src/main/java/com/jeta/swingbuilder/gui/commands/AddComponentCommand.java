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
import javax.swing.undo.CannotUndoException;

import com.jeta.forms.gui.form.ComponentConstraints;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;

/**
 * Command that adds a component to a view
 * 
 * @author Jeff Tassin
 */
public class AddComponentCommand extends FormUndoableEdit {

	/** @link dependency */
	/* # com.jeta.forms.gui.form.GridComponent lnkGridComponent; */

	/**
	 * @directed
	 */
	private GridComponent m_comp;

	/**
	 * The cell constraints for the given component
	 */
	private ComponentConstraints m_cc;

	/**
	 * The component we replaced.
	 */
	private GridComponent m_oldcomp;

	/**
	 * The cell constraints for the component we replaced
	 */
	private ComponentConstraints m_oldcc;

	/**
	 * Adds a new component to a form.
	 */
	public AddComponentCommand(FormComponent fc, GridComponent gc, ComponentConstraints cc) {
		super(fc);
		m_comp = gc;
		m_cc = cc;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();

		if (m_oldcomp == null) {
			m_oldcomp = getView().getGridComponent(m_cc.getColumn(), m_cc.getRow());
			assert (m_oldcomp != null);
			m_oldcc = (ComponentConstraints) m_oldcomp.getConstraints().clone();
		}

		getView().setComponent(m_comp, m_cc);
		m_comp.setSelected(true);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotUndoException {
		super.undo();

		if (m_oldcomp != null) {
			getView().setComponent(m_oldcomp, m_oldcc);
			m_oldcomp.setSelected(true);
		}
	}

	public String toString() {
		return "AddComponentCommand     " + CommandUtils.getBeanDelegate(m_comp);
	}

}
