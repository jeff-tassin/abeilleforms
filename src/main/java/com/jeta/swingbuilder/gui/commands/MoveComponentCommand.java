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

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.jeta.forms.gui.components.ComponentSource;
import com.jeta.forms.gui.form.FormComponent;
import com.jeta.forms.gui.form.GridComponent;
import com.jeta.swingbuilder.gui.editor.FormEditor;

/**
 * Command that moves a component from one location and replaces a component at
 * another location. Keep in mind that every cell in the designer view has a
 * component, so you would never have a case where your are moving to an empty
 * cell. Rather you would move and replace an EmptyComponent in that cell.
 * 
 * @author Jeff Tassin
 */
public class MoveComponentCommand extends FormUndoableEdit {
	/**
	 * A move command is really a Delete followed by a ReplaceCommand
	 */
	private DeleteComponentCommand m_delete_cmd;

	private ReplaceComponentCommand m_replace_cmd;

	private SetConstraintsCommand m_constraints_cmd;

	/**
	 * Non null if the target is an EmptyComponent.
	 */
	private GridComponent m_empty_comp;

	private FormComponent m_destform;

	private FormEditor m_editor;

	/**
	 * ctor
	 */
	public MoveComponentCommand(FormComponent destForm, GridComponent destCell, FormComponent sourceForm, GridComponent sourceCell, ComponentSource compSrc) {
		super(sourceForm);
		try {
			m_destform = destForm;
			m_editor = FormEditor.getEditor(destForm);
			m_delete_cmd = new DeleteComponentCommand(sourceCell, compSrc);
			m_replace_cmd = new ReplaceComponentCommand(sourceCell, destCell, destForm);
		} catch (Exception e) {

		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		if (m_delete_cmd != null) {
			m_delete_cmd.redo();
			m_replace_cmd.redo();
			/**
			 * We need to update the entire container hierarchy because it seems
			 * that nested forms can get confused when the delete and replace
			 * commands directly follow one another. This can be tested by
			 * running the replace command in a SwingUtilities.invokeLater
			 * event. It runs fine, but the screen has an annoying flicker.
			 * Walking up the container hierarchy and calling revalidate seems
			 * to fix the problem.
			 */
			Container c = m_destform.getParent();
			while (true) {
				if (c instanceof JComponent)
					((JComponent) c).revalidate();

				if (c instanceof FormEditor)
					break;

				c = c.getParent();
				if (c == null || c instanceof java.awt.Window)
					break;
			}
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotUndoException {
		super.undo();
		if (m_delete_cmd != null) {
			m_replace_cmd.undo();
			m_delete_cmd.undo();
		}
	}

	public String toString() {
		return "MoveComponentCommand     ";
	}

}
