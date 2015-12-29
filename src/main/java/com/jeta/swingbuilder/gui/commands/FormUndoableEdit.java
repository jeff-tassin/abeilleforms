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

import javax.swing.undo.AbstractUndoableEdit;

import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridView;

/**
 * Represents an edit on a given form.
 * 
 * @author Jeff Tassin
 */
public abstract class FormUndoableEdit extends AbstractUndoableEdit implements Cloneable {
	/**
	 * The form associated with this edit.
	 */
	private FormComponent m_form;

	/**
	 * ctor
	 */
	public FormUndoableEdit(FormComponent fc) {
		m_form = fc;
	}

	/**
	 * UndoableEdit implementation
	 */
	public boolean canRedo() {
		return true;
	}

	/**
	 * UndoableEdit implementation
	 */
	public boolean canUndo() {
		return true;
	}

	/**
	 * @return the form associated with this edit.
	 */
	public FormComponent getForm() {
		return m_form;
	}

	/**
	 * @return the id of form associated with this edit.
	 */
	public String getFormId() {
		return m_form.getId();
	}

	/**
	 * @return the child view associated with this form.
	 */
	public GridView getView() {
		return m_form.getChildView();
	}

}
