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

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.undo.CannotRedoException;

import com.jeta.forms.gui.form.FormComponent;

/**
 * Command that runs a group of commands as one
 * 
 * @author Jeff Tassin
 */
public class CompositeCommand extends FormUndoableEdit {
	private ArrayList m_commands = new ArrayList();

	/**
	 * ctor
	 */
	public CompositeCommand(FormComponent form, FormUndoableEdit cmd1, FormUndoableEdit cmd2) {
		super(form);
		if (cmd1 != null)
			m_commands.add(cmd1);

		if (cmd2 != null)
			m_commands.add(cmd2);
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void redo() throws CannotRedoException {
		super.redo();
		Iterator iter = m_commands.iterator();
		while (iter.hasNext()) {
			FormUndoableEdit cmd = (FormUndoableEdit) iter.next();
			cmd.redo();
		}
	}

	/**
	 * UndoableEdit implementation Override should begin with a call to super.
	 */
	public void undo() throws CannotRedoException {
		super.undo();
		for (int index = m_commands.size() - 1; index >= 0; index--) {
			FormUndoableEdit cmd = (FormUndoableEdit) m_commands.get(index);
			cmd.undo();
		}
	}

	public String toString() {
		return "CompositeCommand  newcomp:   size: " + m_commands.size();
	}

}
