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
import com.jeta.forms.store.properties.effects.PaintProperty;

public class SetCellBackgroundCommand extends FormUndoableEdit {
	private PaintProperty m_paint;
	private PaintProperty m_oldpaint;
	private int m_row;
	private int m_column;

	/**
	 * ctor
	 */
	public SetCellBackgroundCommand(FormComponent form, int column, int row, PaintProperty paint, PaintProperty oldpaint) {
		super(form);
		m_column = column;
		m_row = row;
		m_paint = paint;
		m_oldpaint = oldpaint;
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		getView().setPaintProperty(m_column, m_row, m_paint);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		getView().setPaintProperty(m_column, m_row, m_oldpaint);
	}

	public String toString() {
		return "SetCellBackground col: " + m_column + "    row: " + m_row;
	}

}
