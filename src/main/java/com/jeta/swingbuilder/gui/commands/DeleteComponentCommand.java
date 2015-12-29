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

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.components.EmptyComponentFactory;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.forms.gui.form.StandardComponent;
import com.jeta.open.registry.JETARegistry;
import com.jeta.swingbuilder.gui.formmgr.AbstractFormManager;
import com.jgoodies.forms.layout.CellConstraints;

/**
 * Command that deletes a component from a view
 * 
 * @author Jeff Tassin
 */
public class DeleteComponentCommand extends FormUndoableEdit {
	/**
	 * The grid component to 'delete'
	 */
	private GridComponent m_gc;

	/**
	 * The constaints assigned to the component.
	 */
	private CellConstraints m_comp_constraints;

	/**
	 * Default constraints with a width/height = 1
	 */
	private CellConstraints m_default_constraints;

	/**
	 * An empty component we use to replace the 'deleted' grid component
	 */
	private GridComponent m_empty;

	/**
	 * ctor
	 */
	public DeleteComponentCommand(GridComponent gc, ComponentSource compsrc) {
		super(gc.getParentView().getParentForm());

		m_gc = gc;
		assert (getView() != null);

		m_comp_constraints = m_gc.getConstraints().createCellConstraints();
		m_default_constraints = new CellConstraints(m_gc.getColumn(), m_gc.getRow());

		try {
			EmptyComponentFactory factory = new EmptyComponentFactory(compsrc);
			m_empty = (StandardComponent) factory.createComponent("empty", getView());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		if (m_empty != null) {
			getView().replaceComponent(m_empty, m_gc);
			getView().setConstraints(m_empty, m_default_constraints);
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		if (m_empty != null) {
			if (m_gc instanceof FormComponent) {
				// must be re-registered
				AbstractFormManager fmgr = (AbstractFormManager) JETARegistry.lookup(AbstractFormManager.COMPONENT_ID);
				fmgr.registerForm((FormComponent) m_gc);
			}

			getView().replaceComponent(m_gc, m_empty);
			getView().setConstraints(m_gc, m_comp_constraints);
		}
	}

	public String toString() {
		return "DeleteComponentCommand   " + CommandUtils.getBeanDelegate(m_gc);
	}

}
